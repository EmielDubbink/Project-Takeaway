package com.saxionact.ehi2vsd3.takeaway.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.User;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.utils.Validate;

import static com.saxionact.ehi2vsd3.takeaway.utils.Validate.validate;
import static com.saxionact.ehi2vsd3.takeaway.utils.Validate.validateEmail;
import static com.saxionact.ehi2vsd3.takeaway.utils.Validate.validateFullName;

/**
 * This activity creates a new user for the app using the input from an administrator.
 *
 * @author Vincent Witten
 * @author Boris Oortwijn
 */
public class CreateUserActivity extends AppCompatActivity {

    // View Containers
    private ImageView ivSettings;
    private EditText etName;
    private TextView tvName;
    private EditText etGmail;
    private TextView tvGmail;
    private EditText etLocation;
    private TextView tvLocation;
    private EditText etIsActivePlaceHolder;
    private EditText etIsAdminPlaceHolder;
    private Switch swActive;
    private Switch swAdmin;
    private Button btnDone;
    private Button btnDelete;

    /**
     * Contains the main logic for this Activity.
     *
     * @param savedInstanceState contains the savedInstanceState of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        // Finding attributes from activity_create_user.xml
        ivSettings = findViewById(R.id.ivSettings);
        etName = findViewById(R.id.etName);
        tvName = findViewById(R.id.tvName);
        etGmail = findViewById(R.id.etGmail);
        tvGmail = findViewById(R.id.tvGmail);
        etLocation = findViewById(R.id.etLocation);
        tvLocation = findViewById(R.id.tvLocation);
        etIsActivePlaceHolder = findViewById(R.id.etIsActivePlaceHolder);
        etIsAdminPlaceHolder = findViewById(R.id.etIsAdminPlaceHolder);
        swActive = findViewById(R.id.swActive);
        swAdmin = findViewById(R.id.swAdmin);
        btnDone = findViewById(R.id.btnDone);
        btnDelete = findViewById(R.id.btnDelete);

        // When user is created, active will automatically be true
        swActive.setChecked(true);

        // Settings button onClickListener
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateUserActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Done onClickListener for saving the user
        btnDone.setOnClickListener(new View.OnClickListener() {
            /**
             * Creates a new user.
             * <p>
             * This onClick method takes the users input and validates the values.
             * If the validation has been validated as correct a new user will be
             * created and stored in the Firebase Realtime Database.
             * </p>
             *
             * @author Vincent Witten
             *
             * @param v contains the Button view Done.
             */
            @Override
            public void onClick(View v) {
                boolean name = validate(getBaseContext(), tvName, etName, Validate.FULLNAME, R.string.name_field_error_message);
                boolean gmail = validate(getBaseContext(), tvGmail, etGmail, Validate.EMAIL, R.string.gmail_field_error_message);
                boolean location = validate(getBaseContext(), tvLocation, etLocation, Validate.FULLNAME, R.string.location_field_error_message);

                if (name && gmail && location) {
                    boolean isActive = swActive.isChecked();
                    boolean isAdmin = swAdmin.isChecked();
                    User user = new User(Database.getUsersRef().push().getKey(), etName.getText().toString(),
                            etGmail.getText().toString(), etLocation.getText().toString(),isActive, isAdmin);
                    Database.getUsersRef().child(user.getUserID()).setValue(user);
                    Toast.makeText(CreateUserActivity.this, "User " + etName.getText().toString() + " has been created", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateUserActivity.this, R.string.invalidForm, Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // By pressing the EditText isActive you will toggle the IsActive switch.
        etIsActivePlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swActive.toggle();
            }
        });

        // By pressing the the EditText IsAdmin you will toggle the IsAdmin switch.
        etIsAdminPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swAdmin.toggle();
            }
        });
    }
}