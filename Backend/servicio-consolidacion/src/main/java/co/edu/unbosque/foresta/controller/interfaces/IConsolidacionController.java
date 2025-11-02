package co.edu.unbosque.foresta.controller.interfaces;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/consolidacion")
public interface IConsolidacionController {

    @GetMapping("/regional/csv")
    void regionalCsv(HttpServletResponse res);

    @GetMapping("/segmentacion/csv")
    void segmentacionCsv(HttpServletResponse res,
                         @RequestParam(value="criterio", required=false) String criterio,
                         @RequestParam(value="paisId", required=false) Long paisId,
                         @RequestParam(value="minMonto", required=false) Double minMonto,
                         @RequestParam(value="maxMonto", required=false) Double maxMonto);

    @GetMapping("/comisiones/csv")
    void comisionesCsv(HttpServletResponse res);
}
