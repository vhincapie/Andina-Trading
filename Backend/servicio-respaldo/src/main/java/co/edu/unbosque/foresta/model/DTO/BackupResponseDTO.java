package co.edu.unbosque.foresta.model.DTO;

import java.util.List;

public class BackupResponseDTO {
    private boolean ok;
    private String file;
    private List<String> files;

    public BackupResponseDTO() {}

    public BackupResponseDTO(boolean ok, String file) {
        this.ok = ok;
        this.file = file;
    }

    public BackupResponseDTO(boolean ok, List<String> files) {
        this.ok = ok;
        this.files = files;
    }

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public List<String> getFiles() { return files; }
    public void setFiles(List<String> files) { this.files = files; }
}
