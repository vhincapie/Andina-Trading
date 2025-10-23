package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCreateRequestDTO {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("qty")
    private String qty;

    @JsonProperty("side")
    private String side;

    @JsonProperty("type")
    private String type;

    @JsonProperty("time_in_force")
    private String timeInForce;

    @JsonProperty("limit_price")
    private String limitPrice;

    @JsonProperty("stop_price")
    private String stopPrice;

    public OrderCreateRequestDTO() {}

    // Getters/Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getQty() { return qty; }
    public void setQty(String qty) { this.qty = qty; }

    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTimeInForce() { return timeInForce; }
    public void setTimeInForce(String timeInForce) { this.timeInForce = timeInForce; }

    public String getLimitPrice() { return limitPrice; }
    public void setLimitPrice(String limitPrice) { this.limitPrice = limitPrice; }

    public String getStopPrice() { return stopPrice; }
    public void setStopPrice(String stopPrice) { this.stopPrice = stopPrice; }
}
