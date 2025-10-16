package co.edu.unbosque.foresta.model.DTO;

import java.math.BigDecimal;

public class TransferResponseDTO {
    private String id;
    private BigDecimal amount;
    private String status;
    private String direction;
    private String timing;
    private String relationshipId;

    public TransferResponseDTO() {
    }

    public TransferResponseDTO(String id, BigDecimal amount, String status, String direction, String timing, String relationshipId) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.direction = direction;
        this.timing = timing;
        this.relationshipId = relationshipId;
    }

    //Getters/Setters/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }
}