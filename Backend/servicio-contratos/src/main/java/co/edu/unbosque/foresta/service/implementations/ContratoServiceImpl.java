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
        // 1) Validar request
        validarRequest(req);

        // 2) Obtener inversionista autenticado
        InversionistaPerfilDTO inv = invClient.miPerfil();
        if (inv == null || inv.getId() == null) throw new NotFoundException("Perfil de inversionista no encontrado");

        // 3) Validar comisionista existe (perfil mínimo)
        ComisionistaPerfilDTO comPerfil = obtenerComisionista(req.getComisionistaId());

        // 4) Regla: un contrato ACTIVO por inversionista
        if (repo.existsByInversionistaIdAndEstado(inv.getId(), EstadoContratoEnum.ACTIVO)) {
            throw new ConflictException("Ya existe un contrato ACTIVO para este inversionista");
        }

        // 5) Construir entidad (fechas automáticas por DB; fecha_fin null)
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

        // 6) Guardar
        Contrato g = repo.save(c);

        // 7) Mapear y ENRIQUECER
        ContratoDTO dto = mm.map(g, ContratoDTO.class);
        enriquecerNombresYDocs(dto);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoDTO obtenerMiContratoActivo() {
        // 1) Identificar inversionista autenticado
        InversionistaPerfilDTO inv = invClient.miPerfil();
        if (inv == null || inv.getId() == null) {
            throw new NotFoundException("Perfil de inversionista no encontrado");
        }

        // 2) Buscar contrato ACTIVO más reciente
        Contrato contrato = repo.findFirstByInversionistaIdAndEstadoOrderByCreadoEnDesc(
                inv.getId(), EstadoContratoEnum.ACTIVO
        ).orElseThrow(() -> new NotFoundException("No tienes un contrato activo"));

        // 3) Mapear y ENRIQUECER
        ContratoDTO dto = mm.map(contrato, ContratoDTO.class);
        enriquecerNombresYDocs(dto);

        return dto;
    }

    @Override
    @Transactional
    public ContratoDTO cancelarMiContratoActivo() {
        // 1) Obtener inversionista autenticado
        InversionistaPerfilDTO inv = invClient.miPerfil();
        if (inv == null || inv.getId() == null)
            throw new NotFoundException("Perfil de inversionista no encontrado");

        // 2) Buscar su contrato activo
        Contrato c = repo.findFirstByInversionistaIdAndEstadoOrderByCreadoEnDesc(
                inv.getId(), EstadoContratoEnum.ACTIVO
        ).orElseThrow(() -> new NotFoundException("No tienes un contrato activo"));

        // 3) Cambiar estado y fecha fin
        c.setEstado(EstadoContratoEnum.TERMINADO);
        c.setFechaFin(LocalDateTime.now());

        // 4) Guardar cambios
        Contrato actualizado = repo.save(c);

        // 5) Mapear y ENRIQUECER
        ContratoDTO dto = mm.map(actualizado, ContratoDTO.class);
        enriquecerNombresYDocs(dto);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoDTO listarPorInversionistaParaComisionista(Long inversionistaId) {
        if (inversionistaId == null) throw new BadRequestException("inversionistaId es obligatorio");

        ComisionistaPerfilDTO com = comClient.miPerfil();
        if (com == null || com.getId() == null)
            throw new NotFoundException("Perfil de comisionista no encontrado");

        Contrato contrato = repo.findFirstByInversionistaIdAndComisionistaIdAndEstadoOrderByCreadoEnDesc(
                inversionistaId, com.getId(), EstadoContratoEnum.ACTIVO
        ).orElseThrow(() -> new NotFoundException("No existe un contrato ACTIVO para este inversionista con este comisionista"));

        ContratoDTO dto = mm.map(contrato, ContratoDTO.class);
        enriquecerNombresYDocs(dto);

        return dto;
    }

    private void validarRequest(ContratoRegistroRequestDTO r) {
        if (r == null) throw new BadRequestException("Body requerido");
        if (r.getComisionistaId() == null) throw new BadRequestException("comisionistaId es obligatorio");
        if (vacio(r.getMoneda())) throw new BadRequestException("moneda es obligatoria");
        if (Boolean.TRUE.equals(r.getAceptaTerminos()) == false)
            throw new BadRequestException("Debe aceptar los términos del contrato");
        // No validamos fechas: fecha_inicio la pone la BD; fecha_fin solo al cancelar
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
        // Inversionista
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

        // Comisionista
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
