package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.MarketStatusDTO;
import co.edu.unbosque.foresta.model.DTO.StockDTO;

import java.util.List;

public interface IMarketService {
    MarketStatusDTO getMarketStatus();
    StockDTO getQuote(String symbol);
    List<StockDTO> searchSymbols(String query);
}
