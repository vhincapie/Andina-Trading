package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.ConflictException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.AuthClient;
import co.edu.unbosque.foresta.integration.CatalogosClient;
import co.edu.unbosque.foresta.integration.DTO.AuthSignupRequest;
import co.edu.unbosque.foresta.integration.DTO.AuthSignupResponse;
import co.edu.unbosque.foresta.model.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaRegistroRequestDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaUpdateRequestDTO;
import co.edu.unbosque.foresta.model.entity.Inversionista;
import co.edu.unbosque.foresta.repository.IInversionistaRepository;
import co.edu.unbosque.foresta.service.interfaces.IInversionistaService;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
public class InversionistaServiceImpl implements IInversionistaService {

    private final IInversionistaRepository repo;
    private final AuthClient authClient;
    private final CatalogosClient catClient;
    private final ModelMapper mm;

    public InversionistaServiceImpl(IInversionistaRepository repo, AuthClient authClient, CatalogosClient catClient, ModelMapper mm) {
        this.repo = repo;
        this.authClient = authClient;
        this.catClient = catClient;
        this.mm = mm;
    }


    @Override
    @Transactional
    public InversionistaDTO registrar(InversionistaRegistroRequestDTO req) {
        InversionistaRegistroRequestDTO n = validarYNormalizarRegistro(req);
        validarNegocioRegistro(n);
        Long usuarioId = registrarEnAuthYObtenerUsuarioId(n);
        Inversionista guardado = construirYGuardarInversionista(usuarioId, n);
        return toDTO(guardado);
    }

    @Override
    @Transactional
    public InversionistaDTO actualizar(String correoAutenticado, InversionistaUpdateRequestDTO req) {
        validarReqUpdate(req);
        InversionistaUpdateRequestDTO n = normalizarReqUpdate(req);
        Inversionista inv = cargarPorCorreo(correoAutenticado);
        validarUbicacion(n.getPaisId(), n.getCiudadId());
        aplicarCambios(inv, n);
        Inversionista g = repo.save(inv);
        return toDTO(g);
    }

    @Override
    @Transactional(readOnly = true)
    public InversionistaDTO obtenerMiPerfil(String correoAutenticado) {
        Inversionista inv = cargarPorCorreo(correoAutenticado);
        return toDTO(inv);
    }

    private InversionistaRegistroRequestDTO validarYNormalizarRegistro(InversionistaRegistroRequestDTO req) {
        validarReqRegistro(req);
        return normalizarReqRegistro(req);
    }

    private void validarNegocioRegistro(InversionistaRegistroRequestDTO n) {
        asegurarCorreoDisponible(n.getCorreo());
        asegurarNumeroIdentificacionDisponible(n.getNumeroDocumento());
        validarEdad(n.getFechaNacimiento());
        validarUbicacion(n.getPaisId(), n.getCiudadId());
    }

    private Long registrarEnAuthYObtenerUsuarioId(InversionistaRegistroRequestDTO n) {
        AuthSignupRequest body = new AuthSignupRequest(n.getCorreo(), n.getContrasena());
        try {
            AuthSignupResponse signup = authClient.registrarInversionista(body);
            return signup.getUsuarioId();
        } catch (FeignException.Conflict e) {
            throw new ConflictException("El correo ya está registrado en Autenticación");
        } catch (FeignException.BadRequest e) {
            throw new BadRequestException("Datos inválidos en Autenticación");
        } catch (FeignException e) {
            throw new BadRequestException("Error registrando en Autenticación: " + e.status());
        }
    }

    private Inversionista construirYGuardarInversionista(Long usuarioId, InversionistaRegistroRequestDTO n) {
        Inversionista nuevo = construirEntidadRegistro(usuarioId, n);
        return repo.save(nuevo);
    }

    private void validarReqRegistro(InversionistaRegistroRequestDTO r) {
        if (r == null) throw new BadRequestException("Body requerido");
        if (vacio(r.getNombre()) || vacio(r.getApellido())) throw new BadRequestException("nombre y apellido son obligatorios");
        if (vacio(r.getTipoDocumento()) || vacio(r.getNumeroDocumento())) throw new BadRequestException("tipoDocumento y numeroDocumento son obligatorios");
        if (vacio(r.getCorreo()) || vacio(r.getContrasena())) throw new BadRequestException("correo y contrasena son obligatorios");
        if (r.getFechaNacimiento() == null) throw new BadRequestException("fechaNacimiento es obligatoria");
        if (r.getPaisId() == null || r.getCiudadId() == null) throw new BadRequestException("paisId y ciudadId son obligatorios");
    }

