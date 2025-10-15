package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.AccountResponseDTO;
import co.edu.unbosque.foresta.model.DTO.CreateAccountRequestDTO;

public interface IAlpacaService {
    AccountResponseDTO createAccount(CreateAccountRequestDTO dto);
    AccountResponseDTO getAccountById(String accountId);
}
