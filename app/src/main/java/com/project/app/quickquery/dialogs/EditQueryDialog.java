package com.project.app.quickquery.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.app.quickquery.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class EditQueryDialog extends Dialog {
    private EditText titleEditText, contentEditText;

    private DatabaseReference mDatabaseQueries;

    EditQueryDialog(Activity activity, String title, String query, String query_key) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_query);

        titleEditText = findViewById(R.id.title_edittext);
        titleEditText.setText(title);

        contentEditText = findViewById(R.id.query_content_edittext);
        contentEditText.setText(query);

        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries").child(query_key);
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(titleEditText.getText()))
                {
                    Toast.makeText(getContext(),"Query title is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(contentEditText.getText()))
                {
                    Toast.makeText(getContext(),"Query description is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                mDatabaseQueries.child("title").setValue(titleEditText.getText().toString());
                mDatabaseQueries.child("query").setValue(contentEditText.getText().toString());
                Toast.makeText(getContext(), "Query updated successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        Button closeButton = findViewById(R.id.cancel_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}