package com.saxionact.ehi2vsd3.takeaway.models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The project class contains all information necessary for the project.
 *
 * @author Vincent Witten
 */
public class Project {

    //project attributes
    private String projectID;
    private String name;
    private boolean done;
    private HashMap<String, String> users;

    public Project(){}

    /**
     * Project constructor.
     *
     * @author Vincent Witten
     *
     * @param projectID a unique id to identify the project with
     * @param name the name for the project
     * @param done true if the project is finished
     * @param users A list of userID's of users who are working on the project
     */
    public Project(String projectID, String name, boolean done, HashMap<String, String> users) {
        this.projectID = projectID;
        this.name = name;
        this.done = done;
        this.users = users;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getName() {
        return name;
    }

    public boolean getDone() {
        return done;
    }

    public HashMap<String, String> getUsers() {
        return users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setUsers(HashMap<String, String> users) {
        this.users = users;
    }
}
