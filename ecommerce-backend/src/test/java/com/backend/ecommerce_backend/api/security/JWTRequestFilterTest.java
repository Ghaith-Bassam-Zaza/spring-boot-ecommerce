package com.backend.ecommerce_backend.api.security;

import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import com.backend.ecommerce_backend.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserRepo localUserRepo;
    private static final String AUTHENTICATED_PATH = "/auth/me";


    @Test
    public void testUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().isForbidden());
    }
    @Test
    public void testBadTokenRequest() throws Exception {
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","invalidTokenForTesting"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer invalidTokenForTesting"))
                .andExpect(status().isForbidden());
    }
    @Test
    public void testUnverifiedUser() throws Exception {
        LocalUser user = localUserRepo.findByUsernameIgnoreCase("UserB").get();
        String token = jwtService.generateToken(user);
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer " + token)).andExpect(status().isForbidden());
    }
    @Test
    public void testVerifiedUser() throws Exception {
        LocalUser user = localUserRepo.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateToken(user);
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer " + token)).andExpect(status().isOk());
    }
}
