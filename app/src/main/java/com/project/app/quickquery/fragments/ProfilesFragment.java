package com.project.app.quickquery.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.*;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.activities.LoginActivity;
import com.project.app.quickquery.R;
import com.project.app.quickquery.dialogs.EditPhoneNoDialog;
import com.project.app.quickquery.dialogs.EditUserNameDialog;
import com.project.app.quickquery.models.LocationModel;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.utils.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfilesFragment extends Fragment {

    private String[] gender = {"Male", "Female"};
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseUsers;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView locationTextView1, locationTextView2, locationTextView3;
    private TextView userNameTextView, phoneNumberTextView, birthdayTextView;
    private Spinner genderSpinner;
    private CircleImageView profileImageView;

    int PLACE_PICKER_REQUEST1 = 1;
    int PLACE_PICKER_REQUEST2 = 2;
    int PLACE_PICKER_REQUEST3 = 3;
    private final static int RESULT_OK = -1;
    private ArrayList<LocationModel> loc_list = new ArrayList<>();
    private LocationModel locationModel1 = null, locationModel2 = null, locationModel3 = null;
    private String userid;
    private UserModel userModel;
    private int PICK_IMAGE_REQUEST = 101;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = rootView.findViewById(R.id.profile_image);
        Button add_editBtn1 = rootView.findViewById(R.id.add_edit_button1);
        Button add_editBtn2 = rootView.findViewById(R.id.add_edit_button2);
        Button add_editBtn3 = rootView.findViewById(R.id.add_edit_button3);

        locationTextView1 = rootView.findViewById(R.id.location_textview1);
        locationTextView2 = rootView.findViewById(R.id.location_textview2);
        locationTextView3 = rootView.findViewById(R.id.location_textview3);

        userNameTextView = rootView.findViewById(R.id.username_textview);
        phoneNumberTextView = rootView.findViewById(R.id.txt_profile_phone_number);
        Button saveProfileButton = rootView.findViewById(R.id.btn_save_profile);
        Button signOutButton = rootView.findViewById(R.id.btn_sign_out);
        genderSpinner = rootView.findViewById(R.id.genderspinner);
        Button btnMobileedit = rootView.findViewById(R.id.btnMobileedit);

        Button editUserNameButton = rootView.findViewById(R.id.edit_username_button);

        auth = FirebaseAuth.getInstance();
        userid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        final Calendar myCalendar = Calendar.getInstance();
        ImageView calendarImageView = rootView.findViewById(R.id.calendar_imageview);
        birthdayTextView = rootView.findViewById(R.id.Birthday);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), gso);

        //Save The User Profile
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate all the properties.
                String selectedGender = genderSpinner.getSelectedItem().toString();
                String selectedBirthday = birthdayTextView.getText().toString();
                String userName = userNameTextView.getText().toString();
                String phoneNumber = phoneNumberTextView.getText().toString();
                int votes = userModel.getVotes();
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
                    Toast.makeText(getContext(), "Minimum 1 Location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userName.length() == 0) {
                    Toast.makeText(getContext(), "UserName Cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (auth.getCurrentUser() != null) {
//                    Toast.makeText(ProfileActivity.this, "Gender: "+selectedGender+" Age "+selectedAge+" Phone Number "+phone, Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    if (Constants.token.equals("")) {
                        Constants.token = FirebaseInstanceId.getInstance().getToken();
                    }
                    UserModel user = new UserModel(userName, phoneNumber, selectedGender, selectedBirthday, votes, loc_list, Constants.token);
                    mDatabaseUsers.child(userid).setValue(user);
                    ApplicationController.getInstance().setUserModel(user);
                }
            }
        });

        //Gender Spinner
        ArrayAdapter<String> adapter_state = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, gender);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter_state);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                genderSpinner.setSelection(position);
                //String selState = (String) genderSpinner.getSelectedItem();
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
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST1);
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
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST2);
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
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST3);
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
                updateLabel(birthdayTextView, myCalendar);
            }

        };

        calendarImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //end of Age Calender


        //Sign out
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();

            }
        });

        editUserNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditUserNameDialog dialog = new EditUserNameDialog(getActivity(), userModel.getUserName());
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();
                final EditText userNameEditText = dialog.findViewById(R.id.username_edittext);
                Button saveButton = dialog.findViewById(R.id.save_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userNameTextView.setText(userNameEditText.getText().toString());
                        dialog.dismiss();
                    }
                });
            }
        });

        btnMobileedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditPhoneNoDialog dialog = new EditPhoneNoDialog(getActivity(), userModel.getPhoneNumber());
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();
                final EditText userNameEditText = dialog.findViewById(R.id.username_edittext);
                Button saveButton = dialog.findViewById(R.id.save_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (userNameEditText.getText().toString().length() == 0) {
                            Toast.makeText(getContext(),
                                    "Enter New Mobile No!!", Toast.LENGTH_SHORT).show();
                        }  else {
                            phoneNumberTextView.setText(userNameEditText.getText().toString());
                            Toast.makeText(getContext(),
                                    "Mobile No Change Successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                });


            }


        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();

            }
        });

        downloadImage();
        return rootView;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void downloadImage() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://quickqueryapp-bc9b1.appspot.com");

        storageRef.child("images/" + userid).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                profileImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, profileImageView.getWidth(),
                        profileImageView.getHeight(), false));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
