package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradingDetailDTO {

    @JsonProperty("buying_power")
    private String buyingPower;

    public TradingDetailDTO() {
    }

    public TradingDetailDTO(String buyingPower) {
        this.buyingPower = buyingPower;
    }

    public String getBuyingPower() {
        return buyingPower;
    }

    public void setBuyingPower(String buyingPower) {
        this.buyingPower = buyingPower;
    }
}

