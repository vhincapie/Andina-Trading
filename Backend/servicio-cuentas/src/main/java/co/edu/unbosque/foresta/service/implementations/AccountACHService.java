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
import jakarta.transaction.Transactional;
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

    public AccountACHService(RestTemplate restTemplate,
                             IAccountACHRepository achRepository,
                             InversionistaClient inversionistaClient,
                             ModelMapper mm) {
        this.restTemplate = restTemplate;
        this.achRepository = achRepository;
        this.inversionistaClient = inversionistaClient;
        this.mm = mm;
    }

    @Transactional
    @Override
    public ResponseAccountACHDTO create(AccountACHRelationShipDTO req) {
        AccountACHRelationShipDTO normalized = normalizar(req);
        validar(normalized);
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        String url = construirUrlAch(mi.getAlpacaId());

        try {
            ResponseAccountACHDTO creado = crearRemoto(url, normalized);
            guardarEntidad(creado, mi.getAlpacaId());
            return creado;
        } catch (ConflictException e) {
            List<ResponseAccountACHDTO> existentes = listarAchRemotos(mi.getAlpacaId());
            if (existentes.isEmpty()) throw e;
            sincronizarExistentes(mi.getAlpacaId(), existentes);
            return existentes.get(0);
        }
    }

    @Override
    public List<AccountACHRelationShipDTO> getAllForAuthenticatedUser() {
        MiAlpacaDTO mi = cargarCuentaAlpaca();
        return achRepository.findByAlpacaAccountId(mi.getAlpacaId())
                .stream()
                .map(e -> mm.map(e, AccountACHRelationShipDTO.class))
                .toList();
    }

    private MiAlpacaDTO cargarCuentaAlpaca() {
        MiAlpacaDTO mi = inversionistaClient.miAlpaca();
        if (mi == null || mi.getAlpacaId() == null)
            throw new NotFoundException("No se encontró cuenta Alpaca asociada");
        return mi;
    }

    private String construirUrlAch(String alpacaId) {
        return achUrlTemplate.replace("{account_id}", alpacaId);
    }

    private ResponseAccountACHDTO crearRemoto(String url, AccountACHRelationShipDTO dto) {
        try {
            ResponseEntity<ResponseAccountACHDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(dto, headersJson()), ResponseAccountACHDTO.class);
            if (response.getBody() == null)
                throw new BadRequestException("Respuesta inválida desde Alpaca");
            return response.getBody();
        } catch (HttpClientErrorException e) {
            int code = e.getStatusCode().value();
            if (code == 409) throw new ConflictException("Relación ACH ya existe en Alpaca");
            if (code == 400) throw new BadRequestException("Solicitud inválida a Alpaca");
            throw new BadRequestException("Error creando ACH: " + code);
        }
    }

    private List<ResponseAccountACHDTO> listarAchRemotos(String alpacaId) {
        String listUrl = construirUrlAch(alpacaId);
        ResponseEntity<ResponseAccountACHDTO[]> resp = restTemplate.exchange(
                listUrl, HttpMethod.GET, new HttpEntity<>(headersJson()), ResponseAccountACHDTO[].class);
        ResponseAccountACHDTO[] body = resp.getBody();
        return body == null ? List.of() : List.of(body);
    }

    private void guardarEntidad(ResponseAccountACHDTO dto, String alpacaId) {
        AccountACHRelationShip entidad = mm.map(dto, AccountACHRelationShip.class);
        entidad.setCreatedAt(LocalDateTime.now());
        entidad.setAlpacaAccountId(alpacaId);
        achRepository.save(entidad);
    }

    private void sincronizarExistentes(String alpacaId, List<ResponseAccountACHDTO> existentes) {
        List<AccountACHRelationShip> actuales = achRepository.findByAlpacaAccountId(alpacaId);
        for (ResponseAccountACHDTO r : existentes) {
            boolean yaExiste = actuales.stream()
                    .anyMatch(e -> r.getId().equals(e.getAchId()));
            if (!yaExiste) guardarEntidad(r, alpacaId);
        }
    }

    private AccountACHRelationShipDTO normalizar(AccountACHRelationShipDTO r) {
        AccountACHRelationShipDTO n = new AccountACHRelationShipDTO();
        n.setAccountOwnerName(trim(r.getAccountOwnerName()));
        n.setBankAccountType(trim(r.getBankAccountType()));
        n.setBankAccountNumber(trim(r.getBankAccountNumber()));
        n.setNickname(trim(r.getNickname()));
        n.setBankRoutingNumber(vacio(r.getBankRoutingNumber())
                ? "123103716"
                : trim(r.getBankRoutingNumber()));
        return n;
    }

    private void validar(AccountACHRelationShipDTO n) {
        if (vacio(n.getAccountOwnerName()))
            throw new BadRequestException("accountOwnerName es obligatorio");
        if (vacio(n.getBankAccountType()))
            throw new BadRequestException("bankAccountType es obligatorio");
        if (vacio(n.getBankAccountNumber()))
            throw new BadRequestException("bankAccountNumber es obligatorio");
        if (vacio(n.getBankRoutingNumber()))
            throw new BadRequestException("bankRoutingNumber es obligatorio");

        if (!permitidos(n.getAccountOwnerName()))
            throw new BadRequestException("accountOwnerName contiene caracteres inválidos");
        if (!permitidos(n.getBankAccountType()))
            throw new BadRequestException("bankAccountType contiene caracteres inválidos");
        if (!permitidos(n.getBankAccountNumber()))
            throw new BadRequestException("bankAccountNumber contiene caracteres inválidos");
        if (!permitidos(n.getBankRoutingNumber()))
            throw new BadRequestException("bankRoutingNumber contiene caracteres inválidos");
        if (n.getNickname() != null && !permitidos(n.getNickname()))
            throw new BadRequestException("nickname contiene caracteres inválidos");
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

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
