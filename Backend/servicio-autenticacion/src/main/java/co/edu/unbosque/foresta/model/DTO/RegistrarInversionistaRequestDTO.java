package co.edu.unbosque.foresta.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegistrarInversionistaRequestDTO {
    @Email @NotBlank
    private String correo;
    @NotBlank
    private String contrasena;

    public RegistrarInversionistaRequestDTO() {
    }

    public RegistrarInversionistaRequestDTO(String correo, String contrasena) {
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
