package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.ITransferController;
import co.edu.unbosque.foresta.model.DTO.TransferCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.TransferResponseDTO;
import co.edu.unbosque.foresta.service.interfaces.ITransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController implements ITransferController {

    private final ITransferService transferService;

    public TransferController(ITransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public ResponseEntity<TransferResponseDTO> crear(TransferCreateRequestDTO req) {
        return ResponseEntity.ok(transferService.crear(req));
    }

}
