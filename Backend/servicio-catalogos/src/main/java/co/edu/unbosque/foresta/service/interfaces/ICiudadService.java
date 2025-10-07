package co.edu.unbosque.foresta.service.interfaces;


import co.edu.unbosque.foresta.model.DTO.CiudadDTO;

import java.util.List;

public interface ICiudadService {
    CiudadDTO crear(CiudadDTO dto);
    List<CiudadDTO> listar();
    CiudadDTO obtener(Long id);
}
