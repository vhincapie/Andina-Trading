package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.repository.IUsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Map;
import co.edu.unbosque.foresta.auth.audit.AuditSender;
import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final IUsuarioRepository repo;
    private final AuditSender auditSender;

    public CustomUserDetailsServiceImpl(IUsuarioRepository repo, AuditSender auditSender){
        this.repo = repo;
        this.auditSender = auditSender;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        var u = repo.findByCorreo(correo).orElseThrow(() -> {
            auditSender.log("", new AuditLogRequest(
                    "AUTH_USER_LOOKUP_NOT_FOUND",
                    "/security/userdetails",
                    "Usuario no encontrado",
                    Map.of("correo", correo)
            ));
            return new UsernameNotFoundException("no existe");
        });

        var auth = new SimpleGrantedAuthority("ROLE_" + u.getRol().getNombre().name());

        auditSender.log("", new AuditLogRequest(
                "AUTH_USER_LOADED",
                "/security/userdetails",
                "Usuario cargado",
                Map.of("correo", u.getCorreo(), "rol", u.getRol().getNombre().name())
        ));

        return new User(u.getCorreo(), u.getContrasenaHash(), List.of(auth));
    }
}
