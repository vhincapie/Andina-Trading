package co.edu.unbosque.foresta.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContratoDTO {

    private Long id;
    private Long inversionistaId;
    private String inversionistaNombreCompleto;
    private String inversionistaDocumento;
    private Long comisionistaId;
    private String comisionistaNombreCompleto;
    private String estado;                    // ACTIVO | TERMINADO | ANULADO
    private String moneda;                    // COP | VES | USD | PEN
    private BigDecimal porcentajeCobroAplicado;
    private String terminosTexto;             // snapshot del texto corporativo
    private String observaciones;

    private LocalDateTime fechaInicio;        // TIMESTAMP (DEFAULT CURRENT_TIMESTAMP)
    private LocalDateTime fechaFin;           // TIMESTAMP (nullable, al cancelar)

    private LocalDateTime creadoEn;           // TIMESTAMP (auto DB)
    private LocalDateTime actualizadoEn;      // TIMESTAMP (auto DB)

    public ContratoDTO() {}

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

    public String getInversionistaNombreCompleto() {
        return inversionistaNombreCompleto;
    }

    public void setInversionistaNombreCompleto(String inversionistaNombreCompleto) {
        this.inversionistaNombreCompleto = inversionistaNombreCompleto;
    }

    public String getInversionistaDocumento() {
        return inversionistaDocumento;
    }

    public void setInversionistaDocumento(String inversionistaDocumento) {
        this.inversionistaDocumento = inversionistaDocumento;
    }

    public Long getComisionistaId() {
        return comisionistaId;
    }

    public void setComisionistaId(Long comisionistaId) {
        this.comisionistaId = comisionistaId;
    }

    public String getComisionistaNombreCompleto() {
        return comisionistaNombreCompleto;
    }

    public void setComisionistaNombreCompleto(String comisionistaNombreCompleto) {
        this.comisionistaNombreCompleto = comisionistaNombreCompleto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public String getTerminosTexto() {
        return terminosTexto;
    }

    public void setTerminosTexto(String terminosTexto) {
        this.terminosTexto = terminosTexto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
