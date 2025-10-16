package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradingDetailDTO {
    private String equity;
    private String cash;
    @JsonProperty("buying_power")
    private String buyingPower;

    public TradingDetailDTO() {
    }

    public TradingDetailDTO(String equity, String cash, String buyingPower) {
        this.equity = equity;
        this.cash = cash;
        this.buyingPower = buyingPower;
    }

    //Getters/Setters
    public String getEquity() {
        return equity;
    }

    public void setEquity(String equity) {
        this.equity = equity;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getBuyingPower() {
        return buyingPower;
    }

    public void setBuyingPower(String buyingPower) {
        this.buyingPower = buyingPower;
    }
}