package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.configuration.BackupProperties;

import co.edu.unbosque.foresta.exceptions.exceptions.BackupException;
import co.edu.unbosque.foresta.service.interfaces.IBackupService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BackupServiceImpl implements IBackupService {

    private final BackupProperties props;
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public BackupServiceImpl(BackupProperties props) {
        this.props = props;
    }

    @Override
    public String backupOne(String dbName) {
        try {
            ensureDir();
            String ts = LocalDateTime.now().format(TS);
            return mysqlDump(dbName, ts);
        } catch (Exception e) {
            throw new BackupException("Error al generar backup de " + dbName, e);
        }
    }

    @Override
    public List<String> backupAll() {
        try {
            ensureDir();
            List<String> generated = new ArrayList<>();
            for (String db : props.getDatabases()) {
                generated.add(mysqlDump(db, LocalDateTime.now().format(TS)));
            }
            return generated;
        } catch (Exception e) {
            throw new BackupException("Error al generar backup de todas las bases", e);
        }
    }

    private String mysqlDump(String db, String ts) throws Exception {
        String filename = db + "-" + ts + ".sql";
        File out = new File(props.getDir(), filename);
        List<String> cmd = Arrays.asList(
                "bash", "-lc",
                String.join(" ",
                        "mysqldump",
                        "-h", shell(props.getHost()),
                        "-P", String.valueOf(props.getPort()),
                        "-u", shell(props.getUser()),
                        "-p" + escape(props.getPassword()),
                        shell(db),
                        ">", shell(out.getAbsolutePath())
                )
        );

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int exit = p.waitFor();
        if (exit != 0)
            throw new BackupException("mysqldump devolvió código de error " + exit);
        return out.getName();
    }

    private String shell(String v) { return "'" + v.replace("'", "'\"'\"'") + "'"; }
    private String escape(String v) { return v.replace("$", "\\$"); }

    @Override
    public List<String> listFiles() {
        ensureDir();
        File dir = new File(props.getDir());
        String[] files = dir.list((d, name) -> name.endsWith(".sql") || name.endsWith(".zip"));
        return files == null ? List.of() : Arrays.asList(files);
    }

    @Override
    public File resolve(String filename) {
        ensureDir();
        File f = new File(props.getDir(), filename);
        if (!f.exists()) throw new IllegalArgumentException("Archivo no encontrado: " + filename);
        return f;
    }

    @Override
    public String zip(String zipName, List<String> filenames) {
        try {
            ensureDir();
            if (!zipName.endsWith(".zip")) zipName += ".zip";
            File zip = new File(props.getDir(), zipName);
            try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(Files.newOutputStream(zip.toPath()))) {
                for (String fn : filenames) {
                    File f = resolve(fn);
                    zos.putNextEntry(new java.util.zip.ZipEntry(f.getName()));
                    Files.copy(f.toPath(), zos);
                    zos.closeEntry();
                }
            }
            return zip.getName();
        } catch (Exception e) {
            throw new BackupException("Error al crear archivo ZIP", e);
        }
    }

    private void ensureDir() {
        File dir = new File(props.getDir());
        if (!dir.exists() && !dir.mkdirs())
            throw new BackupException("No se pudo crear el directorio de respaldo: " + props.getDir());
    }
}
