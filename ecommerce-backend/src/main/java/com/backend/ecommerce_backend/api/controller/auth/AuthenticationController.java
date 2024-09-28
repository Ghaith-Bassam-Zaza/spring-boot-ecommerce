package com.backend.ecommerce_backend.api.controller.auth;


import com.backend.ecommerce_backend.api.exceptions.EmailFailureException;
import com.backend.ecommerce_backend.api.exceptions.UserAlreadyExistsException;
import com.backend.ecommerce_backend.api.exceptions.UserNotVerifiedException;
import com.backend.ecommerce_backend.api.model.LoginBody;
import com.backend.ecommerce_backend.api.model.LoginResponse;
import com.backend.ecommerce_backend.api.model.RegistrationBody;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    UserService userService;

    /**
     * Constructor
     *
     * @param userService injected by spring boot
     *
     */
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * process a new user Request.
     *
     * @param body the first integer
     * @return a ResponseEntity with only a suitable http status.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegistrationBody body) {
        try { // if user registered successfully return ok
            userService.registerUser(body);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException e) { // if username or email already exists return conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) { // if verification email sending failed return internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * process login request.
     *
     * @param body the first integer
     * @return a ResponseEntity with only a suitable http status.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginBody body) {
        String jwt = null;
        try { // send login request to user service
            jwt = userService.loginUser(body);
        } catch (UserNotVerifiedException e) { // if email is not verified login fails
            String reason = "USER_NOT_VERIFIED" + (e.isNewEmailSent()?"_EMAIL_RESENT":""); // if no email is sent in the last out send a new email and tell user u sent one
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new LoginResponse(null,false,reason)); // return forbidden status with the reason of failure
        } catch (EmailFailureException e) { // if verification email sending failed return internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // check if user exists
        return jwt == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() // if no such user return unauthorized
                : ResponseEntity.ok().body(new LoginResponse(jwt,true,null)); // else return the JWT token with ok status
    }

    /**
     * shows the data of the logged-in user.
     *
     * @param user injected by spring boot from the received token
     * @return user data.
     */
    @GetMapping("/me")
    public LocalUser getCurrentUser(@AuthenticationPrincipal LocalUser user) {
        return user;
    }

    /**
     * verify the account using the token sent in email.
     *
     * @param token verification token sent in email as link param.
     * @return ok if email is verified and conflict in case of no such token or user already verified.
     */
    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token) {
        if(userService.verifyUser(token)) {
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
