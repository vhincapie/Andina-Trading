package co.edu.unbosque.foresta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthenticationFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            Jws<Claims> jws = jwt.parse(token);
            Claims claims = jws.getPayload();

            String username = claims.getSubject();

            Object rolesObj = claims.get("roles");
            if (rolesObj == null) rolesObj = claims.get("authorities");
            if (rolesObj == null) rolesObj = claims.get("role");
            if (rolesObj == null) rolesObj = claims.get("rol");
            if (rolesObj == null) rolesObj = claims.get("scope");

            List<String> roles;
            if (rolesObj instanceof List<?> rawList) {
                roles = rawList.stream()
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .toList();
            } else if (rolesObj instanceof String s) {
                roles = Arrays.stream(s.split("[,\\s]+"))
                        .filter(t -> !t.isBlank())
                        .toList();
            } else {
                roles = List.of();
            }

            Collection<? extends GrantedAuthority> authorities = roles.stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
