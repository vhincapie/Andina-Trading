package co.edu.unbosque.foresta.model.DTO;

import co.edu.unbosque.foresta.model.enums.EstadoEnum;

public class SituacionEconomicaDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private EstadoEnum estado = EstadoEnum.ACTIVO;

    public SituacionEconomicaDTO() {
    }

    public SituacionEconomicaDTO(Long id, String nombre, String descripcion, EstadoEnum estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado;
    }


}
