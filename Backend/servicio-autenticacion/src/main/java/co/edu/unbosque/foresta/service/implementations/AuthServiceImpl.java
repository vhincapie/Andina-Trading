package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.exceptions.AuthException;
import co.edu.unbosque.foresta.model.DTO.LoginRequestDTO;
import co.edu.unbosque.foresta.model.DTO.RecoverPasswordRequestDTO;
import co.edu.unbosque.foresta.model.DTO.RefreshTokenRequestDTO;
import co.edu.unbosque.foresta.model.DTO.ResetPasswordRequestDTO;
import co.edu.unbosque.foresta.model.DTO.UsuarioDTO;
import co.edu.unbosque.foresta.model.entity.LoginResponse;
import co.edu.unbosque.foresta.model.entity.PasswordResetToken;
import co.edu.unbosque.foresta.model.entity.RefreshToken;
import co.edu.unbosque.foresta.model.entity.Usuario;
import co.edu.unbosque.foresta.repository.IUsuarioRepository;
import co.edu.unbosque.foresta.repository.IRefreshTokenRepository;
import co.edu.unbosque.foresta.repository.IPasswordResetTokenRepository;
import co.edu.unbosque.foresta.security.JwtService;
import co.edu.unbosque.foresta.service.interfaces.IAuthService;
import co.edu.unbosque.foresta.service.interfaces.IEmailService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final int MAX_INTENTOS_FALLIDOS = 3;
    private static final long VIDA_REFRESH_SEGUNDOS = 7 * 24 * 3600; // 7 días
    private static final long VIDA_ACCESS_SEGUNDOS = 900;            // 15 min

    private final IUsuarioRepository usuarioRepo;
    private final IRefreshTokenRepository refreshRepo;
    private final IPasswordResetTokenRepository resetRepo;
    private final BCryptPasswordEncoder encriptador;
    private final JwtService jwt;
    private final IEmailService emailService;
    private final ModelMapper modelMapper;

    public AuthServiceImpl(IUsuarioRepository usuarioRepo,
                           IRefreshTokenRepository refreshRepo,
                           IPasswordResetTokenRepository resetRepo,
                           BCryptPasswordEncoder encriptador,
                           JwtService jwt,
                           ModelMapper modelMapper,
                           IEmailService emailService) {
        this.usuarioRepo = usuarioRepo;
        this.refreshRepo = refreshRepo;
        this.resetRepo = resetRepo;
        this.encriptador = encriptador;
        this.jwt = jwt;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    @Override
    public LoginResponse login(LoginRequestDTO solicitud) {
        Usuario usuario = obtenerUsuarioPorCorreo(solicitud.getCorreo());
        validarNoBloqueado(usuario);
        verificarContrasenaOIncrementarIntentos(usuario, solicitud.getContrasena());
        reiniciarIntentosYRegistrarUltimoLogin(usuario);

        String tokenAcceso = generarTokenAcceso(usuario);
        RefreshToken refreshToken = crearYGuardarRefreshToken(usuario);

        return construirRespuestaLogin(usuario, tokenAcceso, refreshToken.getToken());
    }

    @Override
    public LoginResponse refresh(RefreshTokenRequestDTO solicitud) {
        String token = (solicitud != null ? solicitud.getRefreshToken() : null);
        if (token == null || token.isBlank()) {
            throw new AuthException("REFRESH_TOKEN_FALTANTE");
        }

        RefreshToken rt = validarRefreshToken(token);
        Usuario usuario = rt.getUsuario();
        String access = generarTokenAcceso(usuario);
        return construirRespuestaLogin(usuario, access, token);
    }

    @Override
    public void logout(String refreshToken) {
        var rt = validarRefreshToken(refreshToken);
        rt.setRevocado(true);
        refreshRepo.save(rt);
    }

    @Override
    public void solicitarRecuperacion(RecoverPasswordRequestDTO solicitud) {
        usuarioRepo.findByCorreo(solicitud.getCorreo()).ifPresent(user -> {
            var pr = new PasswordResetToken();
            pr.setUsuario(user);
            pr.setToken(java.util.UUID.randomUUID().toString());
            pr.setExpiraEn(java.time.Instant.now().plusSeconds(30 * 60)); // 30 min
            pr.setUsado(false);
            resetRepo.save(pr);

            emailService.enviarResetPassword(user.getCorreo(), pr.getToken());
        });
    }

    @Override
    public void restablecer(ResetPasswordRequestDTO solicitud) {
        PasswordResetToken resetToken = validarTokenRecuperacion(solicitud.getToken());
        actualizarContrasenaYMarcarTokenUsado(resetToken.getUsuario(), resetToken, solicitud.getNuevaContrasena());
    }

    @Override
    public UsuarioDTO me(String correo) {
        Usuario usuario = obtenerUsuarioPorCorreo(correo);
        return convertirADTO(usuario);
    }

    private Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new AuthException("CREDENCIALES_INVALIDAS"));
    }

    private void validarNoBloqueado(Usuario usuario) {
        if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
            throw new AuthException("CUENTA_BLOQUEADA");
        }
    }

    private void verificarContrasenaOIncrementarIntentos(Usuario usuario, String contrasenaIngresada) {
        if (!encriptador.matches(contrasenaIngresada, usuario.getContrasenaHash())) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            usuarioRepo.save(usuario);
            throw new AuthException("CREDENCIALES_INVALIDAS");
        }
    }

    private void reiniciarIntentosYRegistrarUltimoLogin(Usuario usuario) {
        usuario.setIntentosFallidos(0);
        usuario.setUltimoLogin(Instant.now());
        usuarioRepo.save(usuario);
    }

    private String generarTokenAcceso(Usuario usuario) {
        return jwt.generarAccess(usuario);
    }

    private RefreshToken crearYGuardarRefreshToken(Usuario usuario) {
        RefreshToken rt = new RefreshToken();
        rt.setUsuario(usuario);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiraEn(Instant.now().plusSeconds(VIDA_REFRESH_SEGUNDOS));
        return refreshRepo.save(rt);
    }

    private RefreshToken validarRefreshToken(String token) {
        RefreshToken rt = refreshRepo.findByToken(token)
                .orElseThrow(() -> new AuthException("REFRESH_TOKEN_INVALIDO"));
        if (rt.isRevocado()) {
            throw new AuthException("REFRESH_TOKEN_REVOCADO");
        }
        if (rt.getExpiraEn().isBefore(Instant.now())) {
            throw new AuthException("REFRESH_TOKEN_EXPIRADO");
        }
        return rt;
    }

    private PasswordResetToken validarTokenRecuperacion(String token) {
        PasswordResetToken pr = resetRepo.findByToken(token)
                .orElseThrow(() -> new AuthException("TOKEN_INVALIDO"));
        if (pr.isUsado() || pr.getExpiraEn().isBefore(Instant.now())) {
            throw new AuthException("TOKEN_EXPIRADO");
        }
        return pr;
    }

    private void actualizarContrasenaYMarcarTokenUsado(Usuario usuario, PasswordResetToken pr, String nuevaContrasena) {
        usuario.setContrasenaHash(encriptador.encode(nuevaContrasena));
        usuarioRepo.save(usuario);

        pr.setUsado(true);
        resetRepo.save(pr);
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    private LoginResponse construirRespuestaLogin(Usuario usuario, String tokenAcceso, String refreshToken) {
        UsuarioDTO dto = convertirADTO(usuario);

        LoginResponse res = new LoginResponse();
        res.setAccessToken(tokenAcceso);
        res.setRefreshToken(refreshToken);
        res.setExpiraEn(VIDA_ACCESS_SEGUNDOS);
        res.setRol(dto.getRol());
        res.setUsuario(dto);
        return res;
    }
}
