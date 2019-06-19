package com.saxionact.ehi2vsd3.takeaway.databases;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The database class provides the necessary references to the database.
 *
 * @author Vincent Witten
 */
public class Database {

    //database instance
    private static Database database = null;

    //firebaseDatabase instance
    private static FirebaseDatabase firebaseDatabase;

    //Database references
    private static DatabaseReference usersRef;
    private static DatabaseReference projectsRef;
    private static DatabaseReference workTimeRef;

    /**
     * Database constructor.
     *
     * @author Vincent Witten
     */
    private Database(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        projectsRef = firebaseDatabase.getReference("projects");
        workTimeRef = firebaseDatabase.getReference("worktime");
    }

    /**
     * If no instance of the database exist one will be made.
     *
     * @author Vincent Witten
     */
    public static void setInstance(){
        if(database == null){
            database = new Database();
        }
    }

    public static DatabaseReference getUsersRef() {
        setInstance();
        return usersRef;
    }

    public static DatabaseReference getProjectsRef() {
        setInstance();
        return projectsRef;
    }

    public static DatabaseReference getWorkTimeRef() {
        setInstance();
        return workTimeRef;
    }
}