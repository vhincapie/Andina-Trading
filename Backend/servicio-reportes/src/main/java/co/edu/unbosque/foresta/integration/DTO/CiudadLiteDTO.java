package co.edu.unbosque.foresta.integration.DTO;

public class CiudadLiteDTO {
    private Long id;
    private String nombre;
    private PaisLiteDTO paisDTO;

    public CiudadLiteDTO() {}
    public CiudadLiteDTO(Long id, String nombre) { this.id = id; this.nombre = nombre; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public PaisLiteDTO getPaisDTO() { return paisDTO; }
    public void setPaisDTO(PaisLiteDTO paisDTO) { this.paisDTO = paisDTO; }
}
