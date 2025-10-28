package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.AlpacaAccountDTO;

public interface IAlpacaAccountQueryService {

    AlpacaAccountDTO getByInversionistaId(Long inversionistaId);
}
