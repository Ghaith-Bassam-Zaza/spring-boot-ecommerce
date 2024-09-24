package com.backend.ecommerce_backend.api.model;

import jakarta.validation.constraints.*;

public class RegistrationBody {
    @NotBlank
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @NotNull
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    private String password;

    @Email
    @NotBlank
    @NotNull
    @Size(min = 8, max = 255)
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 8, max = 55)
    private String firstName;

    @NotBlank
    @NotNull
    @Size(min = 8, max = 55)
    private String lastName;


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
