package co.edu.unbosque.foresta.controller;

import co.edu.unbosque.foresta.service.interfaces.IReporteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final IReporteService service;
    public ReporteController(IReporteService service) { this.service = service; }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/inversionistas")
    public void inversionistas(HttpServletResponse res) {
        setCsvHeaders(res, "reporte_inversionistas");
        stream(res, service::csvInversionistas);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/comisionistas")
    public void comisionistas(HttpServletResponse res) {
        setCsvHeaders(res, "reporte_comisionistas");
        stream(res, service::csvComisionistas);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ordenes")
    public void ordenes(HttpServletResponse res) {
        setCsvHeaders(res, "reporte_ordenes");
        stream(res, service::csvOrdenes);
    }

    private void setCsvHeaders(HttpServletResponse res, String baseName) {
        res.setContentType("text/csv; charset=UTF-8");
        res.setHeader("Content-Disposition", "attachment; filename=\"" + baseName + ".csv\"");
    }
    @FunctionalInterface private interface Writer { void write(java.io.OutputStream out); }
    private void stream(HttpServletResponse res, Writer w) {
        try (var out = res.getOutputStream()) { w.write(out); out.flush(); }
        catch (Exception e) { throw new RuntimeException("Error generando CSV: " + e.getMessage(), e); }
    }
}
