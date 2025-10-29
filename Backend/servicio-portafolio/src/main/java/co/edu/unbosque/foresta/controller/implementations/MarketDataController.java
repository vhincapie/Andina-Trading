package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IMarketDataController;
import co.edu.unbosque.foresta.model.DTO.CandleDTO;
import co.edu.unbosque.foresta.service.interfaces.IMarketDataService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
public class MarketDataController implements IMarketDataController {

    private final IMarketDataService service;

    public MarketDataController(IMarketDataService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasRole('INVERSIONISTA')")
    public Map<String, List<CandleDTO>> getCandles(
            String symbols,
            String timeframe,
            String feed,
            String start,
            String end,
            Integer lastDays
    ) {
        if (start == null || start.isBlank() || end == null || end.isBlank()) {
            Instant now = Instant.now();
            end = now.toString();
            start = now.minus(lastDays != null ? lastDays : 10, ChronoUnit.DAYS).toString();
        }

        List<String> list = null;
        if (symbols != null && !symbols.isBlank()) {
            list = Arrays.asList(symbols.split("\\s*,\\s*"));
        }

        return service.getCandles(list, timeframe, feed, start, end, 1000);
    }

}