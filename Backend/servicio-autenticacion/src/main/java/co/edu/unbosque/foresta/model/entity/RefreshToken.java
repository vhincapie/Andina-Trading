package co.edu.unbosque.foresta.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="usuario_id")
    private Usuario usuario;

    @Column(nullable=false, unique=true, length=512)
    private String token;

    @Column(name="expira_en", nullable=false)
    private Instant expiraEn;

    @Column(nullable=false) private boolean revocado = false;
    @Column(name="creado_en", nullable=false) private Instant creadoEn = Instant.now();

    public RefreshToken() {
    }

    public RefreshToken(Long id, Usuario usuario, String token, Instant expiraEn, boolean revocado, Instant creadoEn) {
        this.id = id;
        this.usuario = usuario;
        this.token = token;
        this.expiraEn = expiraEn;
        this.revocado = revocado;
        this.creadoEn = creadoEn;
    }

    //Getters/Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(Instant expiraEn) {
        this.expiraEn = expiraEn;
    }

    public boolean isRevocado() {
        return revocado;
    }

    public void setRevocado(boolean revocado) {
        this.revocado = revocado;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}