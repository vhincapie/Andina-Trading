package co.edu.unbosque.foresta.integration.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdenDTO {

    private String id;

    private String symbol;
    private String side;
    private String orderType;
    private String status;

    private BigDecimal qty;

    private BigDecimal limitPrice;
    private BigDecimal stopPrice;
    private BigDecimal transactionAmount;
    private BigDecimal commissionAmount;
    private BigDecimal netAmount;
    private BigDecimal unitPrice;

    private String timeInForce;
    private String moneda;

    @JsonProperty("creadoEn")
    private OffsetDateTime createdAt;

    @JsonProperty("actualizadoEn")
    private OffsetDateTime updatedAt;

    private Long inversionistaId;
    private Long comisionistaId;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }

    public BigDecimal getLimitPrice() { return limitPrice; }
    public void setLimitPrice(BigDecimal limitPrice) { this.limitPrice = limitPrice; }

    public BigDecimal getStopPrice() { return stopPrice; }
    public void setStopPrice(BigDecimal stopPrice) { this.stopPrice = stopPrice; }

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getTimeInForce() { return timeInForce; }
    public void setTimeInForce(String timeInForce) { this.timeInForce = timeInForce; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getInversionistaId() { return inversionistaId; }
    public void setInversionistaId(Long inversionistaId) { this.inversionistaId = inversionistaId; }

    public Long getComisionistaId() { return comisionistaId; }
    public void setComisionistaId(Long comisionistaId) { this.comisionistaId = comisionistaId; }
}
