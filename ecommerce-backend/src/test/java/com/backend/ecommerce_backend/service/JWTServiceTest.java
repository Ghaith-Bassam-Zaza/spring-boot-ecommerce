package com.backend.ecommerce_backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserRepo localUserRepo;
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Test
    @Transactional
    public void testVerificationTokenNotUsableForLogin() {
        LocalUser localUser = localUserRepo.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateVerificationToken(localUser);
        Assertions.assertNull(jwtService.getUsernameFromToken(token),"Verification token should not contain username!");

    }
    @Test
    @Transactional
    public void testAuthTokenReturnsUsername() {
        LocalUser localUser = localUserRepo.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateToken(localUser);
        Assertions.assertEquals(localUser.getUsername(),jwtService.getUsernameFromToken(token),"Token should contain username!");

    }
    @Test
    public void testLoginJWTNotGeneratedByUs() throws Exception{
        String token = JWT.create().withClaim("USERNAME","UserA").sign(Algorithm.HMAC256("NotTheRealSecretCode"));
        Assertions.assertThrows(SignatureVerificationException.class,() -> jwtService.getUsernameFromToken(token));
    }
    @Test
    public void testLoginJWTCorrectlySignedNoIssuer() throws Exception{
        String token = JWT.create().withClaim("USERNAME","UserA").sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class,() -> jwtService.getUsernameFromToken(token));
    }
    @Test
    public void testPasswordResetToken() throws Exception{
        LocalUser localUser = localUserRepo.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordRestToken(localUser);
        Assertions.assertEquals(localUser.getEmail(),
                jwtService.getResetPasswordEmail(token),
                "Password Reset token should contain email!");
    }
    @Test
    public void testResetJWTNotGeneratedByUs() throws Exception{
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL","UserA@Junit.com").sign(Algorithm.HMAC256("NotTheRealSecretCode"));
        Assertions.assertThrows(SignatureVerificationException.class,() -> jwtService.getUsernameFromToken(token));
    }
    @Test
    public void testResetJWTCorrectlySignedNoIssuer() throws Exception{
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL","UserA@Junit.com").sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class,() -> jwtService.getUsernameFromToken(token));
    }
}
