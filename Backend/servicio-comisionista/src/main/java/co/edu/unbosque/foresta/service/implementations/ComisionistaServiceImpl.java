package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.ConflictException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.AuthClient;
import co.edu.unbosque.foresta.integration.CatalogosClient;
import co.edu.unbosque.foresta.integration.DTO.AuthSignupRequest;
import co.edu.unbosque.foresta.integration.DTO.AuthSignupResponse;
import co.edu.unbosque.foresta.model.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.model.DTO.ComisionistaRegistroRequestDTO;
import co.edu.unbosque.foresta.model.entity.Comisionista;
import co.edu.unbosque.foresta.repository.IComisionistaRepository;
import co.edu.unbosque.foresta.service.interfaces.IComisionistaService;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComisionistaServiceImpl implements IComisionistaService {

    private final IComisionistaRepository repo;
    private final AuthClient authClient;
    private final CatalogosClient catClient;
    private final ModelMapper mm;

    public ComisionistaServiceImpl(IComisionistaRepository repo,
                                   AuthClient authClient,
                                   CatalogosClient catClient,
                                   ModelMapper mm) {
        this.repo = repo;
        this.authClient = authClient;
        this.catClient = catClient;
        this.mm = mm;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ComisionistaDTO registrar(ComisionistaRegistroRequestDTO req, String ip) {
        ComisionistaRegistroRequestDTO n = prepararRegistro(req);
        Long usuarioId = registrarUsuarioAuth(n);
        Comisionista guardado = persistirComisionista(usuarioId, n);
        return toDTO(guardado);
    }

    @Transactional(readOnly = true)
    @Override
    public ComisionistaDTO perfil(String correoAutenticado) {
        if (correoAutenticado == null || correoAutenticado.isBlank()) {
            throw new BadRequestException("Correo de autenticación requerido");
        }
        Comisionista c = repo.findByCorreoIgnoreCase(correoAutenticado)
                .orElseThrow(() -> new NotFoundException("Perfil no encontrado"));
        return toDTO(c);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ComisionistaDTO> listarTodos() {
        return repo.findAll()
                .stream()
                .map(e -> mm.map(e, ComisionistaDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ComisionistaDTO obtenerPorId(Long id) {
        Comisionista c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Comisionista not found"));
        return mm.map(c, ComisionistaDTO.class);
    }

    private ComisionistaRegistroRequestDTO prepararRegistro(ComisionistaRegistroRequestDTO req) {
        validarReqRegistro(req);
        ComisionistaRegistroRequestDTO n = normalizarReqRegistro(req);
        validarNegocioRegistro(n);
        validarUbicacion(n.getPaisId(), n.getCiudadId());
        return n;
    }

    private Long registrarUsuarioAuth(ComisionistaRegistroRequestDTO n) {
        AuthSignupRequest body = new AuthSignupRequest(n.getCorreo(), n.getContrasena());
        try {
            AuthSignupResponse signup = authClient.registrarComisionista(body);
            return signup.getUsuarioId();
        } catch (FeignException.Conflict e) {
            throw new ConflictException("El correo ya está registrado en Autenticación");
        } catch (FeignException.BadRequest e) {
            throw new BadRequestException("Datos inválidos en Autenticación");
        } catch (FeignException e) {
            throw new BadRequestException("Error registrando en Autenticación: " + e.status());
        }
    }

    private Comisionista persistirComisionista(Long usuarioId, ComisionistaRegistroRequestDTO n) {
        Comisionista c = construirComisionista(usuarioId, n);
        return repo.save(c);
    }

    private void validarNegocioRegistro(ComisionistaRegistroRequestDTO n) {
        asegurarCorreoDisponible(n.getCorreo());
        asegurarNumeroIdentificacionDisponible(n.getNumeroDocumento());
        validarEdad(n.getFechaNacimiento());
    }

    private void validarReqRegistro(ComisionistaRegistroRequestDTO r) {
        if (r == null) throw new BadRequestException("Body requerido");
        if (vacio(r.getNombre()) || vacio(r.getApellido())) throw new BadRequestException("nombre y apellido son obligatorios");
        if (vacio(r.getTipoDocumento()) || vacio(r.getNumeroDocumento())) throw new BadRequestException("tipoDocumento y numeroDocumento son obligatorios");
        if (vacio(r.getCorreo()) || vacio(r.getContrasena())) throw new BadRequestException("correo y contrasena son obligatorios");
        if (r.getFechaNacimiento() == null) throw new BadRequestException("fechaNacimiento es obligatoria");
        if (r.getPaisId() == null || r.getCiudadId() == null) throw new BadRequestException("paisId y ciudadId son obligatorios");
        if (r.getAniosExperiencia() == null || r.getAniosExperiencia() < 0) throw new BadRequestException("aniosExperiencia es obligatorio y no puede ser negativo");
    }

    private ComisionistaRegistroRequestDTO normalizarReqRegistro(ComisionistaRegistroRequestDTO r) {
        ComisionistaRegistroRequestDTO n = new ComisionistaRegistroRequestDTO();
        n.setNombre(trim(r.getNombre()));
        n.setApellido(trim(r.getApellido()));
        n.setTipoDocumento(trim(r.getTipoDocumento()));
        n.setNumeroDocumento(trim(r.getNumeroDocumento()));
        n.setCorreo(trimLower(r.getCorreo()));
        n.setContrasena(r.getContrasena());
        n.setFechaNacimiento(r.getFechaNacimiento());
        n.setPaisId(r.getPaisId());
        n.setCiudadId(r.getCiudadId());
        n.setAniosExperiencia(r.getAniosExperiencia());
        return n;
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

    private void validarEdad(LocalDate fechaNac) {
        LocalDate hoy = LocalDate.now();
        if (fechaNac.isAfter(hoy)) throw new BadRequestException("La fecha no puede ser futura");
        if (Period.between(fechaNac, hoy).getYears() < 18) throw new BadRequestException("Debes ser mayor de 18 años");
    }

    private Comisionista construirComisionista(Long usuarioId, ComisionistaRegistroRequestDTO n) {
        Comisionista c = new Comisionista();
        c.setUsuarioId(usuarioId);
        c.setCorreo(n.getCorreo());
        c.setNombre(n.getNombre());
        c.setApellido(n.getApellido());
        c.setTipoDocumento(n.getTipoDocumento());
        c.setNumeroDocumento(n.getNumeroDocumento());
        c.setFechaNacimiento(n.getFechaNacimiento());
        c.setPaisId(n.getPaisId());
        c.setCiudadId(n.getCiudadId());
        c.setAniosExperiencia(n.getAniosExperiencia());
        return c;
    }

    private ComisionistaDTO toDTO(Comisionista e) {
        return mm.map(e, ComisionistaDTO.class);
    }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static String trimLower(String s) { return s == null ? null : s.trim().toLowerCase(); }
}
