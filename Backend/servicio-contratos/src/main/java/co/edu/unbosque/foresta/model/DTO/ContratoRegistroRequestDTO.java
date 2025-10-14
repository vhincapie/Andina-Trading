package co.edu.unbosque.foresta.model.DTO;

public class ContratoRegistroRequestDTO {
    private Long comisionistaId;
    private String moneda;
    private String observaciones;
    private Boolean aceptaTerminos;

    public ContratoRegistroRequestDTO() {

    }

    //Getters/Setters
    public ContratoRegistroRequestDTO(Long comisionistaId, String moneda, String observaciones, Boolean aceptaTerminos) {
        this.comisionistaId = comisionistaId;
        this.moneda = moneda;
        this.observaciones = observaciones;
        this.aceptaTerminos = aceptaTerminos;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getAceptaTerminos() {
        return aceptaTerminos;
    }

    public void setAceptaTerminos(Boolean aceptaTerminos) {
        this.aceptaTerminos = aceptaTerminos;
    }
}
