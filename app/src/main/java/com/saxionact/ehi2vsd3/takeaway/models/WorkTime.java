package com.saxionact.ehi2vsd3.takeaway.models;

import java.util.Date;

/**
 * The WorkTime class contains all information necessary to keep track of the time that has been spent for a project on a specific day.
 *
 * @author Vincent Witten
 */
public class WorkTime {

    //workTime attributes
    private String worktimeID;
    private String projectID;
    private String userID;
    private String description;
    private int time; // in minutes rounded by 5
    private long date;
    private long beginDate;
    private long endDate;

    public WorkTime(){}

    /**
     * WorkTime constructor.
     *
     * @author Vincent Witten
     *
     * @param worktimeID a unique id to identify the workTime with
     * @param projectID a unique id to identify the project that has been worked on
     * @param userID a unique id to identify the user that made these hours
     * @param description the description of activity done during work
     * @param time the amount of time in minutes thet has been worked
     * @param date the date on which the hours have been worked
     */
    public WorkTime(String worktimeID, String projectID, String userID, String description, int time, long date, long beginDate, long endDate) {
        this.worktimeID = worktimeID;
        this.projectID = projectID;
        this.userID = userID;
        this.description = description;
        this.time = time;
        this.date = date;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public String getWorktimeID() {
        return worktimeID;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getUserID() {
        return userID;
    }

    public String getDescription() {
        return description;
    }

    public int getTime() {
        return time;
    }

    public long getDate() {
        return date;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public long getEndDate() {
        return endDate;
    }
}
