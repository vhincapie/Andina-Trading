package co.edu.unbosque.foresta.exceptions.exceptions;

public class BackupException extends RuntimeException {
    public BackupException(String msg) {
        super(msg);
    }

    public BackupException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
