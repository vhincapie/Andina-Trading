package co.edu.unbosque.foresta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private static final List<String> PUBLIC_BASES = List.of(
            "/actuator",
            "/api/auth",
            "/api/catalogos/paises",
            "/api/catalogos/ciudades",
            "/api/inversionistas/registrar"
    );

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            String rawPath = exchange.getRequest().getURI().getPath();
            String path = normalize(rawPath);

            if (isPublic(path)) {
                return chain.filter(exchange);
            }

            String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (auth == null || !auth.startsWith("Bearer ")) {
                return unauthorized(exchange, "Falta Authorization: Bearer <token>");
            }

            try {
                String token = auth.substring(7).trim();
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                var mutated = exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .header("X-User-Id", claims.getSubject() == null ? "" : claims.getSubject())
                                .build()
                ).build();

                return chain.filter(mutated);

            } catch (Exception e) {
                return unauthorized(exchange, "JWT inválido o expirado");
            }
        };
    }

    private boolean isPublic(String path) {
        for (String base : PUBLIC_BASES) {
            if (path.equals(base)) return true;
            if (path.startsWith(base + "/")) return true;
        }
        return false;
    }

    private String normalize(String p) {
        if (p != null && p.length() > 1 && p.endsWith("/")) {
            return p.substring(0, p.length() - 1);
        }
        return p;
    }

    private Mono<Void> unauthorized(ServerWebExchange ex, String msg) {
        ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        var bytes = msg.getBytes(StandardCharsets.UTF_8);
        return ex.getResponse().writeWith(Mono.just(
                ex.getResponse().bufferFactory().wrap(bytes)
        ));
    }
}
