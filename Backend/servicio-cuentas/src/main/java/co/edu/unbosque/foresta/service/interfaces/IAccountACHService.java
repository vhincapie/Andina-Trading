package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.foresta.model.DTO.ResponseAccountACHDTO;
import java.util.List;

public interface IAccountACHService<T> {
    ResponseAccountACHDTO create(T dto);
    List<AccountACHRelationShipDTO> getAllForAuthenticatedUser();
}
