package co.edu.unbosque.foresta.controller.interfaces;

import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.model.entity.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<UsuarioDTO> me(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user);

    @PostMapping("/logout")
    ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String tokenCookie,
            @RequestBody(required = false) LogoutRequestDTO body);
}
