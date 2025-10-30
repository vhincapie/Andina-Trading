package co.edu.unbosque.foresta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class JwtService {
    @Value("${jwt.issuer}") private String issuer;
    @Value("${jwt.secret}") private String secret;

    public Jws<Claims> parse(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }
}
