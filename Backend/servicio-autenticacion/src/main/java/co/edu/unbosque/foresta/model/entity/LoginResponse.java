package co.edu.unbosque.foresta.model.entity;

import co.edu.unbosque.foresta.model.DTO.UsuarioDTO;
import co.edu.unbosque.foresta.model.enums.RolEnum;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiraEn;
    private RolEnum rol;
    private UsuarioDTO usuario;


    public LoginResponse() {
    }

    public LoginResponse(String accessToken, String refreshToken, long expiraEn, RolEnum rol, UsuarioDTO usuario) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiraEn = expiraEn;
        this.rol = rol;
        this.usuario = usuario;
    }

    //Getters/Setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(long expiraEn) {
        this.expiraEn = expiraEn;
    }

    public RolEnum getRol() {
        return rol;
    }

    public void setRol(RolEnum rol) {
        this.rol = rol;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }
}
