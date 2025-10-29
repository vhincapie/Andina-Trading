package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.CandleDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/market")
public interface IMarketDataController {

    @GetMapping("/candles")
    Map<String, List<CandleDTO>> getCandles(
            @RequestParam(required = false) String symbols,
            @RequestParam(defaultValue = "1Day") String timeframe,
            @RequestParam(defaultValue = "iex") String feed,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "10") Integer lastDays
    );
}