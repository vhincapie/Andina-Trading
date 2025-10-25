package co.edu.unbosque.foresta.model.DTO;

public class MarketStatusDTO {
    private boolean open;
    private String message;

    public MarketStatusDTO() {}
    public MarketStatusDTO(boolean open, String message) {
        this.open = open; this.message = message;
    }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
