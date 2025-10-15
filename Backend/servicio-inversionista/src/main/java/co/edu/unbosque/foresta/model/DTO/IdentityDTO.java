package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityDTO {

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    // Alpaca espera YYYY-MM-DD
    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    @JsonProperty("tax_id")
    private String taxId;

    // "national_id" | "passport" | "other"
    @JsonProperty("tax_id_type")
    private String taxIdType;

    @JsonProperty("country_of_citizenship")
    private String countryOfCitizenship;

    @JsonProperty("country_of_birth")
    private String countryOfBirth;

    @JsonProperty("country_of_tax_residence")
    private String countryOfTaxResidence;

    public IdentityDTO() {

    }

    public IdentityDTO(String givenName, String familyName, String dateOfBirth, String taxId, String taxIdType, String countryOfCitizenship, String countryOfBirth, String countryOfTaxResidence) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.dateOfBirth = dateOfBirth;
        this.taxId = taxId;
        this.taxIdType = taxIdType;
        this.countryOfCitizenship = countryOfCitizenship;
        this.countryOfBirth = countryOfBirth;
        this.countryOfTaxResidence = countryOfTaxResidence;
    }

    //Getters/Setters
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTaxIdType() {
        return taxIdType;
    }

    public void setTaxIdType(String taxIdType) {
        this.taxIdType = taxIdType;
    }

    public String getCountryOfCitizenship() {
        return countryOfCitizenship;
    }

    public void setCountryOfCitizenship(String countryOfCitizenship) {
        this.countryOfCitizenship = countryOfCitizenship;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public String getCountryOfTaxResidence() {
        return countryOfTaxResidence;
    }

    public void setCountryOfTaxResidence(String countryOfTaxResidence) {
        this.countryOfTaxResidence = countryOfTaxResidence;
    }
}
