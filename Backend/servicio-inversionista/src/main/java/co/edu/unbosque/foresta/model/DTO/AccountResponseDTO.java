package co.edu.unbosque.foresta.model.DTO;

public class AccountResponseDTO {
    private String id;
    private String status;
    private String currency;
    private String accountNumber;

    public AccountResponseDTO() {}

    public AccountResponseDTO(String id, String status, String currency, String accountNumber) {
        this.id = id;
        this.status = status;
        this.currency = currency;
        this.accountNumber = accountNumber;
    }

    //Getters/Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
