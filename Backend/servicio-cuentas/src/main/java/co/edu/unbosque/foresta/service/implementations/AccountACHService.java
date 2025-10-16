package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.exceptions.BadRequestException;
import co.edu.unbosque.foresta.exceptions.exceptions.ConflictException;
import co.edu.unbosque.foresta.exceptions.exceptions.NotFoundException;
import co.edu.unbosque.foresta.integration.InversionistaClient;
import co.edu.unbosque.foresta.integration.DTO.MiAlpacaDTO;
import co.edu.unbosque.foresta.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.foresta.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.foresta.model.entity.AccountACHRelationShip;
import co.edu.unbosque.foresta.repository.IAccountACHRepository;
import co.edu.unbosque.foresta.service.interfaces.IAccountACHService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountACHService implements IAccountACHService<AccountACHRelationShipDTO> {

    private final RestTemplate restTemplate;
    private final IAccountACHRepository achRepository;
    private final InversionistaClient inversionistaClient;
    private final ModelMapper mm;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;
    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;
    @Value("${alpaca.broker.ach-url-template}")
    private String achUrlTemplate;

    public AccountACHService(RestTemplate restTemplate, IAccountACHRepository achRepository, InversionistaClient inversionistaClient, ModelMapper mm) {
        this.restTemplate = restTemplate;
        this.achRepository = achRepository;
        this.inversionistaClient = inversionistaClient;
        this.mm = mm;
    }

    @Override
    public ResponseAccountACHDTO create(AccountACHRelationShipDTO req) {
        AccountACHRelationShipDTO n = normalizar(req);
        validar(n);
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        String url = construirUrlAch(mi.getAlpacaId());
        ResponseAccountACHDTO creado = crearRemoto(url, n);
        AccountACHRelationShip entidad = construirEntidad(creado, n, mi.getAlpacaId());
        achRepository.save(entidad);
        return creado;
    }

    @Override
    public List<AccountACHRelationShipDTO> getAllForAuthenticatedUser() {
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        return achRepository.findByAlpacaAccountId(mi.getAlpacaId())
                .stream()
                .map(this::mapear)
                .toList();
    }

    private MiAlpacaDTO cargarCuentaAlpaca() {
        MiAlpacaDTO mi = inversionistaClient.miAlpaca();
        if (mi == null || mi.getAlpacaId() == null) throw new NotFoundException("No se encontró cuenta Alpaca asociada");
        return mi;
    }

    private String construirUrlAch(String alpacaId) {
        return achUrlTemplate.replace("{account_id}", alpacaId);
    }

    private ResponseAccountACHDTO crearRemoto(String url, AccountACHRelationShipDTO n) {
        try {
            ResponseEntity<ResponseAccountACHDTO> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(n, headersJson()), ResponseAccountACHDTO.class);
            if (response.getBody() == null) throw new BadRequestException("Respuesta inválida desde Alpaca");
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) throw new ConflictException("Relación ACH duplicada");
            if (e.getStatusCode().value() == 400) throw new BadRequestException("Solicitud inválida a Alpaca");
            throw new BadRequestException("Error creando ACH: " + e.getStatusCode().value());
        }
    }

    private AccountACHRelationShip construirEntidad(ResponseAccountACHDTO creado, AccountACHRelationShipDTO n, String alpacaId) {
        AccountACHRelationShip e = new AccountACHRelationShip();
        e.setAchId(creado.getId());
        e.setAccountOwnerName(n.getAccountOwnerName());
        e.setBankAccountType(n.getBankAccountType());
        e.setBankAccountNumber(n.getBankAccountNumber());
        e.setNickname(n.getNickname());
        e.setCreatedAt(LocalDateTime.now());
        e.setAlpacaAccountId(alpacaId);
        return e;
    }

    private AccountACHRelationShipDTO mapear(AccountACHRelationShip e) {
        AccountACHRelationShipDTO d = new AccountACHRelationShipDTO();
        d.setAccountOwnerName(e.getAccountOwnerName());
        d.setBankAccountType(e.getBankAccountType());
        d.setBankAccountNumber(e.getBankAccountNumber());
        d.setNickname(e.getNickname());
        return d;
    }

    private AccountACHRelationShipDTO normalizar(AccountACHRelationShipDTO r) {
        AccountACHRelationShipDTO n = new AccountACHRelationShipDTO();
        n.setAccountOwnerName(trim(r.getAccountOwnerName()));
        n.setBankAccountType(trim(r.getBankAccountType()));
        n.setBankAccountNumber(trim(r.getBankAccountNumber()));
        n.setNickname(trim(r.getNickname()));
        n.setBankRoutingNumber(vacio(r.getBankRoutingNumber()) ? "123103716" : trim(r.getBankRoutingNumber()));
        return n;
    }

    private void validar(AccountACHRelationShipDTO n) {
        if (vacio(n.getAccountOwnerName())) throw new BadRequestException("accountOwnerName es obligatorio");
        if (vacio(n.getBankAccountType())) throw new BadRequestException("bankAccountType es obligatorio");
        if (vacio(n.getBankAccountNumber())) throw new BadRequestException("bankAccountNumber es obligatorio");
        if (vacio(n.getBankRoutingNumber())) throw new BadRequestException("bankRoutingNumber es obligatorio");
        if (!permitidos(n.getAccountOwnerName())) throw new BadRequestException("accountOwnerName contiene caracteres inválidos");
        if (!permitidos(n.getBankAccountType())) throw new BadRequestException("bankAccountType contiene caracteres inválidos");
        if (!permitidos(n.getBankAccountNumber())) throw new BadRequestException("bankAccountNumber contiene caracteres inválidos");
        if (!permitidos(n.getBankRoutingNumber())) throw new BadRequestException("bankRoutingNumber contiene caracteres inválidos");
        if (n.getNickname() != null && !permitidos(n.getNickname())) throw new BadRequestException("nickname contiene caracteres inválidos");
    }

    private boolean permitidos(String v) {
        return v == null || v.matches("^[A-Za-z0-9 !'()*+,.\\-/:;?=]*$");
    }

    private HttpHeaders headersJson() {
        HttpHeaders h = new HttpHeaders();
        h.setBasicAuth(apiKey, apiSecret);
        h.setAccept(MediaType.parseMediaTypes("application/json"));
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private static boolean vacio(String s){ return s == null || s.isBlank(); }
    private static String trim(String s){ return s == null ? null : s.trim(); }
}
