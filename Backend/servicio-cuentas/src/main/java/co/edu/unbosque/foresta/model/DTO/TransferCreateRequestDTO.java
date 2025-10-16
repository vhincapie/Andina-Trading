package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferCreateRequestDTO {
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("timing")
    private String timing;

    @JsonProperty("relationship_id")
    private String relationshipId;

    @JsonProperty("note")
    private String note;

    @JsonProperty("transfer_type")
    private String transferType;

    public TransferCreateRequestDTO() {
    }

    public TransferCreateRequestDTO(BigDecimal amount, String direction, String timing, String relationshipId, String note, String transferType) {
        this.amount = amount;
        this.direction = direction;
        this.timing = timing;
        this.relationshipId = relationshipId;
        this.note = note;
        this.transferType = transferType;
    }

    //Getters/Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}