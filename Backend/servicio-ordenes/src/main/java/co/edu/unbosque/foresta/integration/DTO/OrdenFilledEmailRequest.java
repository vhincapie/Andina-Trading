package co.edu.unbosque.foresta.integration.DTO;

import java.math.BigDecimal;
import java.time.Instant;
public class OrdenFilledEmailRequest {

    private Long orderDbId;
    private String symbol;
    private String side;
    private String qty;
    private BigDecimal unitPrice;
    private BigDecimal netAmount;
    private String moneda;
    private Instant filledAt;
    private String inversionistaNombre;
    private String inversionistaCorreo;

    public Long getOrderDbId() {
        return orderDbId;
    }

    public void setOrderDbId(Long orderDbId) {
        this.orderDbId = orderDbId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Instant getFilledAt() {
        return filledAt;
    }

    public void setFilledAt(Instant filledAt) {
        this.filledAt = filledAt;
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
