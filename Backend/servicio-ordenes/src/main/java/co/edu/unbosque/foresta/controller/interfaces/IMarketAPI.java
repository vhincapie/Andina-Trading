package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.MarketStatusDTO;
import co.edu.unbosque.foresta.model.DTO.StockDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/mercado")
public interface IMarketAPI {

    @GetMapping("/market-status")
    ResponseEntity<MarketStatusDTO> status();

    @GetMapping("/quote")
    ResponseEntity<StockDTO> quote(@RequestParam("symbol") String symbol);

    @GetMapping("/search")
    ResponseEntity<List<StockDTO>> search(@RequestParam("query") String query);
}
