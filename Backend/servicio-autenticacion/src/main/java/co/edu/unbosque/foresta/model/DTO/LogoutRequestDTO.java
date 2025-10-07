package co.edu.unbosque.foresta.model.DTO;

public class LogoutRequestDTO {
    private String refreshToken;

    public LogoutRequestDTO() {
    }

    public LogoutRequestDTO(String refreshToken) {
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
