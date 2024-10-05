package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.api.exceptions.EmailFailureException;
import com.backend.ecommerce_backend.api.exceptions.EmailNotFoundException;
import com.backend.ecommerce_backend.api.exceptions.UserAlreadyExistsException;
import com.backend.ecommerce_backend.api.exceptions.UserNotVerifiedException;
import com.backend.ecommerce_backend.api.model.LoginBody;
import com.backend.ecommerce_backend.api.model.PasswordResetBody;
import com.backend.ecommerce_backend.api.model.RegistrationBody;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.VerificationToken;
import com.backend.ecommerce_backend.model.dao.LocalUserRepo;
import com.backend.ecommerce_backend.model.dao.VerificationTokenRepo;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {


    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot","secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenRepo verificationTokenRepo;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserRepo localUserRepo;
    @Autowired
    EncryptionService encryptionService;
    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody registrationBody = new RegistrationBody();
        registrationBody.setUsername("UserA");
        registrationBody.setEmail("UserServiceTest$testRegisterUser@JUnit.com");
        registrationBody.setPassword("UserServiceTest$testRegisterUser123");
        registrationBody.setFirstName("F_name");
        registrationBody.setLastName("L_name");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationBody),"Username should already be in use!");
        registrationBody.setUsername("UserServiceTest$testRegisterUser");
        registrationBody.setEmail("UserA@JUnit.com");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationBody),"Email should already be in use!");
        registrationBody.setEmail("UserServiceTest$testRegisterUser@JUnit.com");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(registrationBody),
                "User should register successfully!");
        Assertions.assertEquals(registrationBody.getEmail(),
                greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("UserA-notExist");
        loginBody.setPassword("UserServiceTest$testLoginUser123");
        Assertions.assertNull(userService.loginUser(loginBody),"User should not exist!");
        loginBody.setUsername("UserA");
        loginBody.setPassword("UserA-badPassword");
        Assertions.assertNull(userService.loginUser(loginBody),"Password should be incorrect!");
        loginBody.setPassword("password@A123");
        Assertions.assertNotNull(userService.loginUser(loginBody),"User should login successfully!");
        loginBody.setUsername("UserB");
        loginBody.setPassword("password@B123");
        try{
            userService.loginUser(loginBody);
            Assertions.fail("User should not have email verified!");
        }catch (UserNotVerifiedException e){
            Assertions.assertTrue(e.isNewEmailSent(),"email verification should be sent!");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);

        }
        try{
            userService.loginUser(loginBody);
            Assertions.fail("User should not have email verified!");
        }catch (UserNotVerifiedException e){
            Assertions.assertFalse(e.isNewEmailSent(),"email verification should not be resent!");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);

        }
    }
    @Test
    @Transactional
    public void testVerifyUser() throws UserNotVerifiedException, EmailFailureException {
        Assertions.assertFalse(userService.verifyUser("badToken"),"Token should be invalid!");
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("UserB");
        loginBody.setPassword("password@B123");
        try{
            userService.loginUser(loginBody);
            Assertions.fail("User should not have email verified!");
        }catch (UserNotVerifiedException e){
            List<VerificationToken> tokens =verificationTokenRepo.findByLocalUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(userService.verifyUser(token),"Token should be valid!");
            Assertions.assertNotNull(loginBody, "user should now be verified!");
        }
    }
    @Test
    @Transactional
    public void testForgotPassword() throws Exception{
        Assertions.assertThrows(EmailNotFoundException.class,
                () -> userService.forgotPassword("NonExist@JUnit.com"),"Invalid email should be rejected!");
        Assertions.assertDoesNotThrow(() -> userService.forgotPassword("UserA@Junit.com"));
        Assertions.assertEquals("UserA@Junit.com",
                greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString()
                ,"Reset Email should be sent");
    }
    @Test
    @Transactional
    public void testResetPassword() throws Exception{
        LocalUser user = localUserRepo.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordRestToken(user);
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("newPassword@A123");
        userService.resetPassword(body);
        user = localUserRepo.findByUsernameIgnoreCase("UserA").get();
        Assertions.assertTrue(encryptionService.checkPassword("newPassword@A123",user.getPassword())
                ,"Password should be updated!");
    }
}
