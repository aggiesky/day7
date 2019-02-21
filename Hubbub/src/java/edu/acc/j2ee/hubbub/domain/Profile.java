package edu.acc.j2ee.hubbub.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Profile implements java.io.Serializable {
    public static final String NAME_PATTERN = "^[A-Za-z]{1,50}$";
    public static final String EMAIL_PATTERN = "^[\\w\\-\\.]+@[\\w\\-\\.]+$";
    
    private String firstName;
    private String lastName;
    private Date   joinDate;
    private String emailAddress;
    private String biography;
    private byte[] avatar;
    private String mime;
    private List<String> followees = new ArrayList<>();
    private List<String> followers = new ArrayList<>();
    private int    id;
    
    public Profile() {
        joinDate = new Date();
    }

    public Profile(String firstName, String lastName, String emailAddress, String biography) {
        this();
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmailAddress(emailAddress);
        this.biography = biography;
    }
    
    public Profile(String firstName, String lastName, Date joinDate,
            String emailAddress, String biography, int id) {
        this(firstName, lastName, emailAddress, biography);
        this.joinDate = joinDate;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        if (firstName == null || firstName.length() == 0 || firstName.matches(NAME_PATTERN))
            this.firstName = firstName;
        else
            throw new IllegalArgumentException("Name format is invalid: " + firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        if (lastName == null || lastName.length() == 0 || lastName.matches(NAME_PATTERN))
            this.lastName = lastName;
        else
            throw new IllegalArgumentException("Name format is invalid: " + lastName);

    }
    
    public Date getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public final void setEmailAddress(String emailAddress) {
        if (emailAddress == null || emailAddress.length() == 0 || emailAddress.matches(EMAIL_PATTERN))
            this.emailAddress = emailAddress;
        else
            throw new IllegalArgumentException("Email format is invalid: " + emailAddress);
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public byte[] getAvatar() {
        return avatar;
    }
    
    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
    
    public String getMime() {
        return mime;
    }
    
    public void setMime(String mime) {
        this.mime = mime;
    }
    
    public List<String> getFollowees() {
        return followees;
    }
    
    public void setFollowees(List<String> followees) {
        this.followees = followees;
    }
    
    public List<String> getFollowers() {
        return followers;
    }
    
    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return String.format("Profile[id=%d]", id);
    }
}
