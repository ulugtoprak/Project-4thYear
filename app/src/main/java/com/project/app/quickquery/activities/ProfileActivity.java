package com.project.app.quickquery.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.models.LocationModel;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.utils.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private String[] gender = {"Male", "Female"};
    int PLACE_PICKER_REQUEST1 = 1;
    int PLACE_PICKER_REQUEST2 = 2;
    int PLACE_PICKER_REQUEST3 = 3;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseUsers;
    private ArrayList<LocationModel> loc_list = new ArrayList<>();
    private LocationModel locationModel1 = null, locationModel2 = null, locationModel3 = null;
    private TextView locationTextView1, locationTextView2, locationTextView3;
    private EditText userNameEditText;
    private EditText phoneNumberEditText;
    private Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button add_editBtn1 = findViewById(R.id.add_edit_button1);
        Button add_editBtn2 = findViewById(R.id.add_edit_button2);
        Button add_editBtn3 = findViewById(R.id.add_edit_button3);

        locationTextView1 = findViewById(R.id.location_textview1);
        locationTextView2 = findViewById(R.id.location_textview2);
        locationTextView3 = findViewById(R.id.location_textview3);

        userNameEditText = findViewById(R.id.username_edittext);
        phoneNumberEditText = findViewById(R.id.txt_profile_phone_number);
        Button save_profile;
        save_profile = findViewById(R.id.btn_save_profile);
        genderSpinner = findViewById(R.id.genderspinner);

        auth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        final Calendar myCalendar = Calendar.getInstance();
        final EditText birthDayEditText = findViewById(R.id.Birthday);
        final Button signOut = findViewById(R.id.btn_sign_out);

        // [START config_signIn]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signIn]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Save The User Profile
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate all the properties.
                String selectedGender = genderSpinner.getSelectedItem().toString();
                String selectedBirthday = birthDayEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                loc_list.clear();
                if (locationModel1 != null) {
                    loc_list.add(locationModel1);
                }
                if (locationModel2 != null) {
                    loc_list.add(locationModel2);
                }
                if (locationModel3 != null) {
                    loc_list.add(locationModel3);
                }
                if (loc_list.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Minimum 1 Location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "UserName Cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (birthDayEditText.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Date of Birth can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (auth.getCurrentUser() != null) {
                    String userId = auth.getCurrentUser().getUid();
//                    Toast.makeText(ProfileActivity.this, "Gender: "+selectedGender+" Age "+selectedAge+" Phone Number "+phone, Toast.LENGTH_LONG).show();

                    if (Constants.token == null) {
                        Constants.token = FirebaseInstanceId.getInstance().getToken();
                    }
                    UserModel user = new UserModel(userName, phoneNumber, selectedGender, selectedBirthday, 0, loc_list, Constants.token);
                    mDatabaseUsers.child(userId).setValue(user);
                    ApplicationController.getInstance().setUserModel(user);

                    //goto the main swipe Activity
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                    finish();
                }
            }
        });

        //Gender Spinner
        ArrayAdapter<String> adapter_state = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, gender);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter_state);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                genderSpinner.setSelection(position);
                //String selState = (String) genderSpinner.getSelectedItem();
                //Toast.makeText(ProfileActivity.this, "Your Gender:" + selState, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //End of Gender Spinner

        //Place Picker Button
        add_editBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(ProfileActivity.this), PLACE_PICKER_REQUEST1);
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }

            }
        });

        add_editBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(ProfileActivity.this), PLACE_PICKER_REQUEST2);
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }

            }
        });

        add_editBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(ProfileActivity.this), PLACE_PICKER_REQUEST3);
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }

            }
        });
        //End of place picker button

        //Set Age Calender
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(birthDayEditText, myCalendar);
            }

        };

        birthDayEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //end of Age Calender

        //Sign out
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();

            }
        });
    }

    private void signOut() {
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
        auth.signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }

    //This method will updates the date of birth
    private void updateLabel(TextView textView, Calendar calendar) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if(calendar.getTime().after(Calendar.getInstance().getTime())){
            Toast.makeText(this,"Please Select Date Of Birth Properly" ,Toast.LENGTH_LONG).show();
        }else {
            textView.setText(sdf.format(calendar.getTime()));
        }

    }
    //This Activity will fire when user will select Location
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel1 = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                locationTextView1.setText(toastMsg);
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST2) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel2 = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                locationTextView2.setText(toastMsg);
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST3) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel3 = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                locationTextView3.setText(toastMsg);
            }
        }
    }
}
