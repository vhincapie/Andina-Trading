package co.edu.unbosque.foresta.model.DTO;

public class AlpacaAccountDTO {
    private String alpacaId;
    private String status;
    private String currency;

    public AlpacaAccountDTO() {

    }

    public AlpacaAccountDTO(String alpacaId, String status, String currency) {
        this.alpacaId = alpacaId;
        this.status = status;
        this.currency = currency;
    }

    //Getters/Setters
    public String getAlpacaId() {
        return alpacaId;
    }

    public void setAlpacaId(String alpacaId) {
        this.alpacaId = alpacaId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
