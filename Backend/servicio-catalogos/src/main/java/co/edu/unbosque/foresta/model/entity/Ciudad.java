package co.edu.unbosque.foresta.model.entity;

import co.edu.unbosque.foresta.model.enums.EstadoEnum;
import jakarta.persistence.*;

@Entity @Table(name="ciudades")
public class Ciudad {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(length=150, nullable=false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private EstadoEnum estado = EstadoEnum.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="pais_id", nullable=false)
    private Pais pais;


    public Ciudad() {
    }

    public Ciudad(Long id, String nombre, EstadoEnum estado, Pais pais) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.pais = pais;
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

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public EstadoEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado;
    }



}
