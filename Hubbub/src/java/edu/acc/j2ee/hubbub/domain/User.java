package edu.acc.j2ee.hubbub.domain;

import java.util.Date;

public class User implements java.io.Serializable {
    public static final String USERNAME_PATTERN = "^\\w{6,12}$";
    public static final String PASSWORD_PATTERN = "^[^<>'\"]{8,16}$";
    private static final String HASH_PATTERN = "^\\$.+\\$.+\\$.+$";

    private String username;
    private String password;
    private Date joinDate;

    public User() {
    }
    
    public User(String username, String password) {
        this(username, password, new Date());
    }

    public User(String username, String password, Date joinDate) {
        this.username = username;
        this.password = password;
        this.joinDate = joinDate;
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

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public final void setPassword(String password) {
        if (password.matches(PASSWORD_PATTERN) || password.matches(HASH_PATTERN))
            this.password = password;
        else throw new IllegalArgumentException("Password is invalid");
    }

    @Override
    public String toString() {
        return username;
    }

}