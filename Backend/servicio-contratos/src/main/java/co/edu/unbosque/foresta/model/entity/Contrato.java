package co.edu.unbosque.foresta.model.entity;

import co.edu.unbosque.foresta.model.enums.EstadoContratoEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="inversionista_id", nullable = false)
    private Long inversionistaId;

    @Column(name="comisionista_id", nullable = false)
    private Long comisionistaId;

    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false, length = 20)
    private EstadoContratoEnum estado;

    @Column(name="moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name="porcentaje_cobro_aplicado", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeCobroAplicado;

    @Column(name="terminos_texto")
    private String terminosTexto;

    @Column(name="observaciones")
    private String observaciones;

    @Column(name = "fecha_inicio", insertable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public Contrato(){}

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

    public EstadoContratoEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoContratoEnum estado) {
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
