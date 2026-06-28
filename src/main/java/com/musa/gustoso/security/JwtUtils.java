package com.musa.gustoso.security;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.musa.gustoso.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generaToken(User user) {
        Date adesso = new Date();
        Date scadenza = new Date(adesso.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("ruolo", user.getRuolo().name())
                .issuedAt(adesso)
                .expiration(scadenza)
                .signWith(getKey())
                .compact();
    }

    public String estraiUsername(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}