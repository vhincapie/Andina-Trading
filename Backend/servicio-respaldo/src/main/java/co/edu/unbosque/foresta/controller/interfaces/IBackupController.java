package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.BackupResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/respaldo")
public interface IBackupController {

    @PostMapping("/ejecutar/{db}")
    ResponseEntity<BackupResponseDTO> backupDb(@PathVariable String db) throws Exception;

    @PostMapping("/ejecutar-todo")
    ResponseEntity<BackupResponseDTO> backupAll() throws Exception;

    @GetMapping("/archivos")
    ResponseEntity<BackupResponseDTO> list() throws Exception;

    @GetMapping("/descargar/{filename}")
    ResponseEntity<byte[]> download(@PathVariable String filename) throws Exception;

    @PostMapping("/zip")
    ResponseEntity<BackupResponseDTO> zip(@RequestBody List<String> files) throws Exception;
}
