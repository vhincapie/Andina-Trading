package co.edu.unbosque.foresta.model.DTO;

import co.edu.unbosque.foresta.model.enums.EstadoEnum;

public class CiudadDTO {
    private Long id;
    private String nombre;
    private EstadoEnum estado = EstadoEnum.ACTIVO;
    private PaisDTO paisDTO;

    public CiudadDTO() {
    }

    public CiudadDTO(Long id, String nombre, EstadoEnum estado, PaisDTO paisDTO) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.paisDTO = paisDTO;
    }

    //Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PaisDTO getPaisDTO() {
        return paisDTO;
    }

    public void setPaisDTO(PaisDTO paisDTO) {
        this.paisDTO = paisDTO;
    }
}
