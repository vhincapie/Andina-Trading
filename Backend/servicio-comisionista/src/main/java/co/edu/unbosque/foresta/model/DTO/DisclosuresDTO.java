package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DisclosuresDTO {

    @JsonProperty("is_control_person")
    private boolean controlPerson;

    @JsonProperty("is_affiliated_exchange_or_finra")
    private boolean affiliatedExchangeOrFinra;

    @JsonProperty("is_affiliated_exchange_or_iiroc")
    private boolean affiliatedExchangeOrIiroc;

    @JsonProperty("is_politically_exposed")
    private boolean politicallyExposed;

    @JsonProperty("immediate_family_exposed")
    private boolean immediateFamilyExposed;

    @JsonProperty("is_discretionary")
    private boolean discretionary;

    public DisclosuresDTO() {}

    public DisclosuresDTO(boolean controlPerson,
                          boolean affiliatedExchangeOrFinra,
                          boolean affiliatedExchangeOrIiroc,
                          boolean politicallyExposed,
                          boolean immediateFamilyExposed,
                          boolean discretionary) {
        this.controlPerson = controlPerson;
        this.affiliatedExchangeOrFinra = affiliatedExchangeOrFinra;
        this.affiliatedExchangeOrIiroc = affiliatedExchangeOrIiroc;
        this.politicallyExposed = politicallyExposed;
        this.immediateFamilyExposed = immediateFamilyExposed;
        this.discretionary = discretionary;
    }

    //Getters/Setters
    public boolean isControlPerson() {
        return controlPerson;
    }

    public void setControlPerson(boolean controlPerson) {
        this.controlPerson = controlPerson;
    }

    public boolean isAffiliatedExchangeOrFinra() {
        return affiliatedExchangeOrFinra;
    }

    public void setAffiliatedExchangeOrFinra(boolean affiliatedExchangeOrFinra) {
        this.affiliatedExchangeOrFinra = affiliatedExchangeOrFinra;
    }

    public boolean isAffiliatedExchangeOrIiroc() {
        return affiliatedExchangeOrIiroc;
    }

    public void setAffiliatedExchangeOrIiroc(boolean affiliatedExchangeOrIiroc) {
        this.affiliatedExchangeOrIiroc = affiliatedExchangeOrIiroc;
    }

    public boolean isPoliticallyExposed() {
        return politicallyExposed;
    }

    public void setPoliticallyExposed(boolean politicallyExposed) {
        this.politicallyExposed = politicallyExposed;
    }

    public boolean isImmediateFamilyExposed() {
        return immediateFamilyExposed;
    }

    public void setImmediateFamilyExposed(boolean immediateFamilyExposed) {
        this.immediateFamilyExposed = immediateFamilyExposed;
    }

    public boolean isDiscretionary() {
        return discretionary;
    }

    public void setDiscretionary(boolean discretionary) {
        this.discretionary = discretionary;
    }
}
