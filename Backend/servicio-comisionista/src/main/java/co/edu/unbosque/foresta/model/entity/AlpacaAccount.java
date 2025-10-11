package co.edu.unbosque.foresta.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alpaca_account")
public class AlpacaAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alpaca_account")
    private Long id;

    @Column(name = "alpaca_id", nullable = false, unique = true, length = 64)
    private String alpacaId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @OneToOne
    @JoinColumn(name = "id_comisionista", referencedColumnName = "id", nullable = false, unique = true)
    private Comisionista comisionista;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AlpacaAccount() {}

    public AlpacaAccount(Long id, String alpacaId, String status, String currency,
                         Comisionista comisionista, LocalDateTime createdAt) {
        this.id = id;
        this.alpacaId = alpacaId;
        this.status = status;
        this.currency = currency;
        this.comisionista = comisionista;
        this.createdAt = createdAt;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlpacaId() { return alpacaId; }
    public void setAlpacaId(String alpacaId) { this.alpacaId = alpacaId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Comisionista getComisionista() { return comisionista; }
    public void setComisionista(Comisionista comisionista) { this.comisionista = comisionista; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
