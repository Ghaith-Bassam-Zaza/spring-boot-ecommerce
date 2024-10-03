package com.backend.ecommerce_backend.api.controller.order;

import com.backend.ecommerce_backend.model.WebOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testUnauthenticatedOrderList() throws Exception {
        mockMvc.perform(get("/order")).andExpect(status().isForbidden());
    }
    @Test
    @WithUserDetails("UserB")
    public void testUserAAuthenticatedOrderList() throws Exception {
        testAuthenticationListBelongToUser("UserA");
    }
    @Test
    @WithUserDetails("UserB")
    public void testUserBAuthenticatedOrderList() throws Exception {
        testAuthenticationListBelongToUser("UserB");
    }
    private void testAuthenticationListBelongToUser(String username) throws Exception {
        mockMvc.perform(get("/order")).andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    List<WebOrder> orders = new ObjectMapper().readValue(body, new TypeReference<List<WebOrder>>(){});
                    for (WebOrder order : orders) {
                        Assertions.assertEquals(username, order.getUser().getUsername(),"Order list Shouldonly be of the user.");
                    }
                });
    }

}
