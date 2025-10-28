package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaRegistroRequestDTO;
import co.edu.unbosque.foresta.model.DTO.InversionistaUpdateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.AlpacaAccountDTO;

public interface IInversionistaService {
    InversionistaDTO registrar(InversionistaRegistroRequestDTO req);
    InversionistaDTO actualizar(String correoAutenticado, InversionistaUpdateRequestDTO req);
    InversionistaDTO obtenerMiPerfil(String correoAutenticado);
    InversionistaDTO obtenerPorId(Long id);
    AlpacaAccountDTO miAlpaca(String correoAutenticado);
}
