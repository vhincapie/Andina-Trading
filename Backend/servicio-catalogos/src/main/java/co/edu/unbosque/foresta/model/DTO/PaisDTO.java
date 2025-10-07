package co.edu.unbosque.foresta.model.DTO;

import co.edu.unbosque.foresta.model.enums.EstadoEnum;

public class PaisDTO {
    private Long id;
    private String codigoIso2;
    private String nombre;
    private EstadoEnum estado;
    private SituacionEconomicaDTO situacionEconomicaDTO;

    public PaisDTO() {
    }

    public PaisDTO(Long id, String codigoIso2, String nombre, EstadoEnum estado, SituacionEconomicaDTO situacionEconomicaDTO) {
        this.id = id;
        this.codigoIso2 = codigoIso2;
        this.nombre = nombre;
        this.estado = estado;
        this.situacionEconomicaDTO = situacionEconomicaDTO;
    }

    //Getters/Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoIso2() {
        return codigoIso2;
    }

    public void setCodigoIso2(String codigoIso2) {
        this.codigoIso2 = codigoIso2;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado;
    }

    public SituacionEconomicaDTO getSituacionEconomicaDTO() {
        return situacionEconomicaDTO;
    }

    public void setSituacionEconomicaDTO(SituacionEconomicaDTO situacionEconomicaDTO) {
        this.situacionEconomicaDTO = situacionEconomicaDTO;
    }
}
