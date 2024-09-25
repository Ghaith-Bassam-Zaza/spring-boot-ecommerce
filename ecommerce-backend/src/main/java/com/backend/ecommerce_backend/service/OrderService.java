package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.WebOrder;
import com.backend.ecommerce_backend.model.dao.WebOrderRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private WebOrderRepo webOrderRepo;

    public OrderService(WebOrderRepo webOrderRepo) {
        this.webOrderRepo = webOrderRepo;
    }
    public List<WebOrder> getOrders(LocalUser user) {
        return webOrderRepo.findByUser(user);
    }
}
