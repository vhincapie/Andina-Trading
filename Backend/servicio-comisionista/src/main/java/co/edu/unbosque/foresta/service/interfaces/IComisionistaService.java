package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.model.DTO.ComisionistaRegistroRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface IComisionistaService {
    ComisionistaDTO registrar(ComisionistaRegistroRequestDTO req, String ip);
    ComisionistaDTO perfil(String correoAutenticado);
}
