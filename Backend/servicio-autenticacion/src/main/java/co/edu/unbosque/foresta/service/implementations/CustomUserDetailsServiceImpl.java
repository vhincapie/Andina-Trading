package co.edu.unbosque.foresta.service.implementations;

import co.edu.unbosque.foresta.repository.IUsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final IUsuarioRepository repo;

    public CustomUserDetailsServiceImpl(IUsuarioRepository repo){ this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        var u = repo.findByCorreo(correo).orElseThrow(() -> new UsernameNotFoundException("no existe"));
        var auth = new SimpleGrantedAuthority("ROLE_" + u.getRol().getNombre().name());
        return new User(u.getCorreo(), u.getContrasenaHash(), List.of(auth));
    }
}
