package com.backend.ecommerce_backend.api.controller.auth;


import com.backend.ecommerce_backend.api.exceptions.UserAlreadyExistsException;
import com.backend.ecommerce_backend.api.model.LoginBody;
import com.backend.ecommerce_backend.api.model.LoginResponse;
import com.backend.ecommerce_backend.api.model.RegistrationBody;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    UserService userService;
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegistrationBody body) {
        try {
            userService.registerUser(body);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginBody body) {
        String jwt = userService.loginUser(body);
        return jwt == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok().body(new LoginResponse(jwt));
    }

    @GetMapping("/me")
    public LocalUser getCurrentUser(@AuthenticationPrincipal LocalUser user) {
        return user;
    }
}
