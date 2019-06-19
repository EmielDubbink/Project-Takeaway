package com.saxionact.ehi2vsd3.takeaway.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.User;
import com.saxionact.ehi2vsd3.takeaway.utils.Validate;

import static com.saxionact.ehi2vsd3.takeaway.utils.Validate.validate;

/**
 * EditUserActivity for editing user information and save the data.
 *
 * @author Vincent Witten
 * @author Emiel Dubbink
 */
public class EditUserActivity extends AppCompatActivity {

    //view containers
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

    String userId = null;

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

        // onClickListener for settings button, to create an new activity.
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditUserActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // onClickListener to save and validate the new / edited information.
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

                if (name && gmail && location && userId != null) {
                    boolean isActive = swActive.isChecked();
                    boolean isAdmin = swAdmin.isChecked();
                    User user = new User(userId, etName.getText().toString(),
                            etGmail.getText().toString(), etLocation.getText().toString(), isActive, isAdmin);
                    Database.getUsersRef().child(user.getUserID()).setValue(user);
                    Toast.makeText(EditUserActivity.this, "User " + etName.getText().toString() + " has been edited", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditUserActivity.this, R.string.invalidForm, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        etIsActivePlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swActive.toggle();
            }
        });

        // By pressing the the EditText IsActive you will toggle the IsActive switch.
        etIsAdminPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swAdmin.toggle();
            }
        });

        // Get information from calling activity.
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        etName.setText(intent.getStringExtra("name"));
        etGmail.setText(intent.getStringExtra("gmail"));
        etLocation.setText(intent.getStringExtra("location"));

        // Setting switches to value of active and administrator
        boolean active = intent.getBooleanExtra("active", false);
        swActive.setChecked(active);

        boolean administrator = intent.getBooleanExtra("administrator", false);
        swAdmin.setChecked(administrator);
    }
}
