package co.edu.unbosque.foresta.model.entity;

import co.edu.unbosque.foresta.model.enums.EstadoEnum;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=180)
    private String correo;

    @Column(name="contrasena_hash", nullable=false, length=255)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private EstadoEnum estado = EstadoEnum.ACTIVO;

    @Column(name="intentos_fallidos", nullable=false)
    private Integer intentosFallidos = 0;

    @Column(name="ultimo_login")
    private Instant ultimoLogin;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;


    public Usuario() {
    }

    public Usuario(Long id, String correo, String contrasenaHash, EstadoEnum estado, Integer intentosFallidos, Instant ultimoLogin, Rol rol) {
        this.id = id;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.estado = estado;
        this.intentosFallidos = intentosFallidos;
        this.ultimoLogin = ultimoLogin;
        this.rol = rol;
    }

    //Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public EstadoEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado;
    }

    public Integer getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(Integer intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public Instant getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(Instant ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
