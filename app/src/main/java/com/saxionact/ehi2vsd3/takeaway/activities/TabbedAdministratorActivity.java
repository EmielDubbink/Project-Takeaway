package com.saxionact.ehi2vsd3.takeaway.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.adapters.ProjectListAdapter;
import com.saxionact.ehi2vsd3.takeaway.adapters.UserOverviewAdapter;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.Project;
import com.saxionact.ehi2vsd3.takeaway.models.User;
import com.saxionact.ehi2vsd3.takeaway.models.WorkTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TabbedAdministratorActivity extends AppCompatActivity {

    private String userId;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * This activity is made for administrators, and contains two tabs, one with the project overview, and one with the user overview.
     * It allows administrators to view and edit users and projects.
     *
     * @author Emiel Dubbink
     * @param savedInstanceState
     */

    public static final String CHANNEL_ID = "2";
    public static final CharSequence CHANNEL_NAME = "Channel notification";
    public static final String CHANNEL_DESCRIPTION = "Channel voor takeaway app";
    public static final String TAG = "TabbedAdministratorAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_administrator);

        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // this creates a channel for application notifications
        createNotificationChannel();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ImageView ivSettings = findViewById(R.id.ivSettings);



        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TabbedAdministratorActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }



    /**
     * A fragment containing the user overview
     */
    public static class UserOverviewFragment extends Fragment {

        // Defining variables and view containers
        private ListView lvFragment;
        private UserOverviewAdapter adapter;
        private UserOverviewAdapter adapterSearch;
      
        private List<User> users;
        private List<User> usersSearch;
        private FloatingActionButton fabAddUser;
        private SearchView searchView;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public UserOverviewFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static UserOverviewFragment newInstance(int sectionNumber) {
            UserOverviewFragment fragment = new UserOverviewFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Finding the attributes from fragment_user_overview.xml
            View rootView = inflater.inflate(R.layout.fragment_user_overview, container, false);
            lvFragment = rootView.findViewById(R.id.lvFragment);
            fabAddUser = rootView.findViewById(R.id.fabFragment);
            searchView = rootView.findViewById(R.id.searchView);
            searchView.setFocusable(false);
            users = new ArrayList<>();
            adapter = new UserOverviewAdapter(getContext(), users);
            adapterSearch = new UserOverviewAdapter(getContext(), users);
            usersSearch = new ArrayList<>();

            lvFragment.setAdapter(adapter);

            // ValueEventListener for refreshing the ArrayList of users when data in the database
            // has been edited or created.
            Database.getUsersRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    users.clear();
                    ArrayList<User> nonActiveUsers = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User tmp = snapshot.getValue(User.class);

                        if (tmp.getActive()) {
                            users.add(tmp);
                        } else {
                            nonActiveUsers.add(tmp);
                        }
                    }

                    users.addAll(nonActiveUsers);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });

            // Start activity to the CreateUserActivity to create an new user
            fabAddUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CreateUserActivity.class);
                    startActivity(intent);
                }
            });

            // Start activity to the HoursOverviewActivity from the selected user
            lvFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), HoursOverviewActivity.class);
                    intent.putExtra("userId", users.get(i).getUserID());

                    startActivity(intent);
                }
            });

            lvFragment.setLongClickable(true);
            lvFragment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                /**
                 * De onItemLongClickListener wordt aangeroepen wanneer een item in de lijst langdurig ingehouden wordt.
                 * Een dialog wordt geopend met 2 knoppen; modify & delete
                 * @param adapterView
                 * @param view de view van het item
                 * @param i de index van het item waarop geklikt is
                 * @param l
                 * @return
                 */
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    // Popup
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View view1 = getLayoutInflater().inflate(R.layout.dialog_list_buttons, null);

                    builder.setView(view1);
                    final AlertDialog dialog = builder.create();

                    // Modify button and onClickListener to modify the user
                    Button modifyButton = view1.findViewById(R.id.btnModify);
                    modifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), EditUserActivity.class);
                            intent.putExtra("userId", users.get(i).getUserID());
                            intent.putExtra("name", users.get(i).getName());
                            intent.putExtra("gmail", users.get(i).getgmail());
                            intent.putExtra("location", users.get(i).getLocation());
                            intent.putExtra("active", users.get(i).getActive());
                            intent.putExtra("administrator", users.get(i).getAdministrator());

                            startActivity(intent);

                            dialog.dismiss();
                        }
                    });

                    // Delete button and onClickListener to delete the user
                    Button deleteButton = view1.findViewById(R.id.btnDelete);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            User user = users.get(i);

                            Query deleteQuery = Database.getUsersRef().orderByChild("userID").equalTo(user.getUserID());

                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                }
                            });
                        }
                    });

                    dialog.show();
                    return true;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if(searchView.getQuery().equals("")){
                        lvFragment.setAdapter(adapter);
                        return false;
                    }

                    usersSearch.clear();
                    for (User user : users){
                        if(user.getName().toLowerCase().contains(searchView.getQuery().toString().toLowerCase())){
                            usersSearch.add(user);
                        }
                    }
                    lvFragment.setAdapter(adapterSearch);
                    adapterSearch = new UserOverviewAdapter(getContext(), usersSearch);
                    return false;
                }
            });

            return rootView;
        }
    }

    public static class ProjectOverviewFragment extends Fragment {

        private ListView lvProjects;
        private ProjectListAdapter plAdapter;
        private List<Project> projects;
        private FloatingActionButton fabAddProject;


        private String userID;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ProjectOverviewFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectOverviewFragment newInstance(int sectionNumber) {
            ProjectOverviewFragment fragment = new ProjectOverviewFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_overview, container, false);
            lvProjects = rootView.findViewById(R.id.lvFragment);


            fabAddProject = rootView.findViewById(R.id.fabFragment);

            Intent intent = getActivity().getIntent();
            userID = intent.getStringExtra("USER_ID");

            projects = new ArrayList<>();
            plAdapter = new ProjectListAdapter(getContext(), projects, userID);
            lvProjects.setAdapter(plAdapter);

            lvProjects.setLongClickable(true);

            Database.getProjectsRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    projects.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        projects.add(snapshot.getValue(Project.class));
                    }

                    plAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });

            // onItemClick to start activity to the HoursOverviewActivity
            lvProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent projectInfo = new Intent(getActivity(), HoursOverviewActivity.class);
                    String project_id = projects.get(i).getProjectID();
                    projectInfo.putExtra("PROJECT_ID", project_id);
                    projectInfo.putExtra("USER_ID", userID);
                    projectInfo.putExtra("ADMIN", true);

                    startActivity(projectInfo);
                }
            });

            // onItemLongClickListener to start an activity to the ProjectActivity
            lvProjects.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent projectInfo = new Intent(getActivity(), ProjectActivity.class);
                    String project_id = projects.get(i).getProjectID();
                    projectInfo.putExtra("PROJECT_ID", project_id);
                    startActivity(projectInfo);

                    return true;
                }
            });

            // onClickListener to add an project
            fabAddProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newProject = new Intent(getActivity(), AddProjectActivity.class);
                    startActivity(newProject);
                }
            });

            return rootView;
        }

        // onResume that refreshes the project list
        @Override
        public void onResume() {
            super.onResume();

            Database.getProjectsRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    projects.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        projects.add(snapshot.getValue(Project.class));
                    }

                    plAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // At position 0, the left tab, we want the project overview, and on position 1 we want the user overview.
            if(position == 0){
                return ProjectOverviewFragment.newInstance(position + 1);
            } else {
                return UserOverviewFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    // Before you can deliver the notification on Android 8.0 and higher,
    // you must register your app's notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
