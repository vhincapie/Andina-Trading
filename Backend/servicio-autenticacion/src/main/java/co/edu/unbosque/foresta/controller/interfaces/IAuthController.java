package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.model.entity.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/api/auth")
public interface IAuthController {

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequestDTO req);

    @PostMapping("/refresh")
    ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshFromCookie,
            @RequestBody(required = false) RefreshTokenRequestDTO req);

    @PostMapping("/recuperar-password")
    ResponseEntity<Void> recuperar(@RequestBody RecoverPasswordRequestDTO req);

    @PostMapping("/restablecer-password")
    ResponseEntity<Void> restablecer(@RequestBody ResetPasswordRequestDTO req);

    @GetMapping("/me")
    ResponseEntity<UsuarioDTO> me(Principal principal);

    @PostMapping("/logout")
    ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String tokenCookie,
            @RequestBody(required = false) LogoutRequestDTO body);

    @PostMapping("/registrar-inversionista")
    ResponseEntity<SignupResponseDTO> registrarInversionista(@RequestBody RegistrarInversionistaRequestDTO req);

    @PostMapping("/registrar-comisionista")
    ResponseEntity<SignupResponseDTO> registrarComisionista(@RequestBody RegistrarComisionistaRequestDTO req);
}
