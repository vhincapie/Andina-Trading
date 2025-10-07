package co.edu.unbosque.foresta.service.interfaces;

import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.model.entity.LoginResponse;

public interface IAuthService {
    LoginResponse login(LoginRequestDTO request);
    LoginResponse refresh(RefreshTokenRequestDTO request);
    void solicitarRecuperacion(RecoverPasswordRequestDTO request);
    void restablecer(ResetPasswordRequestDTO request);
    UsuarioDTO me(String correoAutenticado);
    void logout(String refreshToken);
}
