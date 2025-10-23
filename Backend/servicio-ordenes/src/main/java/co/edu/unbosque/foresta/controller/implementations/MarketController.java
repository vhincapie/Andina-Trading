package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IMarketAPI;
import co.edu.unbosque.foresta.model.DTO.MarketStatusDTO;
import co.edu.unbosque.foresta.model.DTO.StockDTO;
import co.edu.unbosque.foresta.service.interfaces.IMarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarketController implements IMarketAPI {

    private final IMarketService marketService;

    public MarketController(IMarketService marketService) {
        this.marketService = marketService;
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<MarketStatusDTO> status() {
        return ResponseEntity.ok(marketService.getMarketStatus());
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<StockDTO> quote(String symbol) {
        return ResponseEntity.ok(marketService.getQuote(symbol));
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<List<StockDTO>> search(String query) {
        return ResponseEntity.ok(marketService.searchSymbols(query));
    }
}
