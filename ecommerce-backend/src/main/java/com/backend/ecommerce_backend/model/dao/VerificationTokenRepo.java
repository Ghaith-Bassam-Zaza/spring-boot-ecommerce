package com.backend.ecommerce_backend.model.dao;

import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationTokenRepo extends ListCrudRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByLocalUser(LocalUser localUser);

}
