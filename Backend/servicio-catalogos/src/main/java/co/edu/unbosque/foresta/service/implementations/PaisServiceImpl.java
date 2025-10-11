package co.edu.unbosque.foresta.service.implementations;


import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.ConflictException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.model.DTO.PaisDTO;
import co.edu.unbosque.foresta.model.DTO.SituacionEconomicaDTO;
import co.edu.unbosque.foresta.model.entity.Pais;
import co.edu.unbosque.foresta.model.entity.SituacionEconomica;
import co.edu.unbosque.foresta.model.enums.EstadoEnum;
import co.edu.unbosque.foresta.repository.IPaisRepository;
import co.edu.unbosque.foresta.repository.ISituacionEconomicaRepository;
import co.edu.unbosque.foresta.service.interfaces.IPaisService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaisServiceImpl implements IPaisService {

    private final IPaisRepository paisRepo;
    private final ISituacionEconomicaRepository sitEcoRepo;
    private final ModelMapper mm;

    public PaisServiceImpl(IPaisRepository paisRepo,
                           ISituacionEconomicaRepository sitEcoRepo,
                           ModelMapper mm) {
        this.paisRepo = paisRepo;
        this.sitEcoRepo = sitEcoRepo;
        this.mm = mm;
    }

    @Override
    @Transactional
    public PaisDTO crear(PaisDTO dto) {
        normalizar(dto);
        validarCampos(dto);
        validarDuplicados(dto);

        SituacionEconomica se = cargarSituacionEconomicaSiViene(dto.getSituacionEconomicaDTO());

        Pais entity = construirEntidad(dto, se);
        Pais guardado = paisRepo.save(entity);

        return aDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaisDTO> listar() {
        return paisRepo.findAll()
                .stream()
                .map(this::aDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaisDTO obtener(Long id) {
        Pais p = paisRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("País no encontrado: " + id));
        return aDTO(p);
    }

    private void normalizar(PaisDTO dto) {
        if (dto == null) return;
        if (dto.getCodigoIso3() != null) dto.setCodigoIso3(dto.getCodigoIso3().trim().toUpperCase());
        if (dto.getNombre() != null) dto.setNombre(dto.getNombre().trim());
        if (dto.getEstado() == null) dto.setEstado(EstadoEnum.ACTIVO);
    }

    private void validarCampos(PaisDTO dto) {
        if (dto == null) throw new BadRequestException("Body requerido");
        if (dto.getCodigoIso3() == null || dto.getCodigoIso3().isBlank())
            throw new BadRequestException("codigoIso3 es obligatorio");
        if (dto.getCodigoIso3().length() != 3)
            throw new BadRequestException("codigoIso3 debe tener 3 letras (ISO3)");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new BadRequestException("nombre es obligatorio");
    }

    private void validarDuplicados(PaisDTO dto) {
        if (paisRepo.existsByCodigoIso3IgnoreCase(dto.getCodigoIso3()))
            throw new ConflictException("Ya existe un país con ese codigoIso3");
        if (paisRepo.existsByNombreIgnoreCase(dto.getNombre()))
            throw new ConflictException("Ya existe un país con ese nombre");
    }

    private SituacionEconomica cargarSituacionEconomicaSiViene(SituacionEconomicaDTO seDTO) {
        if (seDTO == null || seDTO.getId() == null) return null;
        return sitEcoRepo.findById(seDTO.getId())
                .orElseThrow(() -> new NotFoundException("Situación económica no encontrada: " + seDTO.getId()));
    }

    private Pais construirEntidad(PaisDTO dto, SituacionEconomica se) {
        Pais p = new Pais();
        p.setCodigoIso3(dto.getCodigoIso3());
        p.setNombre(dto.getNombre());
        p.setEstado(dto.getEstado());
        p.setSituacionEconomica(se);
        return p;
    }

    private PaisDTO aDTO(Pais entity) {
        return mm.map(entity, PaisDTO.class);
    }
}
