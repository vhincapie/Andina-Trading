package co.edu.unbosque.foresta.controller.implementations;

import co.edu.unbosque.foresta.controller.interfaces.IAuthController;
import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.model.entity.LoginResponse;
import co.edu.unbosque.foresta.service.interfaces.IAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class AuthControllerImpl implements IAuthController {

    private final IAuthService service;
    private static final boolean COOKIE_SECURE = false;

    public AuthControllerImpl(IAuthService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequestDTO req) {
        LoginResponse res = service.login(req);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();


        res.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @Override
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) RefreshTokenRequestDTO req
    ) {
        String token = (cookieToken != null) ? cookieToken :
                (req != null ? req.getRefreshToken() : null);

        LoginResponse res = service.refresh(new RefreshTokenRequestDTO(token));

        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        res.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @Override
    public ResponseEntity<Void> recuperar(RecoverPasswordRequestDTO req) {
        service.solicitarRecuperacion(req);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> restablecer(ResetPasswordRequestDTO req) {
        service.restablecer(req);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UsuarioDTO> me(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return ResponseEntity.ok(service.me(user.getUsername()));
    }

    @Override
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String tokenCookie,
            @RequestBody(required = false) LogoutRequestDTO body
    ) {
        String token = (tokenCookie != null) ? tokenCookie :
                (body != null ? body.getRefreshToken() : null);

        if (token != null && !token.isBlank()) {
            service.logout(token);
        }

        ResponseCookie delete = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, delete.toString())
                .build();
    }
}
