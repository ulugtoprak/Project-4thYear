package com.project.app.quickquery.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatusBarTranslucent();

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        Constants.token = FirebaseInstanceId.getInstance().getToken();
        Log.e("token is ",Constants.token +"");
        // [END initialize_auth]

        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if getCurrentUser does not returns null
                if (auth.getCurrentUser() != null) {
                    mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
                    final String userId = auth.getCurrentUser().getUid();
                    progressDialog = new ProgressDialog(SplashActivity.this);
                    progressDialog.setMessage("Loading Data...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mDatabaseUsers.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                if(userModel.token ==null) {
                                    userModel.token=Constants.token;
                                    mDatabaseUsers.child(userId).setValue(userModel);
                                    ApplicationController.getInstance().setUserModel(userModel);
                                }
                                ApplicationController.getInstance().setUserModel(userModel);
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                                finish();
                            } else {
                                Intent mainIntent = new Intent(SplashActivity.this, ProfileActivity.class);
                                startActivity(mainIntent);
                                overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    /* Create an Intent that will start the Login-Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.animation_fade_in, R.anim.animation_fade_out);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    protected void setStatusBarTranslucent() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
}
