package co.edu.unbosque.foresta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthenticationFilter(JwtService jwt) { this.jwt = jwt; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) { chain.doFilter(req, res); return; }
        if (SecurityContextHolder.getContext().getAuthentication() != null) { chain.doFilter(req, res); return; }

        String token = auth.substring(7);
        try {
            Jws<Claims> jws = jwt.parse(token);
            Claims c = jws.getPayload();
            String username = c.getSubject();

            String rol = c.get("rol", String.class);
            if (rol == null) rol = c.get("role", String.class);

            List<String> rolesArray = c.get("roles", List.class);
            List<SimpleGrantedAuthority> auths = new ArrayList<>();
            if (rolesArray != null) {
                for (String r : rolesArray) auths.add(new SimpleGrantedAuthority(r.startsWith("ROLE_")? r : "ROLE_"+r));
            } else if (rol != null) {
                auths.add(new SimpleGrantedAuthority(rol.startsWith("ROLE_")? rol : "ROLE_"+rol));
            }

            UsernamePasswordAuthenticationToken at = new UsernamePasswordAuthenticationToken(username, null, auths);
            at.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(at);
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(req, res);
    }
}