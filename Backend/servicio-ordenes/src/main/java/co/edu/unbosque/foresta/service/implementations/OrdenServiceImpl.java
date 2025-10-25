package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.AlpacaTradingClient;
import co.edu.unbosque.foresta.integration.ContratosClient;
import co.edu.unbosque.foresta.integration.DTO.AlpacaAccountDTO;
import co.edu.unbosque.foresta.integration.DTO.ContratoActivoDTO;
import co.edu.unbosque.foresta.integration.DTO.InversionistaDTO;
import co.edu.unbosque.foresta.integration.InversionistasClient;
import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.model.entity.Order;
import co.edu.unbosque.foresta.repository.IOrderRepository;
import co.edu.unbosque.foresta.service.interfaces.IPrecioService;
import co.edu.unbosque.foresta.service.interfaces.IOrdenService;
import co.edu.unbosque.foresta.service.interfaces.ISaldoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class OrdenServiceImpl implements IOrdenService {

    private final IOrderRepository repo;
    private final ContratosClient contratos;
    private final InversionistasClient inversionistas;
    private final AlpacaTradingClient alpaca;
    private final IPrecioService precios;
    private final ISaldoService saldoService;
    private final ModelMapper mm;

    private static final Set<String> FINAL_STATES = Set.of(
            "FILLED","CANCELED","EXPIRED","REJECTED","DONE_FOR_DAY"
    );

    public OrdenServiceImpl(IOrderRepository repo,
                            ContratosClient contratos,
                            InversionistasClient inversionistas,
                            AlpacaTradingClient alpaca,
                            IPrecioService precios,
                            ISaldoService saldoService,
                            ModelMapper mm) {
        this.repo = repo;
        this.contratos = contratos;
        this.inversionistas = inversionistas;
        this.alpaca = alpaca;
        this.precios = precios;
        this.saldoService = saldoService;
        this.mm = mm;
    }

    @Transactional
    @Override
    public OrderDTO crear(OrderCreateRequestDTO req) {
        validarReq(req);

        ContratoActivoDTO contrato = obtenerContratoActivo();
        Long inversionistaId = contrato.getInversionistaId();
        Long comisionistaId = contrato.getComisionistaId();

        BigDecimal unitPrice = resolverUnitPrice(req);

        BigDecimal qty = new BigDecimal(req.getQty());
        BigDecimal transaction = qty.multiply(unitPrice);
        BigDecimal commission = transaction.multiply(contrato.getPorcentajeCobroAplicado().movePointLeft(2));
        BigDecimal net = req.getSide().equalsIgnoreCase("buy") ? transaction.add(commission) : transaction.subtract(commission);

        if ("buy".equalsIgnoreCase(req.getSide())) {
            var trading = saldoService.obtenerSaldoUsuarioActual();
            BigDecimal buyingPower = safeBD(trading.getBuyingPower());
            if (buyingPower.compareTo(net) < 0) {
                throw new BadRequestException(
                        "Saldo insuficiente. Buying power: " + buyingPower.toPlainString() +
                                " USD, requerido: " + net.toPlainString() + " USD"
                );
            }
        }

        Order o = new Order();
        o.setInversionistaId(inversionistaId);
        o.setComisionistaId(comisionistaId);
        o.setAlpacaOrderId(null);
        o.setSymbol(req.getSymbol().trim().toUpperCase());
        o.setQty(qty);
        o.setOrderType(req.getType().toUpperCase());
        o.setSide(req.getSide().toUpperCase());
        o.setTimeInForce(req.getTimeInForce().toUpperCase());
        o.setLimitPrice(parseOrNull(req.getLimitPrice()));
        o.setStopPrice(parseOrNull(req.getStopPrice()));
        o.setStatus("PENDIENTE_AUTORIZACION");
        o.setUnitPrice(unitPrice);
        o.setTransactionAmount(transaction);
        o.setCommissionAmount(commission);
        o.setNetAmount(net);
        o.setMoneda(contrato.getMoneda() != null ? contrato.getMoneda() : "USD");

        Order g = repo.save(o);
        return mapOut(g);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> misOrdenes() {
        ContratoActivoDTO contrato = obtenerContratoActivo();
        return repo.findByInversionistaIdOrderByCreadoEnDesc(contrato.getInversionistaId())
                .stream().map(this::mapOut).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDTO obtener(Long id) {
        ContratoActivoDTO contrato = obtenerContratoActivo();
        Order e = repo.findById(id).orElseThrow(() -> new NotFoundException("Orden no encontrada"));
        if (!e.getInversionistaId().equals(contrato.getInversionistaId()))
            throw new NotFoundException("No tienes acceso a esta orden");
        return mapOut(e);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> listarPorComisionista(Long comisionistaId, String status) {
        var list = (status == null || status.isBlank())
                ? repo.findByComisionistaIdOrderByCreadoEnDesc(comisionistaId)
                : repo.findByComisionistaIdAndStatusOrderByCreadoEnDesc(comisionistaId, status);

        Map<Long, String[]> cacheInv = new HashMap<>();
        list.stream().map(Order::getInversionistaId).distinct().forEach(invId -> {
            try {
                var inv = inversionistas.obtenerPorId(invId);
                if (inv != null) {
                    String nombre = ((inv.getNombre() != null ? inv.getNombre() : "") + " " +
                            (inv.getApellido()!= null ? inv.getApellido(): "")).trim();
                    cacheInv.put(invId, new String[]{ nombre, inv.getCorreo() });
                }
            } catch (Exception ignored) {

            }
        });

        return list.stream().map(e -> mapOutForComisionista(e, cacheInv)).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> listarPorInversionistaParaComisionista(Long comisionistaId, Long inversionistaId) {
        var list = repo.findByComisionistaIdOrderByCreadoEnDesc(comisionistaId)
                .stream().filter(o -> o.getInversionistaId().equals(inversionistaId)).toList();

        Map<Long, String[]> cacheInv = new HashMap<>();
        try {
            var inv = inversionistas.obtenerPorId(inversionistaId);
            if (inv != null) {
                String nombre = ((inv.getNombre() != null ? inv.getNombre() : "") + " " +
                        (inv.getApellido()!= null ? inv.getApellido(): "")).trim();
                cacheInv.put(inversionistaId, new String[]{ nombre, inv.getCorreo() });
            }
        } catch (Exception ignored) {}

        return list.stream().map(e -> mapOutForComisionista(e, cacheInv)).toList();
    }

    @Transactional
    @Override
    public OrderDTO aprobar(Long orderId, Long comisionistaId, OrderApprovalRequestDTO req) {
        Order e = repo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Orden no encontrada"));
        if (!Objects.equals(e.getComisionistaId(), comisionistaId))
            throw new NotFoundException("No puedes aprobar órdenes de otro comisionista");
        if (!"PENDIENTE_AUTORIZACION".equalsIgnoreCase(e.getStatus()))
            throw new BadRequestException("Solo se pueden aprobar órdenes en estado PENDIENTE_AUTORIZACION");

        List<Order> activasMismoSimbolo = repo
                .findByInversionistaIdAndSymbolOrderByCreadoEnDesc(e.getInversionistaId(), e.getSymbol());

        String ladoActual = e.getSide();
        String ladoOpuesto = "BUY".equalsIgnoreCase(ladoActual) ? "SELL" : "BUY";

        activasMismoSimbolo.stream()
                .filter(o -> !Objects.equals(o.getId(), e.getId()))
                .filter(o -> isActive(o.getStatus()))
                .filter(o -> ladoOpuesto.equalsIgnoreCase(o.getSide()))
                .findFirst()
                .ifPresent(conflict -> {
                    throw new BadRequestException(
                            "No puedes aprobar: existe una orden " + conflict.getSide() +
                                    " (#" + conflict.getId() + ") activa para " + e.getSymbol() +
                                    ". Cancélala o espera a que finalice."
                    );
                });

        String alpacaAccountId = obtenerAlpacaIdDeInversionista(e.getInversionistaId());
        if (alpacaAccountId == null || alpacaAccountId.isBlank())
            throw new NotFoundException("Cuenta Alpaca del inversionista no disponible");

        OrderCreateRequestDTO reqAlpaca = new OrderCreateRequestDTO();
        reqAlpaca.setSymbol(e.getSymbol());
        reqAlpaca.setQty(e.getQty().toPlainString());
        reqAlpaca.setSide(e.getSide().toLowerCase());
        reqAlpaca.setType(e.getOrderType().toLowerCase());
        reqAlpaca.setTimeInForce(e.getTimeInForce().toLowerCase());
        if (e.getLimitPrice()!=null) reqAlpaca.setLimitPrice(e.getLimitPrice().toPlainString());
        if (e.getStopPrice()!=null)  reqAlpaca.setStopPrice(e.getStopPrice().toPlainString());

        OrderDTO alpacaRes = alpaca.crearOrden(alpacaAccountId, reqAlpaca);

        e.setAlpacaOrderId(alpacaRes.getAlpacaOrderId() != null ? alpacaRes.getAlpacaOrderId() : alpacaRes.getId());
        e.setStatus(alpacaRes.getStatus() != null ? alpacaRes.getStatus().toUpperCase() : "ENVIADA_ALPACA");
        e.setApprovedBy(comisionistaId);
        e.setApprovedAt(Instant.now());
        e.setRejectReason(null);

        return mapOut(repo.save(e));
    }

    @Transactional
    @Override
    public OrderDTO rechazar(Long orderId, Long comisionistaId, OrderRejectRequestDTO req) {
        if (req == null || req.getMotivo() == null || req.getMotivo().isBlank())
            throw new BadRequestException("Motivo es obligatorio");

        Order e = repo.findById(orderId).orElseThrow(() -> new NotFoundException("Orden no encontrada"));
        if (!Objects.equals(e.getComisionistaId(), comisionistaId))
            throw new NotFoundException("No puedes rechazar órdenes de otro comisionista");
        if (!"PENDIENTE_AUTORIZACION".equalsIgnoreCase(e.getStatus()))
            throw new BadRequestException("Solo se pueden rechazar órdenes en estado PENDIENTE_AUTORIZACION");

        e.setStatus("RECHAZADA");
        e.setApprovedBy(comisionistaId);
        e.setApprovedAt(Instant.now());
        e.setRejectReason(req.getMotivo());

        Order g = repo.save(e);
        return mapOut(g);
    }

    @Transactional(readOnly = true)
    @Override
    public CommissionSummaryDTO resumenComisiones(Long comisionistaId, String from, String to) {
        final String EXCLUIR_STATUS = "RECHAZADA";

        Instant start = parseStartOfDayUtc(from);
        Instant end = parseEndOfDayUtc(to);

        List<Order> list;

        if (start != null && end != null) {

            list = repo.findByComisionistaIdAndCreadoEnBetweenAndStatusNotIgnoreCaseOrderByCreadoEnDesc(
                    comisionistaId, start, end, EXCLUIR_STATUS);
        } else if (start != null) {

            list = repo.findByComisionistaIdAndCreadoEnGreaterThanEqualAndStatusNotIgnoreCaseOrderByCreadoEnDesc(
                    comisionistaId, start, EXCLUIR_STATUS);
        } else if (end != null) {

            list = repo.findByComisionistaIdAndCreadoEnLessThanEqualAndStatusNotIgnoreCaseOrderByCreadoEnDesc(
                    comisionistaId, end, EXCLUIR_STATUS);
        } else {

            list = repo.findByComisionistaIdOrderByCreadoEnDesc(comisionistaId)
                    .stream()
                    .filter(o -> !EXCLUIR_STATUS.equalsIgnoreCase(o.getStatus()))
                    .toList();
        }

        BigDecimal total = list.stream()
                .map(Order::getCommissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CommissionSummaryDTO dto = new CommissionSummaryDTO();
        dto.setTotal(total);
        dto.setCantidadOrdenes((long) list.size());
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PositionDTO> listarMisPosiciones() {

        AlpacaAccountDTO acc = inversionistas.miAlpaca();
        if (acc == null || acc.getAlpacaId() == null || acc.getAlpacaId().isBlank()) {
            throw new NotFoundException("No se encontró tu cuenta Alpaca.");
        }

        return alpaca.listarPosiciones(acc.getAlpacaId());
    }

    private static Instant parseStartOfDayUtc(String yyyyMmDd) {
        if (yyyyMmDd == null || yyyyMmDd.isBlank()) return null;
        try {
            LocalDate d = LocalDate.parse(yyyyMmDd.trim());
            return d.atStartOfDay(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static Instant parseEndOfDayUtc(String yyyyMmDd) {
        if (yyyyMmDd == null || yyyyMmDd.isBlank()) return null;
        try {
            LocalDate d = LocalDate.parse(yyyyMmDd.trim());
            return d.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static BigDecimal safeBD(String s) {
        try {
            if (s == null || s.isBlank()) return BigDecimal.ZERO;
            return new BigDecimal(s.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private ContratoActivoDTO obtenerContratoActivo() {
        try {
            ContratoActivoDTO c = contratos.miContratoActivo();
            if (c == null || c.getId() == null) throw new NotFoundException("No tienes contrato activo");
            return c;
        } catch (feign.FeignException.NotFound e) {
            throw new NotFoundException("No tienes contrato activo");
        } catch (feign.FeignException e) {
            throw new BadRequestException("Error consultando contrato activo: " + e.status());
        }
    }

    private String obtenerAlpacaIdDeInversionista(Long inversionistaId) {
        try {
            InversionistaDTO inv = inversionistas.obtenerPorId(inversionistaId);
            if (inv != null && inv.getAlpacaAccountId() != null && !inv.getAlpacaAccountId().isBlank()) {
                return inv.getAlpacaAccountId();
            }
        } catch (feign.FeignException ignored) { }

        try {
            AlpacaAccountDTO a = inversionistas.alpacaPorInversionistaId(inversionistaId);
            if (a != null && a.getAlpacaId() != null && !a.getAlpacaId().isBlank()) {
                return a.getAlpacaId();
            }
        } catch (feign.FeignException ignored) { }

        throw new NotFoundException("No fue posible resolver la cuenta Alpaca del inversionista " + inversionistaId);
    }

    private void validarReq(OrderCreateRequestDTO r) {
        if (r == null) throw new BadRequestException("Body requerido");
        if (vacio(r.getSymbol())) throw new BadRequestException("symbol es obligatorio");
        if (vacio(r.getQty())) throw new BadRequestException("qty es obligatorio");
        if (vacio(r.getSide())) throw new BadRequestException("side es obligatorio");
        if (vacio(r.getType())) throw new BadRequestException("type es obligatorio");
        if (vacio(r.getTimeInForce())) throw new BadRequestException("timeInForce es obligatorio");

        String type = r.getType().trim().toLowerCase();
        if (type.equals("limit") || type.equals("stop_limit")) {
            if (vacio(r.getLimitPrice())) throw new BadRequestException("limitPrice es obligatorio para órdenes limit/stop_limit");
        }
        if (type.equals("stop") || type.equals("stop_limit")) {
            if (vacio(r.getStopPrice())) throw new BadRequestException("stopPrice es obligatorio para órdenes stop/stop_limit");
        }
        try {
            BigDecimal q = new BigDecimal(r.getQty());
            if (q.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("qty debe ser > 0");
        } catch (NumberFormatException ex) {
            throw new BadRequestException("qty inválido");
        }
    }

    private BigDecimal resolverUnitPrice(OrderCreateRequestDTO r) {
        String t = r.getType().trim().toLowerCase();
        return switch (t) {
            case "market" -> BigDecimal.valueOf(precios.obtenerPrecioActual(r.getSymbol().trim().toUpperCase()));
            case "limit", "stop_limit" -> toBD(r.getLimitPrice(), "limitPrice inválido");
            case "stop" -> toBD(r.getStopPrice(), "stopPrice inválido");
            default -> throw new BadRequestException("type inválido: " + r.getType());
        };
    }

    private OrderDTO mapOut(Order e) {
        OrderDTO d = mm.map(e, OrderDTO.class);
        d.setDbId(e.getId());
        d.setId(e.getAlpacaOrderId());
        d.setQty(e.getQty().toPlainString());
        if (e.getLimitPrice()!=null) d.setLimitPrice(e.getLimitPrice().toPlainString());
        if (e.getStopPrice()!=null)  d.setStopPrice(e.getStopPrice().toPlainString());
        return d;
    }

    private OrderDTO mapOutForComisionista(Order e, Map<Long, String[]> cacheInv) {
        OrderDTO d = mapOut(e);
        String[] info = cacheInv.get(e.getInversionistaId());
        if (info != null) {
            d.setInversionistaNombre(info[0]);
            d.setInversionistaCorreo(info[1]);
        }
        return d;
    }

    private static BigDecimal toBD(String s, String err) {
        try { return new BigDecimal(s); } catch (Exception e) { throw new BadRequestException(err); }
    }
    private static boolean vacio(String s){ return s==null || s.isBlank(); }
    private static BigDecimal parseOrNull(String s){ try { return (s==null||s.isBlank())? null : new BigDecimal(s); } catch(Exception e){ return null; } }

    @Transactional
    public int sincronizarEstadosPendientes() {
        var pendientes = repo.findOrdersToSync();
        int actualizadas = 0;

        for (Order o : pendientes) {
            if (o.getAlpacaOrderId() == null || o.getAlpacaOrderId().isBlank()) continue;

            String alpacaAccountId = null;
            try {
                alpacaAccountId = obtenerAlpacaIdDeInversionista(o.getInversionistaId());
            } catch (Exception ignored) { /* si falla, sigo con la siguiente */ }
            if (alpacaAccountId == null || alpacaAccountId.isBlank()) continue;

            OrderDTO alp = alpaca.obtenerOrden(alpacaAccountId, o.getAlpacaOrderId());
            if (alp == null) continue;

            String nuevoEstado = (alp.getStatus() == null ? null : alp.getStatus().toUpperCase(Locale.ROOT));
            if (nuevoEstado != null && !nuevoEstado.equalsIgnoreCase(o.getStatus())) {
                o.setStatus(nuevoEstado);

                repo.save(o);
                actualizadas++;
            }
        }
        return actualizadas;
    }

    private boolean isActive(String st) {
        return st != null && !FINAL_STATES.contains(st.toUpperCase());
    }
}
