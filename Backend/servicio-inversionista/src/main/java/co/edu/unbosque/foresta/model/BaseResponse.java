package co.edu.unbosque.foresta.model;

import java.time.Instant;

public class BaseResponse {
    private String message;
    private int status;
    private String path;
    private Instant timestamp = Instant.now();

    public BaseResponse() {
    }

    public BaseResponse(String message, int status, String path) {
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = Instant.now();
    }


    //Getters/Setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
