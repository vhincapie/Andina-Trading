package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.DTO.MiAlpacaDTO;
import co.edu.unbosque.foresta.integration.InversionistaClient;
import co.edu.unbosque.foresta.model.DTO.TransferCreateRequestDTO;
import co.edu.unbosque.foresta.model.DTO.TransferResponseDTO;
import co.edu.unbosque.foresta.model.entity.AccountACHRelationShip;
import co.edu.unbosque.foresta.model.entity.TransferLog;
import co.edu.unbosque.foresta.repository.IAccountACHRepository;
import co.edu.unbosque.foresta.repository.ITransferLogRepository;
import co.edu.unbosque.foresta.service.interfaces.ITransferService;
import co.edu.unbosque.foresta.util.TimeUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Service
public class TransferService implements ITransferService {

    private final RestTemplate restTemplate;
    private final InversionistaClient inversionistaClient;
    private final IAccountACHRepository achRepository;
    private final ITransferLogRepository transferLogRepository;
    private final ModelMapper mm;
    private final AuditSender auditSender;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    @Value("${alpaca.broker.transfers-url-template}")
    private String transfersUrlTemplate;

    public TransferService(RestTemplate restTemplate,
                           InversionistaClient inversionistaClient,
                           IAccountACHRepository achRepository,
                           ITransferLogRepository transferLogRepository,
                           ModelMapper mm,
                           AuditSender auditSender) {
        this.restTemplate = restTemplate;
        this.inversionistaClient = inversionistaClient;
        this.achRepository = achRepository;
        this.transferLogRepository = transferLogRepository;
        this.mm = mm;
        this.auditSender = auditSender;
    }

    @Override
    public TransferResponseDTO crear(TransferCreateRequestDTO req) {
        TransferCreateRequestDTO dto = prepararSolicitud(req);
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        auditSender.log("", new AuditLogRequest(
                "TRANSFER_CREATE_REQUEST",
                "/api/transfers",
                "Solicitar transferencia",
                Map.of("alpacaAccountId", mi.getAlpacaId(), "amount", dto.getAmount())
        ));
        bloquearUnaPorDia(mi.getAlpacaId());
        dto.setRelationshipId(obtenerRelacionACH(mi.getAlpacaId()));
        TransferResponseDTO resp = ejecutarTransferencia(construirUrlTransfer(mi.getAlpacaId()), dto);
        registrarTransferencia(mi.getAlpacaId(), resp, dto.getAmount());
        auditSender.log("", new AuditLogRequest(
                "TRANSFER_CREATE_SUCCESS",
                "/api/transfers",
                "Transferencia creada",
                Map.of("alpacaAccountId", mi.getAlpacaId(), "transferId", resp.getId(), "status", resp.getStatus())
        ));
        return resp;
    }

    private TransferCreateRequestDTO prepararSolicitud(TransferCreateRequestDTO r) {
        TransferCreateRequestDTO n = new TransferCreateRequestDTO();
        n.setAmount(r.getAmount());
        n.setDirection("INCOMING");
        n.setTiming("immediate");
        n.setTransferType("ach");
        n.setNote("Recarga automática Andina Trading");
        validarMonto(n);
        return n;
    }

    private void validarMonto(TransferCreateRequestDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            auditSender.log("", new AuditLogRequest(
                    "TRANSFER_BAD_AMOUNT",
                    "/api/transfers",
                    "Monto inválido",
                    Map.of()
            ));
            throw new BadRequestException("amount debe ser mayor a 0");
        }
    }

    private MiAlpacaDTO cargarCuentaAlpaca() {
        MiAlpacaDTO mi = inversionistaClient.miAlpaca();
        if (mi == null || mi.getAlpacaId() == null) {
            auditSender.log("", new AuditLogRequest(
                    "TRANSFER_INV_ALPACA_NOT_FOUND",
                    "/api/transfers",
                    "Cuenta Alpaca no asociada",
                    Map.of()
            ));
            throw new NotFoundException("No se encontró cuenta Alpaca asociada");
        }
        return mi;
    }

    private void bloquearUnaPorDia(String alpacaAccountId) {
        boolean existe = transferLogRepository.existsByAlpacaAccountIdAndCreatedAtBetween(
                alpacaAccountId, TimeUtils.todayStartNY(), TimeUtils.todayEndNY()
        );
        if (existe) {
            auditSender.log("", new AuditLogRequest(
                    "TRANSFER_LIMIT_PER_DAY",
                    "/api/transfers",
                    "Transferencia ya registrada hoy",
                    Map.of("alpacaAccountId", alpacaAccountId)
            ));
            throw new BadRequestException("Ya registraste una transferencia hoy. Intenta nuevamente mañana.");
        }
    }

    private String obtenerRelacionACH(String alpacaAccountId) {
        AccountACHRelationShip rel = achRepository.findFirstByAlpacaAccountIdOrderByCreatedAtDesc(alpacaAccountId)
                .orElseThrow(() -> {
                    auditSender.log("", new AuditLogRequest(
                            "TRANSFER_ACH_REL_NOT_FOUND",
                            "/api/transfers",
                            "Relación ACH no encontrada",
                            Map.of("alpacaAccountId", alpacaAccountId)
                    ));
                    return new NotFoundException("No se encontró relación ACH asociada");
                });
        return rel.getAchId();
    }

    private String construirUrlTransfer(String alpacaId) {
        return transfersUrlTemplate.replace("{account_id}", alpacaId);
    }

    private TransferResponseDTO ejecutarTransferencia(String url, TransferCreateRequestDTO dto) {
        try {
            ResponseEntity<TransferResponseDTO> resp = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(dto, headersJson()), TransferResponseDTO.class
            );
            if (resp.getBody() == null) {
                auditSender.log("", new AuditLogRequest(
                        "TRANSFER_REMOTE_BAD_RESPONSE",
                        "/integracion/alpaca/transfers",
                        "Respuesta vacía desde Alpaca",
                        Map.of("statusHttp", resp.getStatusCode().value())
                ));
                throw new BadRequestException("Respuesta inválida desde Alpaca");
            }
            auditSender.log("", new AuditLogRequest(
                    "TRANSFER_REMOTE_OK",
                    "/integracion/alpaca/transfers",
                    "Transferencia remota creada",
                    Map.of("statusHttp", resp.getStatusCode().value(), "transferId", resp.getBody().getId())
            ));
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            auditSender.log("", new AuditLogRequest(
                    "TRANSFER_REMOTE_HTTP_ERROR",
                    "/integracion/alpaca/transfers",
                    "Error HTTP creando transferencia",
                    Map.of("statusHttp", e.getStatusCode().value(), "payload", body)
            ));
            throw new BadRequestException("Error creando transferencia: " + e.getStatusCode().value() + " - " + body);
        }
    }

    private void registrarTransferencia(String alpacaAccountId, TransferResponseDTO resp, BigDecimal amount) {
        TransferLog log = mm.map(resp, TransferLog.class);
        log.setAlpacaAccountId(alpacaAccountId);
        log.setAmount(amount);
        log.setCreatedAt(TimeUtils.nowNY());
        transferLogRepository.save(log);
        auditSender.log("", new AuditLogRequest(
                "TRANSFER_LOCAL_SAVED",
                "/api/transfers",
                "Guardar transferencia local",
                Map.of("alpacaAccountId", alpacaAccountId, "transferId", resp.getId(), "amount", amount)
        ));
    }

    private HttpHeaders headersJson() {
        HttpHeaders h = new HttpHeaders();
        h.setBasicAuth(apiKey, apiSecret);
        h.setAccept(MediaType.parseMediaTypes("application/json"));
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
