package edu.acc.j2ee.hubbub.domain;

import java.util.Date;

public class User implements java.io.Serializable {
    public static final String USERNAME_PATTERN = "^\\w{6,12}$";
    public static final String PASSWORD_PATTERN = "^[^<>'\"]{8,16}$";
    private static final String HASH_PATTERN = "^\\$.+\\$.+\\$.+$";

    private String username;
    private String password;
    private String hash;
    private Profile profile;

    public User() {
    }
    
    public User(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public final void setUsername(String username) {
        if (username.matches(USERNAME_PATTERN))
            this.username = username;
        else throw new IllegalArgumentException("Username is invalid");
    }

    public String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        if (password.matches(PASSWORD_PATTERN)) {
            this.password = password;
            this.hash = null;
        }
        else throw new IllegalArgumentException("Password is invalid");
    }
    
    public String getHash() {
        return hash;
    }
    
    public final void setHash(String hash) {
        if (hash.matches(HASH_PATTERN)) {
            this.hash = hash;
            this.password = null;
        }
        else throw new IllegalArgumentException("Password hash is invalid");
    }
    
    public Profile getProfile() {
        return profile;
    }
    
    public final void setProfile(Profile profile) {
        if (profile == null)
            throw new IllegalArgumentException("Profile is null");
        else
            this.profile = profile;
    }

    @Override
    public String toString() {
        return username;
    }

}