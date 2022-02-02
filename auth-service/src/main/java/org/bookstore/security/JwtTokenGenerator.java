package org.bookstore.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenGenerator {

    @Value("${bookstore.auth.issuer}")
    private String issuer;
    @Value("${bookstore.auth.signatureSecret}")
    private String signatureSecret;
    @Value("${bookstore.auth.validityPeriod}")
    private long validityPeriod;

    public String generateToken(Authentication authentication) {
        Date expiration = new Date(System.currentTimeMillis() + validityPeriod);
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Algorithm algorithm = Algorithm.HMAC256(signatureSecret);
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(authentication.getName())
            .withExpiresAt(expiration)
            .withClaim("authorities", authorities)
            .sign(algorithm);
    }
}
