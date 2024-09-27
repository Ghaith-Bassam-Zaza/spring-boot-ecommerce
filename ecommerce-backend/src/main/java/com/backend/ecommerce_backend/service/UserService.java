package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.api.exceptions.EmailFailureException;
import com.backend.ecommerce_backend.api.exceptions.UserAlreadyExistsException;
import com.backend.ecommerce_backend.api.exceptions.UserNotVerifiedException;
import com.backend.ecommerce_backend.api.model.LoginBody;
import com.backend.ecommerce_backend.api.model.RegistrationBody;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.VerificationToken;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import com.backend.ecommerce_backend.model.dao.VerificationTokenRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final LocalUserRepo localUserRepo;
    private final VerificationTokenRepo verificationTokenRepo;
    private final EncryptionService encryptionService;
    private final JWTService jwtService;
    private final EmailService emailService;

    @Autowired
    public UserService(LocalUserRepo localUserRepo, VerificationTokenRepo verificationTokenRepo, EncryptionService encryptionService, JWTService jwtService, EmailService emailService) {
        this.localUserRepo = localUserRepo;
        this.verificationTokenRepo = verificationTokenRepo;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public LocalUser registerUser(RegistrationBody user) throws UserAlreadyExistsException, EmailFailureException {
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
        VerificationToken verificationToken = createVerificationToken(localUser);
        emailService.sendVerificationMail(verificationToken);
        verificationTokenRepo.save(verificationToken);
        return localUserRepo.save(localUser);

    }

    public VerificationToken createVerificationToken(LocalUser localUser) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationToken(localUser));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setLocalUser(localUser);
        localUser.getVerificationTokens().add(verificationToken);
        return verificationToken;

    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserRepo.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            if (encryptionService.checkPassword(loginBody.getPassword(), user.getPassword())){
                if (! user.getEmailVerified()){
                    List<VerificationToken> tokens = user.getVerificationTokens();
                    boolean resend = tokens.isEmpty()
                            || tokens.get(0).getCreatedTimeStamp().getTime() < System.currentTimeMillis() - 60 * 60 * 1000;
                    if(resend){
                        VerificationToken token = createVerificationToken(user);
                        verificationTokenRepo.save(token);
                        emailService.sendVerificationMail(token);
                    }
                    throw new UserNotVerifiedException(resend);
                }
                return jwtService.generateToken(user);
            }
        }
        return null;
    }
    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenRepo.findByToken(token);
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getLocalUser();
            if(!user.getEmailVerified()){
                user.setEmailVerified(true);
                localUserRepo.save(user);
                verificationTokenRepo.deleteByLocalUser(verificationToken.getLocalUser());
                return true;
            }

        }
        return false;
    }
}
