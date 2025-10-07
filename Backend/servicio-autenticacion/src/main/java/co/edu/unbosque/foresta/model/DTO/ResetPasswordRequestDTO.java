package co.edu.unbosque.foresta.model.DTO;


import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequestDTO {
    @NotBlank
    private String token;
    @NotBlank
    private String nuevaContrasena;

    public ResetPasswordRequestDTO() {
    }

    public ResetPasswordRequestDTO(String token, String nuevaContrasena) {
        this.token = token;
        this.nuevaContrasena = nuevaContrasena;
    }

    //Getters/Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}