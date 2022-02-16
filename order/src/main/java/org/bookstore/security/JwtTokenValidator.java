package org.bookstore.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenValidator {

    private static String issuer = "https://bookstore.org/auth";
    private static String signatureSecret = "01234567890123456789012345678901";

    public static Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(signatureSecret);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
        DecodedJWT jwt = verifier.verify(token);
        String username = jwt.getSubject();
        List<String> authorities = jwt.getClaim("authorities").asList(String.class);
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).toList();
        return new JwtAuthenticationToken(token, username, grantedAuthorities);
    }
}
