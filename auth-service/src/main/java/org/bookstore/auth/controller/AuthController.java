package org.bookstore.auth.controller;

import org.bookstore.security.JwtTokenGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class AuthController {

    private final JwtTokenGenerator jwtTokenGenerator;

    public AuthController(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @GetMapping(path = "/token", produces = APPLICATION_JSON_VALUE)
    public String getToken(Authentication authentication) {
        //return String.format("%06x", new Random().nextInt(0x1000000));
        return jwtTokenGenerator.generateToken(authentication);
    }
}
