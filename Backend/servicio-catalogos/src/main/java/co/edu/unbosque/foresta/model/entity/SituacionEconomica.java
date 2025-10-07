package co.edu.unbosque.foresta.model.entity;
import co.edu.unbosque.foresta.model.enums.EstadoEnum;
import jakarta.persistence.*;

@Entity
@Table(
        name = "situaciones_economicas",
        uniqueConstraints = @UniqueConstraint(name = "uk_sit_eco_nombre", columnNames = "nombre"))
public class SituacionEconomica {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 400)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnum estado = EstadoEnum.ACTIVO;


    public SituacionEconomica() {
    }

    public SituacionEconomica(Long id, String nombre, String descripcion, EstadoEnum estado) {
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
