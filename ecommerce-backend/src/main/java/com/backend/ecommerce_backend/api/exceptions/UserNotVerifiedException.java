package com.backend.ecommerce_backend.api.exceptions;

public class UserNotVerifiedException extends Exception {
    private final boolean newEmailSent;

    public UserNotVerifiedException(boolean newEmailSent) {
        this.newEmailSent = newEmailSent;
    }
    public boolean isNewEmailSent() {
        return newEmailSent;
    }
}
