package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.PaisDTO;
import java.util.List;

public interface IPaisService {
    PaisDTO crear(PaisDTO dto);
    List<PaisDTO> listar();
    PaisDTO obtener(Long id);
}
