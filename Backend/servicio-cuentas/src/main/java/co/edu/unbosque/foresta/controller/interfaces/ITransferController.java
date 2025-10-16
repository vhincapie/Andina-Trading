package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.TransferCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.TransferResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/cuentas/transferencias")
public interface ITransferController {
    @PostMapping("/crear")
    ResponseEntity<TransferResponseDTO> crear(@RequestBody TransferCreateRequestDTO req);
}
