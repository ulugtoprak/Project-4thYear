package com.project.app.quickquery.Fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.project.app.quickquery.R;
import com.project.app.quickquery.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        Map<String, String> data = remoteMessage.getData();

        Log.e("data ", data.toString());
        try {
            JSONObject jsonObject = new JSONObject(data.get("message"));
            sendNotification(jsonObject.getString("title"), jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void sendNotification(String title, String message) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.login_avtar);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentInfo("")
                .setLargeIcon(icon)
                .setColor(Color.GRAY)
                .setSmallIcon(R.drawable.login_avtar);

        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setLights(Color.YELLOW, 1000, 300);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }


}