    private void validarReqUpdate(InversionistaUpdateRequestDTO r) {
        if (r == null) throw new BadRequestException("Body requerido");
        if (r.getPaisId() == null || r.getCiudadId() == null) throw new BadRequestException("paisId y ciudadId son obligatorios");
    }

    private void validarEdad(LocalDate fechaNac) {
        LocalDate hoy = LocalDate.now();
        if (fechaNac.isAfter(hoy)) throw new BadRequestException("La fecha no puede ser futura");
        if (Period.between(fechaNac, hoy).getYears() < 18) throw new BadRequestException("Debes ser mayor de 18 años");
    }

    private void validarUbicacion(Long paisId, Long ciudadId) {
        try {
            catClient.obtenerPais(paisId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("País no encontrado: " + paisId);
        } catch (FeignException e) {
              throw new BadRequestException("Error consultando País en Catálogos: " + e.status());
        }

        try {
            catClient.obtenerCiudad(ciudadId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Ciudad no encontrada: " + ciudadId);
        } catch (FeignException e) {
            throw new BadRequestException("Error consultando Ciudad en Catálogos: " + e.status());
        }
    }

    private void asegurarCorreoDisponible(String correo) {
        if (repo.existsByCorreoIgnoreCase(correo)) throw new ConflictException("El correo ya está registrado");
    }

    private void asegurarNumeroIdentificacionDisponible(String numeroDocumento) {
        if (repo.existsByNumeroDocumento(numeroDocumento)) throw new ConflictException("El número de documento ya está registrado");
    }

    private InversionistaRegistroRequestDTO normalizarReqRegistro(InversionistaRegistroRequestDTO r) {
        InversionistaRegistroRequestDTO n = new InversionistaRegistroRequestDTO();
        n.setNombre(trim(r.getNombre()));
        n.setApellido(trim(r.getApellido()));
        n.setTipoDocumento(trim(r.getTipoDocumento()));
        n.setNumeroDocumento(trim(r.getNumeroDocumento()));
        n.setCorreo(trimLower(r.getCorreo()));
        n.setContrasena(r.getContrasena());
        n.setFechaNacimiento(r.getFechaNacimiento());
        n.setPaisId(r.getPaisId());
        n.setCiudadId(r.getCiudadId());
        return n;
    }

    private InversionistaUpdateRequestDTO normalizarReqUpdate(InversionistaUpdateRequestDTO r) {
        InversionistaUpdateRequestDTO n = new InversionistaUpdateRequestDTO();
        n.setPaisId(r.getPaisId());
        n.setCiudadId(r.getCiudadId());
        return n;
    }

    private Inversionista cargarPorCorreo(String correo) {
        return repo.findByCorreoIgnoreCase(correo).orElseThrow(() -> new NotFoundException("Perfil no encontrado"));
    }

    private Inversionista construirEntidadRegistro(Long usuarioId, InversionistaRegistroRequestDTO n) {
        Inversionista inv = new Inversionista();
        inv.setUsuarioId(usuarioId);
        inv.setCorreo(n.getCorreo());
        inv.setNombre(n.getNombre());
        inv.setApellido(n.getApellido());
        inv.setTipoDocumento(n.getTipoDocumento());
        inv.setNumeroDocumento(n.getNumeroDocumento());
        inv.setFechaNacimiento(n.getFechaNacimiento());
        inv.setPaisId(n.getPaisId());
        inv.setCiudadId(n.getCiudadId());
        return inv;
    }

    private void aplicarCambios(Inversionista inv, InversionistaUpdateRequestDTO n) {
        inv.setPaisId(n.getPaisId());
        inv.setCiudadId(n.getCiudadId());
    }

    private InversionistaDTO toDTO(Inversionista e) {
        return mm.map(e, InversionistaDTO.class);
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
    private static String trimLower(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }
}