package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.DTO.AccountResponseDTO;
import co.edu.unbosque.foresta.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.foresta.service.interfaces.IAlpacaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Slf4j
@Service
public class AlpacaServiceImpl implements IAlpacaService {

    private final RestTemplate restTemplate;
    private final AuditSender auditSender;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    @Value("${alpaca.broker.account-url}")
    private String baseUrl;

    public AlpacaServiceImpl(RestTemplate restTemplate, AuditSender auditSender) {
        this.restTemplate = restTemplate;
        this.auditSender = auditSender;
    }

    private HttpHeaders buildAlpacaHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Apca-Api-Key-Id", apiKey);
        headers.add("Apca-Api-Secret-Key", apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequestDTO dto) {
        HttpHeaders headers = buildAlpacaHeaders();
        HttpEntity<CreateAccountRequestDTO> request = new HttpEntity<>(dto, headers);

        String emailForLog = null;
        try {
            emailForLog = dto.getContact() != null ? dto.getContact().getEmailAddress() : null;
        } catch (Exception ignored) {}

        log.info("Alpaca createAccount POST {} email={}", baseUrl, emailForLog);

        try {
            ResponseEntity<AccountResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    request,
                    AccountResponseDTO.class
            );
            AccountResponseDTO body = response.getBody();
            log.info("Alpaca response status={} id={}", response.getStatusCode(), body != null ? body.getId() : null);
            auditSender.log("", new AuditLogRequest(
                    "ALPACA_CREATE_ACCOUNT_SUCCESS",
                    "/integracion/alpaca/accounts",
                    "Crear cuenta Alpaca",
                    Map.of("statusHttp", response.getStatusCode().value(), "alpacaAccountId", body != null ? body.getId() : null, "email", emailForLog)
            ));
            return body;

        } catch (HttpClientErrorException.Conflict e) {
            auditSender.log("", new AuditLogRequest(
                    "ALPACA_CREATE_ACCOUNT_CONFLICT",
                    "/integracion/alpaca/accounts",
                    "Conflicto al crear cuenta Alpaca",
                    Map.of("email", emailForLog, "statusHttp", 409, "payload", e.getResponseBodyAsString())
            ));
            throw new RuntimeException("Cuenta Alpaca duplicada para este correo.", e);

        } catch (HttpClientErrorException e) {
            String payload = e.getResponseBodyAsString();
            log.error("Alpaca HTTP error {}: {}", e.getStatusCode(), payload);
            auditSender.log("", new AuditLogRequest(
                    "ALPACA_CREATE_ACCOUNT_HTTP_ERROR",
                    "/integracion/alpaca/accounts",
                    "Error HTTP al crear cuenta Alpaca",
                    Map.of("statusHttp", e.getStatusCode().value(), "payload", payload, "email", emailForLog)
            ));
            throw new RuntimeException("Error API Alpaca: " + e.getStatusCode() + " - " + payload, e);

        } catch (ResourceAccessException e) {
            String msg = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
            log.error("Alpaca I/O error: {}", msg);
            auditSender.log("", new AuditLogRequest(
                    "ALPACA_CREATE_ACCOUNT_IO_ERROR",
                    "/integracion/alpaca/accounts",
                    "Error de E/S al crear cuenta Alpaca",
                    Map.of("mensaje", msg, "email", emailForLog)
            ));
            throw new RuntimeException("No hay conexión con Alpaca (DNS/TLS/firewall): " + msg, e);
        }
    }

    @Override
    public AccountResponseDTO getAccountById(String accountId) {
        HttpHeaders headers = buildAlpacaHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = baseUrl + "/" + accountId;

        log.info("Alpaca getAccount GET {}", url);

        try {
            ResponseEntity<AccountResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    AccountResponseDTO.class
            );
            AccountResponseDTO body = response.getBody();
            auditSender.log("", new AuditLogRequest(
                    "ALPACA_GET_ACCOUNT_SUCCESS",
                    "/integracion/alpaca/accounts/" + accountId,
                    "Consultar cuenta Alpaca por id",
                    Map.of("statusHttp", response.getStatusCode().value(), "alpacaAccountId", accountId)
            ));
            return body;

        } catch (HttpClientErrorException e) {
            String payload = e.getResponseBodyAsString();
            log.error("Alpaca GET error {}: {}", e.getStatusCode(), payload);
            auditSender.log("", new AuditLogRequest(
                    "ALPACA_GET_ACCOUNT_HTTP_ERROR",
                    "/integracion/alpaca/accounts/" + accountId,
                    "Error HTTP al consultar cuenta Alpaca",
                    Map.of("statusHttp", e.getStatusCode().value(), "payload", payload, "alpacaAccountId", accountId)
            ));
            throw new RuntimeException("Error API Alpaca al consultar cuenta: " + e.getStatusCode() + " - " + payload, e);

        } catch (ResourceAccessException e) {
            String msg = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
            log.error("Alpaca I/O error (GET): {}", msg);
            auditSender.log("", new AuditLogRequest(
                    "ALPACA_GET_ACCOUNT_IO_ERROR",
                    "/integracion/alpaca/accounts/" + accountId,
                    "Error de E/S al consultar cuenta Alpaca",
                    Map.of("mensaje", msg, "alpacaAccountId", accountId)
            ));
            throw new RuntimeException("No hay conexión con Alpaca (DNS/TLS/firewall): " + msg, e);
        }
    }
}
