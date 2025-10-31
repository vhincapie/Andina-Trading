package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IBackupController;
import co.edu.unbosque.foresta.model.DTO.BackupResponseDTO;
import co.edu.unbosque.foresta.service.interfaces.IBackupService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@RestController
public class BackupController implements IBackupController {

    private final IBackupService service;

    public BackupController(IBackupService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<BackupResponseDTO> backupDb(String db) throws Exception {
        String file = service.backupOne(db);
        return ResponseEntity.ok(new BackupResponseDTO(true, file));
    }

    @Override
    public ResponseEntity<BackupResponseDTO> backupAll() throws Exception {
        List<String> files = service.backupAll();
        return ResponseEntity.ok(new BackupResponseDTO(true, files));
    }

    @Override
    public ResponseEntity<BackupResponseDTO> list() throws Exception {
        List<String> files = service.listFiles();
        return ResponseEntity.ok(new BackupResponseDTO(true, files));
    }

    @Override
    public ResponseEntity<byte[]> download(String filename) throws Exception {
        File f = service.resolve(filename);
        byte[] bytes = Files.readAllBytes(f.toPath());
        HttpHeaders h = new HttpHeaders();
        h.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(bytes, h, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BackupResponseDTO> zip(List<String> files) throws Exception {
        String zip = service.zip("respaldo-" + System.currentTimeMillis(), files);
        return ResponseEntity.ok(new BackupResponseDTO(true, zip));
    }
}
