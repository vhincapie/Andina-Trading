package co.edu.unbosque.foresta.service.interfaces;

import java.io.File;
import java.util.List;

public interface IBackupService {
    String backupOne(String dbName) throws Exception;
    List<String> backupAll() throws Exception;
    List<String> listFiles() throws Exception;
    File resolve(String filename) throws Exception;
    String zip(String zipName, List<String> filenames) throws Exception;
}
