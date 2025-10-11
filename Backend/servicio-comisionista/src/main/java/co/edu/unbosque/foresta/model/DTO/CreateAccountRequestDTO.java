package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAccountRequestDTO {

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("contact")
    private ContactDTO contact;

    @JsonProperty("identity")
    private IdentityDTO identity;

    @JsonProperty("disclosures")
    private DisclosuresDTO disclosures;

    @JsonProperty("additional_information")
    private String additionalInformation;

    @JsonProperty("enabled_assets")
    private List<String> enabledAssets;

    @JsonProperty("documents")
    private List<DocumentDTO> documents;

    @JsonProperty("trusted_contact")
    private TrustedContactDTO trustedContact;

    @JsonProperty("agreements")
    private List<AgreementDTO> agreements;

    @JsonProperty("password")
    private String password;

    public CreateAccountRequestDTO() {

    }

    public CreateAccountRequestDTO(String accountType, ContactDTO contact, IdentityDTO identity, DisclosuresDTO disclosures, String additionalInformation, List<String> enabledAssets, List<DocumentDTO> documents, TrustedContactDTO trustedContact, List<AgreementDTO> agreements, String password) {
        this.accountType = accountType;
        this.contact = contact;
        this.identity = identity;
        this.disclosures = disclosures;
        this.additionalInformation = additionalInformation;
        this.enabledAssets = enabledAssets;
        this.documents = documents;
        this.trustedContact = trustedContact;
        this.agreements = agreements;
        this.password = password;
    }

    //Getters/Setters
    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public ContactDTO getContact() {
        return contact;
    }
    public void setContact(ContactDTO contact) {
        this.contact = contact;
    }

    public IdentityDTO getIdentity() {
        return identity;
    }
    public void setIdentity(IdentityDTO identity) {
        this.identity = identity;
    }

    public DisclosuresDTO getDisclosures() {
        return disclosures;
    }
    public void setDisclosures(DisclosuresDTO disclosures) {
        this.disclosures = disclosures;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public List<String> getEnabledAssets() {
        return enabledAssets;
    }
    public void setEnabledAssets(List<String> enabledAssets) {
        this.enabledAssets = enabledAssets;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }
    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

    public TrustedContactDTO getTrustedContact() {
        return trustedContact;
    }
    public void setTrustedContact(TrustedContactDTO trustedContact) {
        this.trustedContact = trustedContact;
    }

    public List<AgreementDTO> getAgreements() {
        return agreements;
    }
    public void setAgreements(List<AgreementDTO> agreements) {
        this.agreements = agreements;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
