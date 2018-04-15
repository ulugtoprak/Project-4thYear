package com.project.app.quickquery.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.app.quickquery.R;
import com.project.app.quickquery.utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailEditText;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        TextView btnSignIn = findViewById(R.id.btn_signin);
        Button btnResetPassword;
        btnResetPassword = findViewById(R.id.btn_reset_password);
        emailEditText = findViewById(R.id.txt_reset_textbox);

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(emailEditText.getText()))
                {
                    Toast.makeText(getApplicationContext(),"Email is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Validation.isValidEmailAddress(emailEditText.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Enter valid email address",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                progressDialog.setMessage("Email Sending...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                auth.sendPasswordResetEmail(emailEditText.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"Email sent",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                overridePendingTransition(R.anim.animation_enter_left, R.anim.animation_leave_right);
                finish();
            }
        });
    }
}
