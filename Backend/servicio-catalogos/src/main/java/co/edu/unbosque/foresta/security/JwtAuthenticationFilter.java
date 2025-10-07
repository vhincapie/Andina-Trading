package co.edu.unbosque.foresta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);
        try {
            Jws<Claims> jws = jwtService.parse(token);
            Claims claims = jws.getPayload();

            String username = claims.getSubject();

            String rol = claims.get("rol", String.class);
            if (rol == null) rol = claims.get("role", String.class);

            List<String> rolesArray = claims.get("roles", List.class);

            List<GrantedAuthority> authorities = new ArrayList<>();

            if (rolesArray != null) {
                for (String r : rolesArray) {
                    if (r != null && !r.isBlank()) {
                        String name = r.startsWith("ROLE_") ? r : "ROLE_" + r;
                        authorities.add(new SimpleGrantedAuthority(name));
                    }
                }
            } else if (rol != null && !rol.isBlank()) {
                String name = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;
                authorities.add(new SimpleGrantedAuthority(name));
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
