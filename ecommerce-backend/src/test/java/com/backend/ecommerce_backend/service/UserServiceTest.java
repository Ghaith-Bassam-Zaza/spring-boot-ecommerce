package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.api.exceptions.EmailFailureException;
import com.backend.ecommerce_backend.api.exceptions.UserAlreadyExistsException;
import com.backend.ecommerce_backend.api.exceptions.UserNotVerifiedException;
import com.backend.ecommerce_backend.api.model.LoginBody;
import com.backend.ecommerce_backend.api.model.RegistrationBody;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
public class UserServiceTest {

    @MockBean
    private SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer;


    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot","secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;


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
        loginBody.setPassword("Password@A123");
        Assertions.assertNotNull(userService.loginUser(loginBody),"User should login successfully!");
        loginBody.setUsername("UserB");
        loginBody.setPassword("Password@B123");
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
}
