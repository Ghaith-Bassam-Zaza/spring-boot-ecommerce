package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.api.exceptions.UserAlreadyExistsException;
import com.backend.ecommerce_backend.api.model.RegistrationBody;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final LocalUserRepo localUserRepo;
    private final EncryptionService encryptionService;

    @Autowired
    public UserService(LocalUserRepo localUserRepo,EncryptionService encryptionService) {
        this.localUserRepo = localUserRepo;
        this.encryptionService = encryptionService;
    }

    public LocalUser registerUser(RegistrationBody user) throws UserAlreadyExistsException {
        if (localUserRepo.findByUsernameIgnoreCase(user.getUsername()).isPresent()
            || localUserRepo.findByEmailIgnoreCase(user.getEmail()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser localUser = new LocalUser();
        localUser.setUsername(user.getUsername());
        localUser.setEmail(user.getEmail());
        localUser.setFirstName(user.getFirstName());
        localUser.setLastName(user.getLastName());
        localUser.setPassword(encryptionService.encryptPassword(user.getPassword()));
        //System.out.println("recieved now saving:" + localUser.getEmail() + " , " + user.getFirstName() + " " + localUser.getLastName() + " " + localUser.getPassword() + " " + localUser.getUsername());
        return localUserRepo.save(localUser);

    }
}
