package ru.gamerivan.springsecurity.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtCore {
    private String secret;
    private int lifetime;
    private SecretKey key;

    public JwtCore(@Value("${springsecurity.app.secret}") String secret, @Value("${springsecurity.app.lifetime}") int lifetime) {
        this.secret = secret;
        this.lifetime = lifetime;
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().subject((userDetails.getUsername())).issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + lifetime))
                .signWith(key).compact();
    }

    public String getNameFromJwt(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
