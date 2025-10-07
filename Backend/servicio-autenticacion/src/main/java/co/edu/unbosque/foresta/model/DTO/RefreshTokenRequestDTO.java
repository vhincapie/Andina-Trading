package co.edu.unbosque.foresta.model.DTO;


import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDTO {
    @NotBlank
    private String refreshToken;

    public RefreshTokenRequestDTO() {
    }

    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //Getters/Setters

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}