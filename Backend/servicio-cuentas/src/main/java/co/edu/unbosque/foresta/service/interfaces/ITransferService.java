package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.TransferCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.TransferResponseDTO;

public interface ITransferService {
    TransferResponseDTO crear(TransferCreateRequestDTO req);
}
