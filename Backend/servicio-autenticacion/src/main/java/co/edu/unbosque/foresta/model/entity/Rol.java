package co.edu.unbosque.foresta.model.entity;

import co.edu.unbosque.foresta.model.enums.RolEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RolEnum nombre;

    public Rol() {
    }

    public Rol(Long id, RolEnum nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    //Getters/Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RolEnum getNombre() {
        return nombre;
    }

    public void setNombre(RolEnum nombre) {
        this.nombre = nombre;
    }
}
