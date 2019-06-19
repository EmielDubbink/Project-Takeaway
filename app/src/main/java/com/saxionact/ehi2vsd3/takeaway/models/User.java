package com.saxionact.ehi2vsd3.takeaway.models;

/**
 * The user class contains all necessary information about the user.
 *
 * @author Vincent Witten
 */
public class User {

    //users attributes
    private String userID;
    private String name;
    private String gmail;
    private String location;
    private boolean administrator;
    private boolean active;

    public User() {
    }

    /**
     * User constructor.
     *
     * @author Vincent Witten
     *
     * @param userID a unique id to identify the user with
     * @param name The users name
     * @param gmail the gmail of the user
     * @param location the work location of the user
     * @param administrator whether the user is an administrator or not
     */
    public User(String userID, String name, String gmail, String location, boolean active, boolean administrator) {
        this.userID = userID;
        this.name = name;
        this.gmail = gmail;
        this.location = location;
        this.active = active;
        this.administrator = administrator;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getgmail() {
        return gmail;
    }

    public String getLocation() {
        return location;
    }

    public boolean getAdministrator() {
        return administrator;
    }

    public boolean getActive() {
        return active;
    }

}
