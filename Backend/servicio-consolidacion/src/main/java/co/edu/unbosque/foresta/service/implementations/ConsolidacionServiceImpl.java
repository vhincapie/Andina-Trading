package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.integration.CatalogosClient;
import co.edu.unbosque.foresta.integration.ComisionistasClient;
import co.edu.unbosque.foresta.integration.InversionistasClient;
import co.edu.unbosque.foresta.integration.OrdenesClient;
import co.edu.unbosque.foresta.integration.DTO.ComisionistaDTO;
import co.edu.unbosque.foresta.integration.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.integration.DTO.OrdenDTO;
import co.edu.unbosque.foresta.integration.DTO.PaisDTO;
import co.edu.unbosque.foresta.service.interfaces.IConsolidacionService;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConsolidacionServiceImpl implements IConsolidacionService {

    private static final byte[] UTF8_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private final InversionistasClient invClient;
    private final ComisionistasClient comClient;
    private final OrdenesClient ordClient;
    private final CatalogosClient catClient;

    public ConsolidacionServiceImpl(InversionistasClient invClient,
                                    ComisionistasClient comClient,
                                    OrdenesClient ordClient,
                                    CatalogosClient catClient) {
        this.invClient = invClient;
        this.comClient = comClient;
        this.ordClient = ordClient;
        this.catClient = catClient;
    }

    @Override
    public void csvRegional(OutputStream out) {
        writeHeader(out, "pais,total_inversionistas,total_ordenes,monto_transado,comisiones,neto\n");
        boolean wrote = false;

        List<InversionistaDTO> inversionistas = safe(invClient.listar());
        List<OrdenDTO> ordenes = safe(ordClient.listarTodas());

        List<PaisDTO> paises;
        try {
            paises = safe(catClient.listarPaises());
        } catch (Exception ex) {
            paises = List.of();
        }

        Map<Long, String> nombrePais = paises.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(PaisDTO::getId, PaisDTO::getNombre, (a, b) -> a));

        Function<Long, String> labelPais = id -> {
            if (id == null || id < 0) return "SIN PAÍS";
            return nombrePais.getOrDefault(id, "PAÍS " + id);
        };

        Map<Long, Long> invPais = inversionistas.stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(InversionistaDTO::getId, i -> ns(i.getPaisId()), (a, b) -> a));

        Map<Long, Long> invsPorPais = inversionistas.stream()
                .collect(Collectors.groupingBy(i -> ns(i.getPaisId()), Collectors.counting()));

        class Acc {
            long invs;
            long ords;
            BigDecimal trx = bd0();
            BigDecimal com = bd0();
            BigDecimal net = bd0();
        }

        Map<Long, Acc> porPais = new HashMap<>();
        invsPorPais.forEach((pais, c) -> porPais.computeIfAbsent(pais, k -> new Acc()).invs = c);

        for (OrdenDTO o : ordenes) {
            Long invId = o.getInversionistaId();
            Long pais = invId == null ? -1L : invPais.getOrDefault(invId, -1L);
            Acc a = porPais.computeIfAbsent(pais, k -> new Acc());
            a.ords++;
            a.trx = a.trx.add(nz(o.getTransactionAmount()));
            a.com = a.com.add(nz(o.getCommissionAmount()));
            a.net = a.net.add(nz(o.getNetAmount()));
        }

        for (var e : porPais.entrySet()) {
            wrote = true;
            line(out,
                    labelPais.apply(e.getKey()),
                    e.getValue().invs,
                    e.getValue().ords,
                    e.getValue().trx,
                    e.getValue().com,
                    e.getValue().net);
        }

        if (!wrote) writeUtf8(out, "# sin datos\n");
    }

    @Override
    public void csvSegmentacion(OutputStream out, String criterio, Long paisId, Double minMonto, Double maxMonto) {
        writeHeader(out, "criterio,categoria,total_inversionistas,monto_total\n");
        boolean wrote = false;

        String crit = criterio == null ? "PAIS" : criterio.toUpperCase(Locale.ROOT);

        List<InversionistaDTO> inversionistas = safe(invClient.listar());
        List<OrdenDTO> ordenes = safe(ordClient.listarTodas());
        List<PaisDTO> paises;
        try {
            paises = safe(catClient.listarPaises());
        } catch (Exception ex) {
            paises = List.of();
        }

        Map<Long, String> nombrePais = paises.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(PaisDTO::getId, PaisDTO::getNombre, (a, b) -> a));

        Function<Long, String> labelPais = id -> {
            if (id == null || id < 0) return "SIN PAÍS";
            return nombrePais.getOrDefault(id, "PAÍS " + id);
        };

        if ("PAIS".equals(crit)) {
            Map<Long, Long> invPais = inversionistas.stream()
                    .filter(i -> i.getId() != null)
                    .collect(Collectors.toMap(InversionistaDTO::getId, i -> ns(i.getPaisId()), (a, b) -> a));

            Map<Long, Long> counts = inversionistas.stream()
                    .filter(i -> paisId == null || Objects.equals(i.getPaisId(), paisId))
                    .collect(Collectors.groupingBy(i -> ns(i.getPaisId()), Collectors.counting()));

            Map<Long, BigDecimal> montoPorPais = new HashMap<>();
            for (OrdenDTO o : ordenes) {
                Long invId = o.getInversionistaId();
                if (invId == null) continue;
                Long pId = invPais.getOrDefault(invId, -1L);
                if (paisId != null && !Objects.equals(pId, paisId)) continue;
                montoPorPais.merge(pId, nz(o.getTransactionAmount()), BigDecimal::add);
            }

            for (var e : counts.entrySet()) {
                Long pId = e.getKey();
                wrote = true;
                line(out,
                        "PAIS",
                        labelPais.apply(pId),
                        e.getValue(),
                        montoPorPais.getOrDefault(pId, bd0()));
            }
            if (!wrote) writeUtf8(out, "# sin datos\n");
            return;
        }

        if ("MONTO".equals(crit)) {
            Map<Long, BigDecimal> sumaPorInv = ordenes.stream()
                    .collect(Collectors.groupingBy(OrdenDTO::getInversionistaId,
                            Collectors.reducing(bd0(), o -> nz(o.getTransactionAmount()), BigDecimal::add)));

            Set<Long> invPermitidos = inversionistas.stream()
                    .filter(i -> paisId == null || Objects.equals(i.getPaisId(), paisId))
                    .map(InversionistaDTO::getId).filter(Objects::nonNull).collect(Collectors.toSet());

            double min = minMonto == null ? Double.NEGATIVE_INFINITY : minMonto;
            double max = maxMonto == null ? Double.POSITIVE_INFINITY : maxMonto;

            long totalInv = sumaPorInv.entrySet().stream()
                    .filter(e -> invPermitidos.contains(e.getKey()))
                    .map(Map.Entry::getValue).mapToDouble(BigDecimal::doubleValue)
                    .filter(v -> v >= min && v <= max).count();

            BigDecimal totalMonto = sumaPorInv.entrySet().stream()
                    .filter(e -> invPermitidos.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .filter(v -> v.doubleValue() >= min && v.doubleValue() <= max)
                    .reduce(bd0(), BigDecimal::add);

            wrote = true;
            line(out, "MONTO", rango(minMonto, maxMonto), totalInv, totalMonto);
        }

        if (!wrote) writeUtf8(out, "# sin datos\n");
    }

    @Override
    public void csvComisiones(OutputStream out) {
        writeHeader(out, "id,nombre,apellido,correo,total_ordenes,total_comision\n");
        boolean wrote = false;

        List<ComisionistaDTO> comisionistas = safe(comClient.listar());
        List<OrdenDTO> ordenes = safe(ordClient.listarTodas());

        Map<Long, List<OrdenDTO>> ordPorCom = ordenes.stream()
                .filter(o -> o.getComisionistaId() != null)
                .collect(Collectors.groupingBy(OrdenDTO::getComisionistaId));

        for (ComisionistaDTO c : comisionistas) {
            List<OrdenDTO> list = ordPorCom.getOrDefault(c.getId(), List.of());
            BigDecimal total = list.stream().map(o -> nz(o.getCommissionAmount())).reduce(bd0(), BigDecimal::add);
            wrote = true;
            line(out, c.getId(), c.getNombre(), c.getApellido(), c.getCorreo(), list.size(), total);
        }

        if (!wrote) writeUtf8(out, "# sin datos\n");
    }

    private static void writeHeader(OutputStream out, String header) {
        writeBom(out);
        writeUtf8(out, "sep=,\n");
        writeUtf8(out, header);
    }

    private static void writeBom(OutputStream out) {
        try {
            out.write(UTF8_BOM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeUtf8(OutputStream out, String s) {
        try {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void line(OutputStream out, Object... cols) {
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(value(cols[i])));
        }
        sb.append('\n');
        writeUtf8(out, sb.toString());
    }

    private static String value(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static String escape(String s) {
        if (s == null) return "";
        boolean needs = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needs ? "\"" + v + "\"" : v;
    }

    private static <T> List<T> safe(List<T> l) {
        return l == null ? List.of() : l;
    }

    private static BigDecimal bd0() {
        return new BigDecimal("0.00");
    }

    private static BigDecimal nz(BigDecimal b) {
        return b == null ? bd0() : b;
    }

    private static Long ns(Long v) {
        return v == null ? -1L : v;
    }

    private static String rango(Double min, Double max) {
        String a = min == null ? "-∞" : String.format(Locale.US, "%.2f", min);
        String b = max == null ? "+∞" : String.format(Locale.US, "%.2f", max);
        return a + " - " + b;
    }
}
