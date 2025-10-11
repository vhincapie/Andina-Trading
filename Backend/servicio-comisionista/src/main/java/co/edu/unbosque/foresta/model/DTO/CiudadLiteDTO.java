package co.edu.unbosque.foresta.model.DTO;

public class CiudadLiteDTO {
    private Long id;
    private String nombre;
    private String estadoCodigo;

    public CiudadLiteDTO() {
    }

    public CiudadLiteDTO(Long id, String nombre, String estadoCodigo) {
        this.id = id;
        this.nombre = nombre;
        this.estadoCodigo = estadoCodigo;
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

    public String getEstadoCodigo() {
        return estadoCodigo;
    }

    public void setEstadoCodigo(String estadoCodigo) {
        this.estadoCodigo = estadoCodigo;
    }
}
