package co.edu.unbosque.foresta.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class PositionDTO {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("qty")
    private String qty;

    @JsonProperty("avg_entry_price")
    private BigDecimal avgEntryPrice;

    @JsonProperty("market_value")
    private BigDecimal marketValue;

    @JsonProperty("unrealized_pl")
    private BigDecimal unrealizedPl;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getQty() { return qty; }
    public void setQty(String qty) { this.qty = qty; }

    public BigDecimal getAvgEntryPrice() { return avgEntryPrice; }
    public void setAvgEntryPrice(BigDecimal avgEntryPrice) { this.avgEntryPrice = avgEntryPrice; }

    public BigDecimal getMarketValue() { return marketValue; }
    public void setMarketValue(BigDecimal marketValue) { this.marketValue = marketValue; }

    public BigDecimal getUnrealizedPl() { return unrealizedPl; }
    public void setUnrealizedPl(BigDecimal unrealizedPl) { this.unrealizedPl = unrealizedPl; }
}
