package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgreementDTO {

    @JsonProperty("agreement")
    private String agreement;

    @JsonProperty("signed_at")
    private String signedAt;

    @JsonProperty("ip_address")
    private String ipAddress;

    public AgreementDTO() {}

    public AgreementDTO(String agreement, String signedAt, String ipAddress) {
        this.agreement = agreement;
        this.signedAt = signedAt;
        this.ipAddress = ipAddress;
    }

    //Getters/Setters
    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public String getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(String signedAt) {
        this.signedAt = signedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
