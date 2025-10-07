package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.SituacionEconomicaDTO;

import java.util.List;

public interface ISituacionEconomicaService {
    SituacionEconomicaDTO crear(SituacionEconomicaDTO dto);
    List<SituacionEconomicaDTO> listar();
    SituacionEconomicaDTO obtener(Long id);
}
