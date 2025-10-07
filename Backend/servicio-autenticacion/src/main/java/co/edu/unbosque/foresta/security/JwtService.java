package co.edu.unbosque.foresta.security;

import co.edu.unbosque.foresta.model.entity.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.issuer}") private String issuer;
    @Value("${jwt.access-exp-seconds}") private long accessExpSeconds;
    @Value("${jwt.secret}") private String secret;

    public String generarAccess(Usuario u){
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(u.getCorreo())
                .claim("role", u.getRol().getNombre().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessExpSeconds)))
                .setIssuer(issuer)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }
}
