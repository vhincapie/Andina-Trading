package co.edu.unbosque.foresta.model.DTO;

import co.edu.unbosque.foresta.model.enums.RolEnum;

public class UsuarioDTO {
    private Long id;
    private String correo;
    private RolEnum rol;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String correo, RolEnum rol) {
        this.id = id;
        this.correo = correo;
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

    public RolEnum getRol() {
        return rol;
    }

    public void setRol(RolEnum rol) {
        this.rol = rol;
    }
}
