package co.edu.unbosque.foresta.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="inversionista_id", nullable=false)
    private Long inversionistaId;

    @Column(name="comisionista_id", nullable=false)
    private Long comisionistaId;

    @Column(name="alpaca_order_id", length=64)
    private String alpacaOrderId;

    @Column(nullable=false, length=16)
    private String symbol;

    @Column(nullable=false, precision=18, scale=6)
    private BigDecimal qty;

    @Column(name="order_type", nullable=false, length=20)
    private String orderType;

    @Column(nullable=false, length=10)
    private String side;

    @Column(name="time_in_force", nullable=false, length=20)
    private String timeInForce;

    @Column(name="limit_price", precision=18, scale=6)
    private BigDecimal limitPrice;

    @Column(name="stop_price", precision=18, scale=6)
    private BigDecimal stopPrice;

    @Column(nullable=false, length=30)
    private String status;

    @Column(name="unit_price", nullable=false, precision=18, scale=6)
    private BigDecimal unitPrice;

    @Column(name="transaction_amount", nullable=false, precision=18, scale=6)
    private BigDecimal transactionAmount;

    @Column(name="commission_amount", nullable=false, precision=18, scale=6)
    private BigDecimal commissionAmount;

    @Column(name="net_amount", nullable=false, precision=18, scale=6)
    private BigDecimal netAmount;

    @Column(nullable=false, length=3)
    private String moneda = "USD";

    @Column(name="creado_en", nullable=false, updatable=false, insertable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant creadoEn;

    @Column(name="actualizado_en", nullable=false, insertable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Instant actualizadoEn;

    @Column(name="approved_by")
    private Long approvedBy;

    @Column(name="approved_at")
    private Instant approvedAt;

    @Column(name="reject_reason", length = 255)
    private String rejectReason;

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAlpacaOrderId() {
        return alpacaOrderId;
    }

    public void setAlpacaOrderId(String alpacaOrderId) {
        this.alpacaOrderId = alpacaOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
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

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public BigDecimal getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(BigDecimal stopPrice) {
        this.stopPrice = stopPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
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

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
