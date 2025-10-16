package co.edu.unbosque.foresta.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_log")
public class TransferLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="alpaca_account_id", nullable=false, length=64)
    private String alpacaAccountId;
    @Column(name="external_id", length=100)
    private String externalId;
    @Column(name="amount", nullable=false, precision=19, scale=2)
    private BigDecimal amount;
    @Column(name="status", length=40)
    private String status;
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    public TransferLog() {
    }

    public TransferLog(Long id, String alpacaAccountId, String externalId, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.alpacaAccountId = alpacaAccountId;
        this.externalId = externalId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    //Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlpacaAccountId() {
        return alpacaAccountId;
    }

    public void setAlpacaAccountId(String alpacaAccountId) {
        this.alpacaAccountId = alpacaAccountId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
