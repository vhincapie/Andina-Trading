package co.edu.unbosque.foresta.integration.DTO;

import java.math.BigDecimal;

public class ContratoActivoDTO {
    private Long id;
    private Long inversionistaId;
    private Long comisionistaId;
    private String moneda;
    private BigDecimal porcentajeCobroAplicado;

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

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getPorcentajeCobroAplicado() {
        return porcentajeCobroAplicado;
    }

    public void setPorcentajeCobroAplicado(BigDecimal porcentajeCobroAplicado) {
        this.porcentajeCobroAplicado = porcentajeCobroAplicado;
    }
}
