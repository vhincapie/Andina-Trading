package co.edu.unbosque.foresta.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_ach_relationship")
public class AccountACHRelationShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "ach_id", nullable = false, unique = true, length = 64)
    private String achId;

    @Column(name = "account_owner_name", length = 120, nullable = true)
    private String accountOwnerName;

    @Column(name = "bank_account_type", length = 30, nullable = true)
    private String bankAccountType;

    @Column(name = "bank_account_number", length = 34, nullable = true)
    private String bankAccountNumber;

    @Column(name = "nickname", length = 80, nullable = true)
    private String nickname;

    @Column(name = "alpaca_account_id", nullable = false, length = 64)
    private String alpacaAccountId;

    public AccountACHRelationShip() {
    }

    public AccountACHRelationShip(Long id, LocalDateTime createdAt, String achId, String accountOwnerName, String bankAccountType, String bankAccountNumber, String nickname, String alpacaAccountId) {
        this.id = id;
        this.createdAt = createdAt;
        this.achId = achId;
        this.accountOwnerName = accountOwnerName;
        this.bankAccountType = bankAccountType;
        this.bankAccountNumber = bankAccountNumber;
        this.nickname = nickname;
        this.alpacaAccountId = alpacaAccountId;
    }

    //Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAchId() {
        return achId;
    }

    public void setAchId(String achId) {
        this.achId = achId;
    }

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAlpacaAccountId() {
        return alpacaAccountId;
    }

    public void setAlpacaAccountId(String alpacaAccountId) {
        this.alpacaAccountId = alpacaAccountId;
    }
}
