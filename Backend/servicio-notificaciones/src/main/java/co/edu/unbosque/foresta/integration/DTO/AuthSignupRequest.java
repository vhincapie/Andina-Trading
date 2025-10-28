package co.edu.unbosque.foresta.integration.DTO;

public class AuthSignupRequest {
    private String correo;
    private String contrasena;

    public AuthSignupRequest() {
    }

    public AuthSignupRequest(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    //Getters/Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
