package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.InversionistaClient;
import co.edu.unbosque.foresta.integration.DTO.MiAlpacaDTO;
import co.edu.unbosque.foresta.model.DTO.TradingDetailDTO;
import co.edu.unbosque.foresta.repository.ITransferLogRepository;
import co.edu.unbosque.foresta.service.interfaces.ISaldoService;
import co.edu.unbosque.foresta.util.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SaldoService implements ISaldoService {

    private final InversionistaClient inversionistaClient;
    private final RestTemplate restTemplate;
    private final ITransferLogRepository transferLogRepository;

    @Value("${alpaca.broker.account-status-url}")
    private String accountStatusUrl;
    @Value("${alpaca.broker.api.key}")
    private String apiKey;
    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    public SaldoService(InversionistaClient inversionistaClient,
                        RestTemplate restTemplate,
                        ITransferLogRepository transferLogRepository) {
        this.inversionistaClient = inversionistaClient;
        this.restTemplate = restTemplate;
        this.transferLogRepository = transferLogRepository;
    }

    @Override
    public TradingDetailDTO obtenerSaldoUsuarioActual() {
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        String url = construirUrlSaldo(mi.getAlpacaId());
        return consultarSaldo(url);
    }

    @Override
    public String calcularAvisoParaUsuarioActual() {
        MiAlpacaDTO mi = cargarCuentaAlpaca();

        boolean fueraHorario = TimeUtils.isAfterMarketHours() || TimeUtils.isWeekend();
        boolean reciente10 = huboTransferenciaReciente(mi.getAlpacaId(), 10);
        boolean reciente60 = huboTransferenciaReciente(mi.getAlpacaId(), 60);
        boolean huboHoy = huboTransferenciaHoy(mi.getAlpacaId());

        if (fueraHorario && huboHoy) {
            return "Transferencia creada fuera de horario. Se reflejará el siguiente día hábil.";
        }
        if (reciente10) {
            return "La transferencia fue creada hace poco. El saldo puede tardar algunos minutos en reflejarse.";
        }
        if (reciente60) {
            return "La transferencia está en compensación. Intenta nuevamente más tarde.";
        }
        return null;
    }

    private boolean huboTransferenciaReciente(String alpacaAccountId, int ventanaMinutos) {
        return transferLogRepository.findTop1ByAlpacaAccountIdOrderByCreatedAtDesc(alpacaAccountId)
                .map(last -> last.getCreatedAt().isAfter(TimeUtils.nowNY().minusMinutes(ventanaMinutos)))
                .orElse(false);
    }

    private boolean huboTransferenciaHoy(String alpacaAccountId) {
        return transferLogRepository.existsByAlpacaAccountIdAndCreatedAtBetween(
                alpacaAccountId, TimeUtils.todayStartNY(), TimeUtils.todayEndNY()
        );
    }

    private MiAlpacaDTO cargarCuentaAlpaca() {
        MiAlpacaDTO mi = inversionistaClient.miAlpaca();
        if (mi == null || mi.getAlpacaId() == null) throw new NotFoundException("No se encontró cuenta Alpaca asociada");
        return mi;
    }

    private String construirUrlSaldo(String alpacaId) {
        return accountStatusUrl + "/" + alpacaId + "/account";
    }

    private TradingDetailDTO consultarSaldo(String url) {
        try {
            ResponseEntity<TradingDetailDTO> resp = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headersJson()), TradingDetailDTO.class);
            if (resp.getBody() == null) throw new BadRequestException("Respuesta inválida desde Alpaca");
            return resp.getBody();
        } catch (HttpClientErrorException e) {
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
