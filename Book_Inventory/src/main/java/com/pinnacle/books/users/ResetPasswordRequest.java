package com.pinnacle.books.users;

//public class ResetPasswordRequest {
//
//    private String token;
//    private String newPassword;
//
//    // Getters and setters
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public String getNewPassword() {
//        return newPassword;
//    }
//
//    public void setNewPassword(String newPassword) {
//        this.newPassword = newPassword;
//    }
//}

public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    // Default Constructor
    public ResetPasswordRequest() {
    }

    // Constructor with parameters
    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    // ✅ Getter for token
    public String getToken() {
        return token;
    }

    // ✅ Setter for token
    public void setToken(String token) {
        this.token = token;
    }

    // ✅ Getter for newPassword
    public String getNewPassword() {
        return newPassword;
    }

    // ✅ Setter for newPassword
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}


