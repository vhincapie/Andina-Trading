package co.edu.unbosque.foresta.integration;

import co.edu.unbosque.foresta.integration.DTO.AuthSignupRequest;
import co.edu.unbosque.foresta.integration.DTO.AuthSignupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name="auth-service", url="${auth.base-url}")
public interface AuthClient {
    @PostMapping("/api/auth/registrar-inversionista")
    AuthSignupResponse registrarInversionista(@RequestBody AuthSignupRequest body);
}

