package com.project.app.quickquery.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh()
    {
        String recent_token = FirebaseInstanceId.getInstance().getToken();

        //Store this token into the firebase database inside notification node.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference("notifications");
        if (auth.getCurrentUser() != null)
        {
            String userId = auth.getCurrentUser().getUid();
            //send it to the firebase database with the current userId
            notificationReference.child(userId).child("auth_token").setValue(recent_token);
        }


    }
}
