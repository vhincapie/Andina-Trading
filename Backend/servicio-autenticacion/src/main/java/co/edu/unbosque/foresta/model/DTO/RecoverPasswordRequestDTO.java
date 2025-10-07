package co.edu.unbosque.foresta.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RecoverPasswordRequestDTO {
    @Email
    @NotBlank
    private String correo;

    public RecoverPasswordRequestDTO() {
    }

    public RecoverPasswordRequestDTO(String correo) {
        this.correo = correo;
    }

    //Getters/Setters

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
