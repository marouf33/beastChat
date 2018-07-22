package com.maroufb.beastchat.Entities;

public class User {
    private String email;
    private String picture;
    private String userName;
    private boolean hasLoggedIn;

    public User() {
    }

    public User(String email, String picture, String userName, boolean hasLoggedIn) {
        this.email = email;
        this.picture = picture;
        this.userName = userName;
        this.hasLoggedIn = hasLoggedIn;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isHasLoggedIn() {
        return hasLoggedIn;
    }
}
