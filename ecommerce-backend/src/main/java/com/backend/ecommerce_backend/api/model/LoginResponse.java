package com.backend.ecommerce_backend.api.model;

public class LoginResponse {
    private final String token;
    private boolean success;
    private String failureReason;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LoginResponse(String token, boolean success, String failureReason) {
        this.token = token;
        this.success = success;
        this.failureReason = failureReason;
    }


    public String getToken() {
        return token;
    }

}
