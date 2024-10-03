package com.backend.ecommerce_backend.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class EncryptionServiceTest {
    @Autowired
    private EncryptionService encryptionService;

    @Test
    @Transactional
    public void testEncryptPassword() {
        String password = "Password@Secret!123";
        String encryptedPassword = encryptionService.encryptPassword(password);
        Assertions.assertTrue(encryptionService.checkPassword(password, encryptedPassword),"Encrypted password should match original!");
        Assertions.assertFalse(encryptionService.checkPassword("wrongPassword", encryptedPassword),"Encrypted password should match wrong password!");

    }
}
