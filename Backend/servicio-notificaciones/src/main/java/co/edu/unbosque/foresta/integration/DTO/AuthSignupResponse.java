package co.edu.unbosque.foresta.integration.DTO;

public class AuthSignupResponse {
    private Long usuarioId;
    private String rol;

    public AuthSignupResponse() {
    }

    public AuthSignupResponse(Long usuarioId, String rol) {
        this.usuarioId = usuarioId;
        this.rol = rol;
    }

    //Getters/Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
