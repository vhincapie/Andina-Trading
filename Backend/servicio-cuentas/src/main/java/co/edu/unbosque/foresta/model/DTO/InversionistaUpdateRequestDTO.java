package co.edu.unbosque.foresta.model.DTO;

import java.time.LocalDate;

public class InversionistaUpdateRequestDTO {

    private Long paisId;
    private Long ciudadId;

    public InversionistaUpdateRequestDTO() {
    }

    public InversionistaUpdateRequestDTO(Long paisId, Long ciudadId) {
        this.paisId = paisId;
        this.ciudadId = ciudadId;
    }

    //Getters/Setters
    public Long getPaisId() {
        return paisId;
    }

    public void setPaisId(Long paisId) {
        this.paisId = paisId;
    }

    public Long getCiudadId() {
        return ciudadId;
    }

    public void setCiudadId(Long ciudadId) {
        this.ciudadId = ciudadId;
    }
}

