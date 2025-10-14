package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.*;
import co.edu.unbosque.foresta.integration.*;
import co.edu.unbosque.foresta.integration.DTO.*;
import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.model.entity.Contrato;
import co.edu.unbosque.foresta.model.enums.EstadoContratoEnum;
import co.edu.unbosque.foresta.model.enums.MonedaEnum;
import co.edu.unbosque.foresta.repository.IContratoRepository;
import co.edu.unbosque.foresta.service.interfaces.IContratoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContratoServiceImpl implements IContratoService {

    private final IContratoRepository repo;
    private final InversionistasClient invClient;
    private final ComisionistasClient comClient;
    private final ModelMapper mm;

    @Value("${contratos.porcentaje.default:2.50}")
    private BigDecimal porcentajeDefault;

    @Value("${contratos.terminos.text:}")
    private String terminosTexto;

    public ContratoServiceImpl(IContratoRepository repo, InversionistasClient invClient, ComisionistasClient comClient, ModelMapper mm) {
        this.repo = repo;
        this.invClient = invClient;
        this.comClient = comClient;
        this.mm = mm;
    }

    @Transactional
    @Override
    public ContratoDTO registrar(ContratoRegistroRequestDTO req) {
        validarRequest(req);
        InversionistaPerfilDTO inv = getInversionistaAutenticado();
        ComisionistaPerfilDTO comPerfil = obtenerComisionista(req.getComisionistaId());
        asegurarNoExisteContratoActivo(inv.getId());
        Contrato entidad = construirContratoRegistro(req, inv, comPerfil);
        Contrato guardado = repo.save(entidad);
        return mapear(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoDTO obtenerMiContratoActivo() {
        InversionistaPerfilDTO inv = getInversionistaAutenticado();
        Contrato contrato = obtenerContratoActivo(inv.getId());
        return mapear(contrato);
    }

    @Override
    @Transactional
    public ContratoDTO cancelarMiContratoActivo() {
        InversionistaPerfilDTO inv = getInversionistaAutenticado();
        Contrato contrato = obtenerContratoActivo(inv.getId());
        contrato.setEstado(EstadoContratoEnum.TERMINADO);
        contrato.setFechaFin(LocalDateTime.now());
        Contrato actualizado = repo.save(contrato);
        return mapear(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratoDTO> listarMisContratosComisionistaAutenticado() {
        ComisionistaPerfilDTO comi = getComisionistaAutenticado();
        List<Contrato> contratos = repo.findByComisionistaIdOrderByFechaInicioDesc(comi.getId());
        return contratos.stream().map(c -> mapearParaListadoComisionista(c, comi)).toList();
    }

    private InversionistaPerfilDTO getInversionistaAutenticado() {
        InversionistaPerfilDTO inv = invClient.miPerfil();
        if (inv == null || inv.getId() == null) throw new NotFoundException("Perfil de inversionista no encontrado");
        return inv;
    }

    private ComisionistaPerfilDTO getComisionistaAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new NotFoundException("No autenticado");
        ComisionistaPerfilDTO comi = comClient.miPerfil();
        if (comi == null || comi.getId() == null) throw new NotFoundException("Perfil de comisionista no encontrado");
        return comi;
    }

    private void asegurarNoExisteContratoActivo(Long inversionistaId) {
        if (repo.existsByInversionistaIdAndEstado(inversionistaId, EstadoContratoEnum.ACTIVO)) {
            throw new ConflictException("Ya existe un contrato ACTIVO para este inversionista");
        }
    }

    private Contrato construirContratoRegistro(ContratoRegistroRequestDTO req, InversionistaPerfilDTO inv, ComisionistaPerfilDTO comPerfil) {
        Contrato c = new Contrato();
        c.setInversionistaId(inv.getId());
        c.setComisionistaId(comPerfil.getId());
        c.setEstado(EstadoContratoEnum.ACTIVO);
        c.setMoneda(validarMoneda(req.getMoneda()));
        c.setPorcentajeCobroAplicado(porcentajeDefault);
        c.setTerminosTexto(
                (terminosTexto == null || terminosTexto.isBlank()) ? null
                        : terminosTexto
                        .replace("{{PORCENTAJE}}", porcentajeDefault.toPlainString())
                        .replace("{{MONEDA}}", req.getMoneda().toUpperCase())
        );
        c.setObservaciones(trim(req.getObservaciones()));
        return c;
    }

    private Contrato obtenerContratoActivo(Long inversionistaId) {
        return repo.findFirstByInversionistaIdAndEstadoOrderByCreadoEnDesc(
                inversionistaId, EstadoContratoEnum.ACTIVO
        ).orElseThrow(() -> new NotFoundException("No tienes un contrato activo"));
    }

    private ContratoDTO mapear(Contrato entidad) {
        ContratoDTO dto = mm.map(entidad, ContratoDTO.class);
        enriquecerNombresYDocs(dto);
        return dto;
    }

    private ContratoDTO mapearParaListadoComisionista(Contrato c, ComisionistaPerfilDTO comi) {
        ContratoDTO dto = mm.map(c, ContratoDTO.class);
        dto.setComisionistaId(comi.getId());
        dto.setComisionistaNombreCompleto(nombreCompleto(comi.getNombre(), comi.getApellido()));
        try {
            InversionistaPerfilDTO inv = invClient.obtenerPorId(c.getInversionistaId());
            if (inv != null) {
                dto.setInversionistaId(inv.getId());
                dto.setInversionistaNombreCompleto(nombreCompleto(inv.getNombre(), inv.getApellido()));
                dto.setInversionistaDocumento(inv.getNumeroDocumento());
            }
        } catch (Exception ignored) {}
        return dto;
    }

    private String nombreCompleto(String n, String a) {
        String nombre = n != null ? n.trim() : "";
        String ape = a != null ? a.trim() : "";
        return (nombre + " " + ape).trim();
    }

    private void validarRequest(ContratoRegistroRequestDTO r) {
        if (r == null) throw new BadRequestException("Body requerido");
        if (r.getComisionistaId() == null) throw new BadRequestException("comisionistaId es obligatorio");
        if (vacio(r.getMoneda())) throw new BadRequestException("moneda es obligatoria");
        if (!Boolean.TRUE.equals(r.getAceptaTerminos())) throw new BadRequestException("Debe aceptar los términos del contrato");
    }

    private String validarMoneda(String m) {
        try {
            MonedaEnum.valueOf(m.trim().toUpperCase());
            return m.trim().toUpperCase();
        } catch (Exception e) {
            throw new BadRequestException("Moneda inválida. Use COP, VES, USD o PEN");
        }
    }

    private ComisionistaPerfilDTO obtenerComisionista(Long id) {
        try {
            ComisionistaPerfilDTO c = comClient.obtenerPorId(id);
            if (c == null || c.getId() == null) throw new NotFoundException("Comisionista no encontrado");
            return c;
        } catch (feign.FeignException.NotFound e) {
            throw new NotFoundException("Comisionista no encontrado");
        } catch (feign.FeignException e) {
            throw new BadRequestException("Error consultando comisionista: " + e.status());
        }
    }

    private void enriquecerNombresYDocs(ContratoDTO dto) {
        try {
            InversionistaPerfilDTO invDet = invClient.obtenerPorId(dto.getInversionistaId());
            if (invDet != null) {
                dto.setInversionistaNombreCompleto(
                        safe(invDet.getNombre()) + " " + safe(invDet.getApellido())
                );
                dto.setInversionistaDocumento(
                        safe(invDet.getTipoDocumento()) + " " + safe(invDet.getNumeroDocumento())
                );
            } else {
                dto.setInversionistaNombreCompleto("No disponible");
                dto.setInversionistaDocumento("-");
            }
        } catch (Exception ex) {
            dto.setInversionistaNombreCompleto("No disponible");
            dto.setInversionistaDocumento("-");
        }

        try {
            ComisionistaPerfilDTO comDet = comClient.obtenerPorId(dto.getComisionistaId());
            if (comDet != null) {
                dto.setComisionistaNombreCompleto(
                        safe(comDet.getNombre()) + " " + safe(comDet.getApellido())
                );
            } else {
                dto.setComisionistaNombreCompleto("No disponible");
            }
        } catch (Exception ex) {
            dto.setComisionistaNombreCompleto("No disponible");
        }
    }

    private static boolean vacio(String s){ return s == null || s.isBlank(); }
    private static String trim(String s){ return s == null ? null : s.trim(); }
    private static String safe(String s) { return s == null ? "" : s.trim(); }
}
