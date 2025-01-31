package com.example.api_gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// Es necesario que sea componente para que spring lo tenga en contexto.
// Para que?... Para que se pueda realizar la inyeccion de dependecias con el @AutoWired
@Component
public class JwtUtils {
    //La clave secreta que se va atulizar para firmar el JWT
    private final SecretKey secretKey;
    //Este anotador más el formato "${value}" le estamos diciendo a java que vaya a buscar ese string en el application.property
    // Estos datos sensibles deben ser obtenidos de variable de entornos.
    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(tokenUsername) && isTokenNotExpired(token));
    }

    Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenNotExpired(String token) {
        System.out.println("Expiration date "+ parseClaims(token).getExpiration());
        System.out.println("Dia de hoy "+ new Date());
        return parseClaims(token).getExpiration().after(new Date());
    }
}
