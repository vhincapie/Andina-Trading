package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IConsolidacionController;
import co.edu.unbosque.foresta.service.interfaces.IConsolidacionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;

@RestController
public class ConsolidacionController implements IConsolidacionController {

    private final IConsolidacionService service;

    public ConsolidacionController(IConsolidacionService service) {
        this.service = service;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void regionalCsv(HttpServletResponse res) {
        export(res, "consolidacion_regional", out -> service.csvRegional(out));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void segmentacionCsv(HttpServletResponse res, String criterio, Long paisId, Double minMonto, Double maxMonto) {
        export(res, "segmentacion_inversionistas", out -> service.csvSegmentacion(out, criterio, paisId, minMonto, maxMonto));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void comisionesCsv(HttpServletResponse res) {
        export(res, "consolidacion_comisiones", out -> service.csvComisiones(out));
    }

    private interface Writer { void write(OutputStream out) throws Exception; }

    private void export(HttpServletResponse res, String filename, Writer writer) {
        try (var out = res.getOutputStream()) {
            res.setContentType("text/csv; charset=UTF-8");
            res.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".csv\"");
            writer.write(out);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error generando CSV: " + e.getMessage(), e);
        }
    }
}
