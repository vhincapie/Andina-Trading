package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.ConflictException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.model.DTO.CiudadDTO;
import co.edu.unbosque.foresta.model.entity.Ciudad;
import co.edu.unbosque.foresta.model.entity.Pais;
import co.edu.unbosque.foresta.model.enums.EstadoEnum;
import co.edu.unbosque.foresta.repository.ICiudadRepository;
import co.edu.unbosque.foresta.repository.IPaisRepository;
import co.edu.unbosque.foresta.service.interfaces.ICiudadService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CiudadServiceImpl implements ICiudadService {

    private final ICiudadRepository ciudadRepo;
    private final IPaisRepository paisRepo;
    private final ModelMapper mm;

    public CiudadServiceImpl(ICiudadRepository ciudadRepo, IPaisRepository paisRepo, ModelMapper mm) {
        this.ciudadRepo = ciudadRepo;
        this.paisRepo = paisRepo;
        this.mm = mm;
    }

    @Override
    @Transactional
    public CiudadDTO crear(CiudadDTO dto) {
        normalizar(dto);
        validarCamposObligatorios(dto);

        Pais pais = obtenerPais(dto.getPaisDTO().getId());
        validarDuplicado(dto.getNombre(), pais.getId());

        Ciudad entity = construirEntidad(dto, pais);
        Ciudad guardada = guardar(entity);

        return aDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CiudadDTO> listar() {
        return ciudadRepo.findAll()
                .stream()
                .map(this::aDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CiudadDTO obtener(Long id) {
        Ciudad c = ciudadRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ciudad no encontrada: " + id));
        return aDTO(c);
    }

    private void normalizar(CiudadDTO dto) {
        if (dto == null) return;
        if (dto.getNombre() != null) dto.setNombre(dto.getNombre().trim());
        if (dto.getEstado() == null) dto.setEstado(EstadoEnum.ACTIVO);
    }

    private void validarCamposObligatorios(CiudadDTO dto) {
        if (dto == null) throw new BadRequestException("Body requerido");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new BadRequestException("nombre es obligatorio");
        if (dto.getPaisDTO() == null || dto.getPaisDTO().getId() == null)
            throw new BadRequestException("paisId es obligatorio");
    }

    private Pais obtenerPais(Long paisId) {
        return paisRepo.findById(paisId)
                .orElseThrow(() -> new NotFoundException("País no encontrado: " + paisId));
    }

    private void validarDuplicado(String nombreCiudad, Long paisId) {
        boolean exists = ciudadRepo.existsByPaisIdAndNombreIgnoreCase(paisId, nombreCiudad);
        if (exists) throw new ConflictException("Ya existe una ciudad con ese nombre para el país");
    }

    private Ciudad construirEntidad(CiudadDTO dto, Pais pais) {
        Ciudad c = new Ciudad();
        c.setNombre(dto.getNombre());
        c.setPais(pais);
        c.setEstado(dto.getEstado());
        return c;
    }

    private Ciudad guardar(Ciudad entity) {
        return ciudadRepo.save(entity);
    }

    private CiudadDTO aDTO(Ciudad entity) {
        return mm.map(entity, CiudadDTO.class);
    }
}
