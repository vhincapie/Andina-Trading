package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.CandleDTO;

import java.util.List;
import java.util.Map;

public interface IMarketDataService {
      Map<String, List<CandleDTO>> getCandles(
            List<String> symbols, String timeframe, String feed,
            String startIso, String endIso, Integer limit
    );
}
