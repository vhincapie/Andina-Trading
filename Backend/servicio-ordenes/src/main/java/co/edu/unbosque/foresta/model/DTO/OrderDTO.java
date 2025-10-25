package co.edu.unbosque.foresta.model.DTO;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderDTO {
    private Long dbId;

    private String id;
    private String alpacaOrderId;

    private Long inversionistaId;
    private Long comisionistaId;

    private String symbol;
    private String qty;
    private String orderType;
    private String side;
    private String timeInForce;
    private String limitPrice;
    private String stopPrice;
    private String status;

    private String moneda;
    private BigDecimal unitPrice;
    private BigDecimal transactionAmount;
    private BigDecimal commissionAmount;
    private BigDecimal netAmount;

    private Instant creadoEn;
    private Instant actualizadoEn;

    private String inversionistaNombre;
    private String inversionistaCorreo;

    // Getters/Setters
    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlpacaOrderId() {
        return alpacaOrderId;
    }

    public void setAlpacaOrderId(String alpacaOrderId) {
        this.alpacaOrderId = alpacaOrderId;
    }

    public Long getInversionistaId() {
        return inversionistaId;
    }

    public void setInversionistaId(Long inversionistaId) {
        this.inversionistaId = inversionistaId;
    }

    public Long getComisionistaId() {
        return comisionistaId;
    }

    public void setComisionistaId(Long comisionistaId) {
        this.comisionistaId = comisionistaId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(String limitPrice) {
        this.limitPrice = limitPrice;
    }

    public String getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(String stopPrice) {
        this.stopPrice = stopPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Instant actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public String getInversionistaNombre() {
        return inversionistaNombre;
    }

    public void setInversionistaNombre(String inversionistaNombre) {
        this.inversionistaNombre = inversionistaNombre;
    }

    public String getInversionistaCorreo() {
        return inversionistaCorreo;
    }

    public void setInversionistaCorreo(String inversionistaCorreo) {
        this.inversionistaCorreo = inversionistaCorreo;
    }
}
