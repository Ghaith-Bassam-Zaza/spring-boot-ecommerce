package com.backend.ecommerce_backend.api.controller.order;

import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.WebOrder;
import com.backend.ecommerce_backend.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    OrderService orderService;

    /**
     * Constructor.
     *
     * @param orderService injected by spring boot
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    /**
     * shows the orders of the logged-in user.
     *
     * @param user injected by spring boot from the received token
     * @return user orders.
     */
    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user) {
        return orderService.getOrders(user);
    }
}
