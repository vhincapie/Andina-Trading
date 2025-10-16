package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountACHRelationShipDTO {

    @JsonProperty("account_owner_name")
    @NotBlank
    private String accountOwnerName;

    @JsonProperty("bank_account_type")
    @NotBlank
    private String bankAccountType;

    @JsonProperty("bank_account_number")
    @NotBlank
    private String bankAccountNumber;

    @JsonProperty("bank_routing_number")
    @NotBlank
    private String bankRoutingNumber;

    @JsonProperty("nickname")
    private String nickname;

    public AccountACHRelationShipDTO() {
    }

    public AccountACHRelationShipDTO(String accountOwnerName, String bankAccountType, String bankAccountNumber, String bankRoutingNumber, String nickname) {
        this.accountOwnerName = accountOwnerName;
        this.bankAccountType = bankAccountType;
        this.bankAccountNumber = bankAccountNumber;
        this.bankRoutingNumber = bankRoutingNumber;
        this.nickname = nickname;
    }

    //Getters/Setters
    public String getAccountOwnerName() {
        return accountOwnerName;
    }

    public void setAccountOwnerName(String accountOwnerName) {
        this.accountOwnerName = accountOwnerName;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }

    public void setBankRoutingNumber(String bankRoutingNumber) {
        this.bankRoutingNumber = bankRoutingNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
