package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.integration.*;
import co.edu.unbosque.foresta.integration.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.integration.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.integration.DTO.OrdenDTO;
import co.edu.unbosque.foresta.service.interfaces.IReporteService;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ReporteServiceImpl implements IReporteService {

    private static final byte[] UTF8_BOM = new byte[] {(byte)0xEF,(byte)0xBB,(byte)0xBF};
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InversionistasClient invClient;
    private final ComisionistasClient comClient;
    private final OrdenesClient ordClient;

    public ReporteServiceImpl(InversionistasClient invClient, ComisionistasClient comClient, OrdenesClient ordClient) {
        this.invClient = invClient;
        this.comClient = comClient;
        this.ordClient = ordClient;
    }

    // ==================== INVERSIONISTAS ====================
    @Override
    public void csvInversionistas(OutputStream out) {
        List<InversionistaDTO> data = invClient.listar();
        // Orden: Apellido, Nombre, ID
        data.sort(Comparator
                .comparing((InversionistaDTO i) -> nullSafe(i.getApellido()))
                .thenComparing(i -> nullSafe(i.getNombre()))
                .thenComparing(i -> i.getId() == null ? Long.MAX_VALUE : i.getId()));

        writeHeader(out, "id,correo,nombre,apellido,tipo_documento,numero_documento,pais_id,ciudad_id\n");

        for (var i : data) {
            line(out,
                    i.getId(),
                    i.getCorreo(),
                    i.getNombre(),
                    i.getApellido(),
                    i.getTipoDocumento(),
                    i.getNumeroDocumento(),
                    i.getPaisId(),
                    i.getCiudadId()
            );
        }
    }

    // ==================== COMISIONISTAS ====================
    @Override
    public void csvComisionistas(OutputStream out) {
        List<ComisionistaDTO> data = comClient.listar();
        // Orden: Apellido, Nombre, ID
        data.sort(Comparator
                .comparing((ComisionistaDTO c) -> nullSafe(c.getApellido()))
                .thenComparing(c -> nullSafe(c.getNombre()))
                .thenComparing(c -> c.getId() == null ? Long.MAX_VALUE : c.getId()));

        writeHeader(out, "id,correo,nombre,apellido,tipo_documento,numero_documento,anios_experiencia,pais_id,ciudad_id\n");

        for (var c : data) {
            line(out,
                    c.getId(),
                    c.getCorreo(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getTipoDocumento(),
                    c.getNumeroDocumento(),
                    c.getAniosExperiencia(),
                    c.getPaisId(),
                    c.getCiudadId()
            );
        }
    }

    // ==================== ORDENES ====================
    @Override
    public void csvOrdenes(OutputStream out) {
        List<OrdenDTO> data = ordClient.listarTodas();
        // Orden: createdAt DESC, luego id DESC
        data.sort(Comparator
                .comparing(OrdenDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                .thenComparing((OrdenDTO o) -> o.getId() == null ? Long.MIN_VALUE : o.getId()).reversed());

        writeHeader(out, "id,created_at,symbol,side,order_type,status,qty,limit_price,stop_price,transaction_amount,commission_amount,net_amount,inversionista_id,comisionista_id\n");

        for (var o : data) {
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
                    o.getInversionistaId(),
                    o.getComisionistaId()
            );
        }
    }

    // ==================== HELPERS CSV ====================

    /** Escribe BOM + "sep=," para que Excel interprete las columnas correctamente. */
    private static void writeHeader(OutputStream out, String header) {
        writeBom(out);
        writeUtf8(out, "sep=,\n"); // 👈 fuerza separador de coma en Excel
        writeUtf8(out, header);
    }

    private static void writeBom(OutputStream out) {
        try { out.write(UTF8_BOM); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    private static void writeUtf8(OutputStream out, String s) {
        try { out.write(s.getBytes(StandardCharsets.UTF_8)); }
        catch (Exception e) { throw new RuntimeException(e); }
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

    private static String value(Object o) {
        if (o == null) return "";
        return String.valueOf(o);
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needs = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needs ? "\"" + v + "\"" : v;
    }
}
