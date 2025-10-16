package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.InversionistaClient;
import co.edu.unbosque.foresta.integration.DTO.MiAlpacaDTO;
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

@Service
public class TransferService implements ITransferService {

    private final RestTemplate restTemplate;
    private final InversionistaClient inversionistaClient;
    private final IAccountACHRepository achRepository;
    private final ITransferLogRepository transferLogRepository;
    private final ModelMapper mm;

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
                           ModelMapper mm) {
        this.restTemplate = restTemplate;
        this.inversionistaClient = inversionistaClient;
        this.achRepository = achRepository;
        this.transferLogRepository = transferLogRepository;
        this.mm = mm;
    }

    @Override
    public TransferResponseDTO crear(TransferCreateRequestDTO req) {
        TransferCreateRequestDTO normalizado = normalizar(req);
        validarMonto(normalizado);
        autocompletarCampos(normalizado);
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        bloquearUnaPorDia(mi.getAlpacaId());
        normalizado.setRelationshipId(obtenerRelacionACH(mi.getAlpacaId()));
        String url = construirUrlTransfer(mi.getAlpacaId());
        TransferResponseDTO resp = ejecutarTransferencia(url, normalizado);
        registrarTransferencia(mi.getAlpacaId(), resp, normalizado.getAmount());
        return resp;
    }

    private void bloquearUnaPorDia(String alpacaAccountId) {
        boolean existe = transferLogRepository.existsByAlpacaAccountIdAndCreatedAtBetween(
                alpacaAccountId, TimeUtils.todayStartNY(), TimeUtils.todayEndNY()
        );
        if (existe) throw new BadRequestException("Ya registraste una transferencia hoy. Intenta nuevamente mañana.");
    }

    private void registrarTransferencia(String alpacaAccountId, TransferResponseDTO resp, BigDecimal amount) {
        TransferLog l = new TransferLog();
        l.setAlpacaAccountId(alpacaAccountId);
        l.setExternalId(resp.getId());
        l.setAmount(amount);
        l.setStatus(resp.getStatus());
        l.setCreatedAt(TimeUtils.nowNY());
        transferLogRepository.save(l);
    }

    private MiAlpacaDTO cargarCuentaAlpaca() {
        MiAlpacaDTO mi = inversionistaClient.miAlpaca();
        if (mi == null || mi.getAlpacaId() == null) throw new NotFoundException("No se encontró cuenta Alpaca asociada");
        return mi;
    }

    private String obtenerRelacionACH(String alpacaAccountId) {
        AccountACHRelationShip rel = achRepository.findFirstByAlpacaAccountIdOrderByCreatedAtDesc(alpacaAccountId)
                .orElseThrow(() -> new NotFoundException("No se encontró relación ACH asociada"));
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
            if (resp.getBody() == null) throw new BadRequestException("Respuesta inválida desde Alpaca");
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            throw new BadRequestException("Error creando transferencia: " + e.getStatusCode().value() + " - " + body);
        }
    }

    private TransferCreateRequestDTO normalizar(TransferCreateRequestDTO r) {
        TransferCreateRequestDTO n = new TransferCreateRequestDTO();
        n.setAmount(r.getAmount());
        n.setDirection("INCOMING");
        n.setTiming("immediate");
        n.setTransferType("ach");
        n.setNote("Recarga automática Andina Trading");
        return n;
    }

    private void validarMonto(TransferCreateRequestDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BadRequestException("amount debe ser mayor a 0");
    }

    private void autocompletarCampos(TransferCreateRequestDTO dto) {
        if (vacio(dto.getDirection())) dto.setDirection("INCOMING");
        if (vacio(dto.getTiming())) dto.setTiming("immediate");
        if (vacio(dto.getTransferType())) dto.setTransferType("ach");
        if (vacio(dto.getNote())) dto.setNote("Recarga automática Andina Trading");
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
