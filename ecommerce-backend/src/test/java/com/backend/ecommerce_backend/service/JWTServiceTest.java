package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTServiceTest {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserRepo localUserRepo;


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
}
