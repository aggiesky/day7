package edu.acc.j2ee.hubbub.domain;

import java.util.ArrayList;
import java.util.List;

public class Profile implements java.io.Serializable {
    public static final String NAME_PATTERN = "^[A-Za-z]{1,50}$";
    public static final String EMAIL_PATTERN = "^[\\w\\-\\.]+@[\\w\\-\\.]+$";
    
    private User   owner;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String biography;
    private byte[] avatar;
    private String mime;
    private List<User> followees = new ArrayList<>();
    private List<User> followers = new ArrayList<>();
    private int    id;
    
    public Profile() {
    }
    
    public Profile(User owner) {
        this.owner = owner;
    }

    public Profile(User owner, String firstName, String lastName, 
            String emailAddress, String biography) {
        this.owner = owner;
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmailAddress(emailAddress);
        this.biography = biography;
    }
    
    public Profile(User owner, String firstName, String lastName, 
            String emailAddress, String biography, int id) {
        this(owner, firstName, lastName, emailAddress, biography);
        this.id = id;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
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
            throw new IllegalArgumentException("Name format is invalid: "
                    + lastName);

    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public final void setEmailAddress(String emailAddress) {
        if (emailAddress == null || emailAddress.length() == 0 || emailAddress.matches(EMAIL_PATTERN))
            this.emailAddress = emailAddress;
        else
            throw new IllegalArgumentException("Email format is invalid: "
                    + emailAddress);

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
    
    public List<User> getFollowees() {
        return followees;
    }
    
    public void setFollowees(List<User> followees) {
        this.followees = followees;
    }
    
    public List<User> getFollowers() {
        return followers;
    }
    
    public void setFollowers(List<User> followers) {
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
        return String.format("Profile[%s, %s, %d]", owner, mime, id);
    }
}
