package co.edu.unbosque.foresta.model.DTO;

public class PaisLiteDTO {
    private Long id;
    private String iso2;

    public PaisLiteDTO() {
    }

    public PaisLiteDTO(Long id, String iso2) {
        this.id = id;
        this.iso2 = iso2;
    }

    //Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }
}