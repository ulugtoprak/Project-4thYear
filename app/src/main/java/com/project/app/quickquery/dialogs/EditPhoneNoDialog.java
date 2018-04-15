package com.project.app.quickquery.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.project.app.quickquery.R;

public class EditPhoneNoDialog extends Dialog {

    public EditPhoneNoDialog(Activity activity, String username) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_mobile_no);

        EditText userEditText = findViewById(R.id.username_edittext);
        userEditText.setText(username);

        Button closeButton = findViewById(R.id.cancel_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}