package com.project.app.quickquery.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.utils.Validation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailText, passwordText;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.project.app.quickquery.R.layout.activity_login);
        setStatusBarTranslucent();

        Button loginBtn = findViewById(R.id.btn_login);
        emailText = findViewById(R.id.txt_login_email);
        passwordText = findViewById(R.id.txt_login_password);
        TextView signUpBtn = findViewById(R.id.btn_signup);
        TextView forgotPasswordBtn = findViewById(R.id.btn_forgot_password);
        SignInButton googleLoginBtn = findViewById(R.id.google_sign_in);

        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        forgotPasswordBtn.setOnClickListener(this);
        googleLoginBtn.setOnClickListener(this);

        setGooglePlusButtonText(googleLoginBtn);
        // [START config_signIn]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signIn]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_forgot_password) {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
            finish();
        } else if (view.getId() == R.id.btn_signup) {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
            finish();
        } else if (view.getId() == R.id.btn_login) {
            loginUser(emailText.getText().toString(), passwordText.getText().toString());
        }
        else if (view.getId() == R.id.google_sign_in) {
            googleSignIn();
        }
    }

    @SuppressLint("SetTextI18n")
    protected void setGooglePlusButtonText(SignInButton signInButton) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("Sign in using Google account");
                return;
            }
        }
    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser !=null)
//        {
//            // Start the ProfileActivity
//        }
    }
    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]
    // [END on_start_check_user]


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //redirect the user to the profile page
                            moveToProfileActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.activity_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void moveToProfileActivity()  {
        DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        mDatabaseUsers.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel != null) {
                    ApplicationController.getInstance().setUserModel(userModel);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                    finish();
                } else {
                    Intent mainIntent = new Intent(LoginActivity.this, ProfileActivity.class);
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
    }
    // [END auth_with_google]

    // [START signIn]
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private void googleSignIn() {

        if (!haveNetworkConnection()) {
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        } else {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("LogIn...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }
    // [END signIn]

    private void loginUser(String email, final String password) {
        //validate email and password.
        if (Validation.isValidEmailAddress(email)) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("LogIn...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                //redirect the user to the main activity
                                moveToProfileActivity();
                                progressDialog.dismiss();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
        }

    }

    protected void setStatusBarTranslucent() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
}
