package com.backend.ecommerce_backend.model.dao;

import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderRepo extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByUser(LocalUser user);
}
