package co.edu.unbosque.foresta.model.entity;

import co.edu.unbosque.foresta.model.enums.EstadoEnum;
import jakarta.persistence.*;

@Entity @Table(name="paises")
public class Pais {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="codigo_iso3", length=2, nullable=false, unique=true)
    private String codigoIso3;

    @Column(name="nombre", length=120, nullable=false, unique=true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private EstadoEnum estado = EstadoEnum.ACTIVO;

    @ManyToOne
    @JoinColumn(name = "situacion_economica_id",
            foreignKey = @ForeignKey(name = "fk_pais_sit_eco"))
    private SituacionEconomica situacionEconomica;

    public Pais() {
    }

    public Pais(Long id, String codigoIso2, String nombre, EstadoEnum estado, SituacionEconomica situacionEconomica) {
        this.id = id;
        this.codigoIso3 = codigoIso2;
        this.nombre = nombre;
        this.estado = estado;
        this.situacionEconomica = situacionEconomica;
    }

    //Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoIso3() {
        return codigoIso3;
    }

    public void setCodigoIso3(String codigoIso2) {
        this.codigoIso3 = codigoIso2;
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

    public SituacionEconomica getSituacionEconomica() {
        return situacionEconomica;
    }

    public void setSituacionEconomica(SituacionEconomica situacionEconomica) {
        this.situacionEconomica = situacionEconomica;
    }
}
