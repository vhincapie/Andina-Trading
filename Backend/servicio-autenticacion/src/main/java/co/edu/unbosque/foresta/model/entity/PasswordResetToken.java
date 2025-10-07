package co.edu.unbosque.foresta.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="password_reset_tokens")
    public class PasswordResetToken {
        @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(optional=false) @JoinColumn(name="usuario_id")
        private Usuario usuario;

        @Column(nullable=false, unique=true, length=256)
        private String token;

        @Column(name="expira_en", nullable=false)
        private Instant expiraEn;

        @Column(nullable=false) private boolean usado = false;
        @Column(name="creado_en", nullable=false) private Instant creadoEn = Instant.now();

    public PasswordResetToken() {
    }

    public PasswordResetToken(Long id, Usuario usuario, String token, Instant expiraEn, boolean usado, Instant creadoEn) {
        this.id = id;
        this.usuario = usuario;
        this.token = token;
        this.expiraEn = expiraEn;
        this.usado = usado;
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

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}
