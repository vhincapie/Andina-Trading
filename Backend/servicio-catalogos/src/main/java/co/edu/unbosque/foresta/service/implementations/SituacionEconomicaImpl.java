package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.ConflictException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.model.DTO.SituacionEconomicaDTO;
import co.edu.unbosque.foresta.model.entity.SituacionEconomica;
import co.edu.unbosque.foresta.model.enums.EstadoEnum;
import co.edu.unbosque.foresta.repository.ISituacionEconomicaRepository;
import co.edu.unbosque.foresta.service.interfaces.ISituacionEconomicaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Service
public class SituacionEconomicaImpl implements ISituacionEconomicaService {

    private final ISituacionEconomicaRepository repo;
    private final ModelMapper mm;
    private final AuditSender auditSender;

    public SituacionEconomicaImpl(ISituacionEconomicaRepository repo, ModelMapper mm, AuditSender auditSender) {
        this.repo = repo;
        this.mm = mm;
        this.auditSender = auditSender;
    }

    @Override
    @Transactional
    public SituacionEconomicaDTO crear(SituacionEconomicaDTO dto) {
        normalizar(dto);
        validarCampos(dto);
        validarDuplicado(dto.getNombre());
        SituacionEconomica entity = construirEntidad(dto);
        SituacionEconomica guardada = repo.save(entity);
        auditSender.log("", new AuditLogRequest(
                "CAT_SIT_ECO_CREATE",
                "/api/catalogos/situaciones-economicas",
                "Crear situación económica",
                Map.of("situacionEconomicaId", guardada.getId(), "nombre", guardada.getNombre())
        ));
        return aDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SituacionEconomicaDTO> listar() {
        List<SituacionEconomicaDTO> res = repo.findAll()
                .stream()
                .map(this::aDTO)
                .toList();
        auditSender.log("", new AuditLogRequest(
                "CAT_SIT_ECO_LIST",
                "/api/catalogos/situaciones-economicas",
                "Listar situaciones económicas",
                Map.of("total", res.size())
        ));
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public SituacionEconomicaDTO obtener(Long id) {
        SituacionEconomica se = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Situación económica no encontrada: " + id));
        auditSender.log("", new AuditLogRequest(
                "CAT_SIT_ECO_GET",
                "/api/catalogos/situaciones-economicas/" + id,
                "Obtener situación económica",
                Map.of("situacionEconomicaId", id)
        ));
        return aDTO(se);
    }

    private void normalizar(SituacionEconomicaDTO dto) {
        if (dto == null) return;
        if (dto.getNombre() != null) dto.setNombre(dto.getNombre().trim());
        if (dto.getDescripcion() != null) dto.setDescripcion(dto.getDescripcion().trim());
        if (dto.getEstado() == null) dto.setEstado(EstadoEnum.ACTIVO);
    }

    private void validarCampos(SituacionEconomicaDTO dto) {
        if (dto == null) throw new BadRequestException("Body requerido");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new BadRequestException("nombre es obligatorio");
    }

    private void validarDuplicado(String nombre) {
        if (repo.existsByNombreIgnoreCase(nombre))
            throw new ConflictException("Ya existe una situación económica con ese nombre");
    }

    private SituacionEconomica construirEntidad(SituacionEconomicaDTO dto) {
        SituacionEconomica se = new SituacionEconomica();
        se.setNombre(dto.getNombre());
        se.setDescripcion(dto.getDescripcion());
        se.setEstado(dto.getEstado());
        return se;
    }

    private SituacionEconomicaDTO aDTO(SituacionEconomica entity) {
        return mm.map(entity, SituacionEconomicaDTO.class);
    }
}
