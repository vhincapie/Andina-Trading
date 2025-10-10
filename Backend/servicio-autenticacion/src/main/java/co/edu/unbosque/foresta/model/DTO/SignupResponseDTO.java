package co.edu.unbosque.foresta.model.DTO;

import co.edu.unbosque.foresta.model.enums.RolEnum;

public class SignupResponseDTO {
    private Long usuarioId;
    private RolEnum rol;

    public SignupResponseDTO() {

    }

    public SignupResponseDTO(Long usuarioId, RolEnum rol) {
        this.usuarioId = usuarioId; this.rol = rol;
    }

    //Getters/Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public RolEnum getRol() {
        return rol;
    }

    public void setRol(RolEnum rol) {
        this.rol = rol;
    }
}
