package com.maroufb.beastchat.Entities;

public class User {
    private String email;
    private String userPicture;
    private String userName;
    private boolean hasLoggedIn;

    public User() {
    }

    public User(String email, String userPicture, String userName, boolean hasLoggedIn) {
        this.email = email;
        this.userPicture = userPicture;
        this.userName = userName;
        this.hasLoggedIn = hasLoggedIn;
    }

    public String getEmail() {
        return email;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isHasLoggedIn() {
        return hasLoggedIn;
    }
}
