package com.backend.ecommerce_backend.model.dao;

import com.backend.ecommerce_backend.model.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressRepo extends ListCrudRepository<Address, Long> {
    List<Address> findByUser_Id(Long id);
}
