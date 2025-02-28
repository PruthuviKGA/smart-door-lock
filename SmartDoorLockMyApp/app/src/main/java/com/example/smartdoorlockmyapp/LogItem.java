package com.example.smartdoorlockmyapp;

public class LogItem {
    private String username;
    private String verifiedAt;

    public LogItem(String username, String verifiedAt) {
        this.username = username;
        this.verifiedAt = verifiedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getVerifiedAt() {
        return verifiedAt;
    }
}
