package com.backend.ecommerce_backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend.ecommerce_backend.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;

    private static final String USERNAME_key = "USERNAME";
    private static final String VERIFICATION_EMAIL_key = "VERIFICATION_EMAIL";
    private static final String RESET_PASSWORD_EMAIL_key = "RESET_PASSWORD_EMAIL";

    @PostConstruct
    public void PostConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateToken(LocalUser user) {
        return JWT.create().withClaim(USERNAME_key,user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationToken(LocalUser user) {
        return JWT.create().withClaim(VERIFICATION_EMAIL_key,user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generatePasswordRestToken(LocalUser user) {
        return JWT.create().withClaim(RESET_PASSWORD_EMAIL_key,user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * 30 * 60)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getResetPasswordEmail(String token) {
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(RESET_PASSWORD_EMAIL_key).asString();
    }


    public String getUsernameFromToken(String token) throws JWTDecodeException {
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(USERNAME_key).asString();
    }



}
