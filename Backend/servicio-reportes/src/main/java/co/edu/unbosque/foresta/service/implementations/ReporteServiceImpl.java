package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.integration.*;
import co.edu.unbosque.foresta.integration.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.integration.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.integration.DTO.OrdenDTO;
import co.edu.unbosque.foresta.integration.DTO.CiudadLiteDTO;
import co.edu.unbosque.foresta.integration.DTO.PaisLiteDTO;
import co.edu.unbosque.foresta.service.interfaces.IReporteService;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReporteServiceImpl implements IReporteService {

    private static final byte[] UTF8_BOM = new byte[] {(byte)0xEF,(byte)0xBB,(byte)0xBF};
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InversionistasClient invClient;
    private final ComisionistasClient comClient;
    private final OrdenesClient ordClient;
    private final CatalogosClient catClient;

    private final Map<Long, String> paisCache = new ConcurrentHashMap<>();
    private final Map<Long, String> ciudadCache = new ConcurrentHashMap<>();
    private final Map<Long, String> invNombreCache = new ConcurrentHashMap<>();
    private final Map<Long, String> comNombreCache = new ConcurrentHashMap<>();

    public ReporteServiceImpl(InversionistasClient invClient,
                              ComisionistasClient comClient,
                              OrdenesClient ordClient,
                              CatalogosClient catClient) {
        this.invClient = invClient;
        this.comClient = comClient;
        this.ordClient = ordClient;
        this.catClient = catClient;
    }

    // ==================== INVERSIONISTAS ====================
    @Override
    public void csvInversionistas(OutputStream out) {
        List<InversionistaDTO> data = invClient.listar();
        data.sort(Comparator
                .comparing((InversionistaDTO i) -> nullSafe(i.getApellido()))
                .thenComparing(i -> nullSafe(i.getNombre()))
                .thenComparing(i -> i.getId() == null ? Long.MAX_VALUE : i.getId()));

        writeHeader(out, "id,correo,nombre,apellido,tipo_documento,numero_documento,pais,ciudad\n");

        for (var i : data) {
            line(out,
                    i.getId(),
                    i.getCorreo(),
                    i.getNombre(),
                    i.getApellido(),
                    i.getTipoDocumento(),
                    i.getNumeroDocumento(),
                    nombrePais(i.getPaisId()),
                    nombreCiudad(i.getCiudadId())
            );
        }
    }

    // ==================== COMISIONISTAS ====================
    @Override
    public void csvComisionistas(OutputStream out) {
        List<ComisionistaDTO> data = comClient.listar();
        data.sort(Comparator
                .comparing((ComisionistaDTO c) -> nullSafe(c.getApellido()))
                .thenComparing(c -> nullSafe(c.getNombre()))
                .thenComparing(c -> c.getId() == null ? Long.MAX_VALUE : c.getId()));

        writeHeader(out, "id,correo,nombre,apellido,tipo_documento,numero_documento,anios_experiencia,pais,ciudad\n");

        for (var c : data) {
            line(out,
                    c.getId(),
                    c.getCorreo(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getTipoDocumento(),
                    c.getNumeroDocumento(),
                    c.getAniosExperiencia(),
                    nombrePais(c.getPaisId()),
                    nombreCiudad(c.getCiudadId())
            );
        }
    }

    // ==================== ORDENES ====================
    @Override
    public void csvOrdenes(OutputStream out) {
        List<OrdenDTO> data = ordClient.listarTodas();
        data.sort(Comparator
                .comparing(OrdenDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                .thenComparing((OrdenDTO o) -> o.getId() == null ? Long.MIN_VALUE : o.getId()).reversed());

        writeHeader(out, "id,created_at,symbol,side,order_type,status,qty,limit_price,stop_price,transaction_amount,commission_amount,net_amount,inversionista,comisionista\n");

        for (var o : data) {
            String invNombre = nombreInversionista(o.getInversionistaId());
            String comNombre = nombreComisionista(o.getComisionistaId());

            line(out,
                    o.getId(),
                    o.getCreatedAt() == null ? "" : TS.format(o.getCreatedAt()),
                    o.getSymbol(),
                    o.getSide(),
                    o.getOrderType(),
                    o.getStatus(),
                    o.getQty(),
                    o.getLimitPrice(),
                    o.getStopPrice(),
                    o.getTransactionAmount(),
                    o.getCommissionAmount(),
                    o.getNetAmount(),
                    invNombre,
                    comNombre
            );
        }
    }

    // ==================== HELPERS CSV ====================

    private static void writeHeader(OutputStream out, String header) {
        writeBom(out);
        writeUtf8(out, "sep=,\n");
        writeUtf8(out, header);
    }
    private static void writeBom(OutputStream out) {
        try { out.write(UTF8_BOM); } catch (Exception e) { throw new RuntimeException(e); }
    }
    private static void writeUtf8(OutputStream out, String s) {
        try { out.write(s.getBytes(StandardCharsets.UTF_8)); } catch (Exception e) { throw new RuntimeException(e); }
    }
    private static String nullSafe(String s) { return s == null ? "" : s; }

    private static void line(OutputStream out, Object... cols) {
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escapeCsv(value(cols[i])));
        }
        sb.append('\n');
        writeUtf8(out, sb.toString());
    }
    private static String value(Object o) { return o == null ? "" : String.valueOf(o); }
    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needs = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needs ? "\"" + v + "\"" : v;
    }

    // ==================== HELPERS NOMBRES ====================

    private String nombrePais(Long id) {
        if (id == null) return "";
        return paisCache.computeIfAbsent(id, key -> {
            try { PaisLiteDTO p = catClient.obtenerPais(key); return p != null ? nullSafe(p.getNombre()) : ""; }
            catch (Exception e) { return ""; }
        });
    }

    private String nombreCiudad(Long id) {
        if (id == null) return "";
        return ciudadCache.computeIfAbsent(id, key -> {
            try { CiudadLiteDTO c = catClient.obtenerCiudad(key); return c != null ? nullSafe(c.getNombre()) : ""; }
            catch (Exception e) { return ""; }
        });
    }

    private String nombreInversionista(Long id) {
        if (id == null) return "";
        return invNombreCache.computeIfAbsent(id, key -> {
            try {
                InversionistaDTO i = invClient.obtenerPorId(key);
                if (i == null) return "";
                String nom = nullSafe(i.getNombre());
                String ape = nullSafe(i.getApellido());
                return (nom + " " + ape).trim();
            } catch (Exception e) { return ""; }
        });
    }

    private String nombreComisionista(Long id) {
        if (id == null) return "";
        return comNombreCache.computeIfAbsent(id, key -> {
            try {
                ComisionistaDTO c = comClient.obtenerPorId(key);
                if (c == null) return "";
                String nom = nullSafe(c.getNombre());
                String ape = nullSafe(c.getApellido());
                return (nom + " " + ape).trim();
            } catch (Exception e) { return ""; }
        });
    }
}