//                Toast.makeText(getContext(), exception.toString() + "!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void uploadImage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + userid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
//                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
//                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mDatabaseUsers.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userModel = dataSnapshot.getValue(UserModel.class);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUI() {
        //Update the UI Accordingly.
        if (userModel != null) {
            userNameTextView.setText(userModel.getUserName());
            birthdayTextView.setText(userModel.getBirthDate());
            phoneNumberTextView.setText(userModel.getPhoneNumber());
            for (int i = 0; i < userModel.getLocations().size(); i++) {
                if (i == 0) {
                    locationModel1 = userModel.getLocations().get(0);
                    locationTextView1.setText(userModel.getLocations().get(0).getLocation());
                }
                if (i == 1) {
                    locationModel2 = userModel.getLocations().get(1);
                    locationTextView2.setText(userModel.getLocations().get(1).getLocation());
                }
                if (i == 2) {
                    locationModel3 = userModel.getLocations().get(2);
                    locationTextView3.setText(userModel.getLocations().get(2).getLocation());
                }
            }
            //Gender Spinner
            ArrayAdapter<String> adapter_state = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                    android.R.layout.simple_spinner_item, gender);
            adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(adapter_state);
            genderSpinner.setSelection(adapter_state.getPosition(userModel.gender));
        }

    }

    private void signOut() {
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(Objects.requireNonNull(getActivity()),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().overridePendingTransition(R.anim.animation_enter_left, R.anim.animation_leave_right);
                            getActivity().finish();
                        }
                    }
                });
        auth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().overridePendingTransition(R.anim.animation_enter_left, R.anim.animation_leave_right);
        getActivity().finish();

    }

    //This method will update the Birthdate
    private void updateLabel(TextView textView, Calendar calendar) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if(calendar.getTime().before(Calendar.getInstance().getTime())){
            Toast.makeText(getContext(),"Please Select Date Of Birth Properly" ,Toast.LENGTH_LONG).show();
        }else {
            textView.setText(sdf.format(calendar.getTime()));
        }



    }

    //This Activity will fire when user will select Location
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //User can only select maximum 3 locations
        if (requestCode == PLACE_PICKER_REQUEST1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(Objects.requireNonNull(getActivity()), data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel1 = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                locationTextView1.setText(toastMsg);
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST2) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(Objects.requireNonNull(getActivity()), data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel2 = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                locationTextView2.setText(toastMsg);
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST3) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(Objects.requireNonNull(getActivity()), data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel3 = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                locationTextView3.setText(toastMsg);
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profileImageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
