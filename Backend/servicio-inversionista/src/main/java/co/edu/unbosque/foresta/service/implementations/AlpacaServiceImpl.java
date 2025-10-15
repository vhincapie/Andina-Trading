package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.model.DTO.AccountResponseDTO;
import co.edu.unbosque.foresta.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.foresta.service.interfaces.IAlpacaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AlpacaServiceImpl implements IAlpacaService {

    private final RestTemplate restTemplate;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    @Value("${alpaca.broker.account-url}")
    private String baseUrl;

    public AlpacaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequestDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateAccountRequestDTO> request = new HttpEntity<>(dto, headers);
        try {
            ResponseEntity<AccountResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    request,
                    AccountResponseDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.Conflict e) {
            throw new RuntimeException("Cuenta Alpaca duplicada para este correo.", e);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error API Alpaca: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        }
    }

    @Override
    public AccountResponseDTO getAccountById(String accountId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<AccountResponseDTO> response = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.GET,
                request,
                AccountResponseDTO.class
        );
        return response.getBody();
    }
}
