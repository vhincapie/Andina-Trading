package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.service.interfaces.IBackupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduler {

    private final IBackupService backupService;

    public BackupScheduler(IBackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void autoBackup() {
        try {
            backupService.backupAll();
            System.out.println("[BACKUP AUTO] Copia de seguridad completada correctamente");
        } catch (Exception e) {
            System.err.println("[BACKUP AUTO] Error al generar backup: " + e.getMessage());
        }
    }
}
