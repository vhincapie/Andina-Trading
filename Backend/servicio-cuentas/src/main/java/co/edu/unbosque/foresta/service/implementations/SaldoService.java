package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.DTO.MiAlpacaDTO;
import co.edu.unbosque.foresta.integration.InversionistaClient;
import co.edu.unbosque.foresta.model.DTO.TradingDetailDTO;
import co.edu.unbosque.foresta.repository.ITransferLogRepository;
import co.edu.unbosque.foresta.service.interfaces.ISaldoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Service
public class SaldoService implements ISaldoService {

    private final InversionistaClient inversionistaClient;
    private final RestTemplate restTemplate;
    private final ITransferLogRepository transferLogRepository;
    private final AuditSender auditSender;

    @Value("${alpaca.broker.account-status-url}")
    private String accountStatusUrl;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    public SaldoService(InversionistaClient inversionistaClient,
                        RestTemplate restTemplate,
                        ITransferLogRepository transferLogRepository,
                        AuditSender auditSender) {
        this.inversionistaClient = inversionistaClient;
        this.restTemplate = restTemplate;
        this.transferLogRepository = transferLogRepository;
        this.auditSender = auditSender;
    }

    @Override
    public TradingDetailDTO obtenerSaldoUsuarioActual() {
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        String url = construirUrlSaldo(mi.getAlpacaId());
        auditSender.log("", new AuditLogRequest(
                "SALDO_GET_REQUEST",
                "/api/saldo/mi",
                "Solicitar saldo",
                Map.of("alpacaAccountId", mi.getAlpacaId())
        ));
        return consultarSaldo(url);
    }

    @Override
    public String calcularAvisoParaUsuarioActual() {
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        String r = transferLogRepository.findTop1ByAlpacaAccountIdOrderByCreatedAtDesc(mi.getAlpacaId())
                .map(last -> {
                    String st = last.getStatus() == null ? "" : last.getStatus().toUpperCase();
                    return st.startsWith("COMPLETE") ? null : "Transferencia en proceso. El saldo cambiará automáticamente al completarse.";
                })
                .orElse(null);
        auditSender.log("", new AuditLogRequest(
                "SALDO_ADVICE_RESULT",
                "/api/saldo/aviso",
                "Calcular aviso de transferencia",
                Map.of("alpacaAccountId", mi.getAlpacaId(), "advicePresent", r != null)
        ));
        return r;
    }

    private MiAlpacaDTO cargarCuentaAlpaca() {
        MiAlpacaDTO mi = inversionistaClient.miAlpaca();
        if (mi == null || mi.getAlpacaId() == null) {
            auditSender.log("", new AuditLogRequest(
                    "SALDO_INV_ALPACA_NOT_FOUND",
                    "/api/saldo/*",
                    "Cuenta Alpaca no asociada",
                    Map.of()
            ));
            throw new NotFoundException("No se encontró cuenta Alpaca asociada");
        }
        return mi;
    }

    private String construirUrlSaldo(String alpacaId) {
        return accountStatusUrl + "/" + alpacaId + "/account";
    }

    private TradingDetailDTO consultarSaldo(String url) {
        try {
            ResponseEntity<TradingDetailDTO> resp = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headersJson()), TradingDetailDTO.class);
            if (resp.getBody() == null) {
                auditSender.log("", new AuditLogRequest(
                        "SALDO_REMOTE_BAD_RESPONSE",
                        "/integracion/alpaca/account",
                        "Respuesta vacía desde Alpaca",
                        Map.of("statusHttp", resp.getStatusCode().value())
                ));
                throw new BadRequestException("Respuesta inválida desde Alpaca");
            }
            auditSender.log("", new AuditLogRequest(
                    "SALDO_REMOTE_OK",
                    "/integracion/alpaca/account",
                    "Consulta de saldo remota",
                    Map.of("statusHttp", resp.getStatusCode().value())
            ));
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            auditSender.log("", new AuditLogRequest(
                    "SALDO_REMOTE_HTTP_ERROR",
                    "/integracion/alpaca/account",
                    "Error HTTP consultando saldo",
                    Map.of("statusHttp", e.getStatusCode().value())
            ));
            throw new BadRequestException("Error consultando saldo Alpaca: " + e.getStatusCode().value());
        }
    }

    private HttpHeaders headersJson() {
        HttpHeaders h = new HttpHeaders();
        h.setBasicAuth(apiKey, apiSecret);
        h.setAccept(MediaType.parseMediaTypes("application/json"));
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
