package com.project.app.quickquery.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.fragments.AnswersFragment;
import com.project.app.quickquery.fragments.PlaceQueryFragment;
import com.project.app.quickquery.fragments.ProfilesFragment;
import com.project.app.quickquery.fragments.QueriesFragment;
import com.project.app.quickquery.models.LocationModel;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private boolean isNotSend = false;
    private List<String> tokenList;
    private boolean isQuerySend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference("com/example/project/quickquery/notifications");
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d("Auth Token", recent_token);
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            //send it to the firebase database with the current userId
            notificationReference.child(userId).child("auth_token").setValue(recent_token);
        }
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Queries");
        // Create the adapter that will return a fragment for each of the four
        // primary sections of the activity.

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.parseColor("#90FFFFFF"), Color.parseColor("#FFFFFF"));
        tabLayout.setTabMode(TabLayout.GRAVITY_FILL);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                toolbar.setTitle(mSectionsPagerAdapter.getPageTitle(position));
                //update the action bar
                setSupportActionBar(toolbar);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //Placeholder fragment deleted form here.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new QueriesFragment();
                case 1:
                    return new AnswersFragment();
                case 2:
                    return new PlaceQueryFragment();
                case 3:
                    return new ProfilesFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Queries";
                case 1:
                    return "My Activity";
                case 2:
                    return "Place Query";
                case 3:
                    return "Profile";
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void sendMessage(final JSONArray recipients, final String title, final String body, final String icon, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);

                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

//            @Override
//            protected void onPostExecute(String result) {
//                try {
//                    JSONObject resultJson = new JSONObject(result);
//                    int success, failure;
//                    success = resultJson.getInt("success");
//                    failure = resultJson.getInt("failure");
////                    Toast.makeText(MainActivity.this, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
////                    Toast.makeText(MainActivity.this, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
//                }
//            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        OkHttpClient mClient = new OkHttpClient();

        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "AIzaSyAGyYUVmSDcyflSuQKuOcr0igBdvDyxRss")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    @SuppressWarnings("unchecked")
    public void getUsers(final String title, final String message, final LocationModel locationModel) {
        isQuerySend = true;
        tokenList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(Constants.token);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("message", message + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendMessage(jsonArray, ApplicationController.getInstance().getUserModel().userName + "", "", "Http:\\google.com", jsonObject.toString());
        tokenList.add(Constants.token);

        DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {

                    if (!Objects.requireNonNull(auth.getCurrentUser()).getUid().equals(user.getKey())) {
                        Map<String, UserModel> map = (Map<String, UserModel>) user.getValue();
                        String token = String.valueOf(Objects.requireNonNull(map).get("token"));

                        List<LocationModel> location = (List<LocationModel>) map.get("locations");
                        for (int i = 0; i < location.size(); i++) {

                            Map<String, List<LocationModel>> locations = (Map<String, List<LocationModel>>) location.get(i);
                            Double lat = Double.parseDouble(String.valueOf(locations.get("lat")));
                            Double lng = Double.parseDouble(String.valueOf(locations.get("lon")));
                            if (distance(lat,
                                    lng,
                                    locationModel.lat,
                                    locationModel.lon) < 100) {
                                Log.e("Between ", "criteria");
                                JSONArray jsonArray = new JSONArray();

                                if (token != null && !token.equals("null")) {
                                    jsonArray.put(token);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("title", title);
                                        jsonObject.put("message", message + "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!tokenList.contains(token)) {
                                        tokenList.add(token);
                                        if (isQuerySend) {
                                            sendMessage(jsonArray, ApplicationController.getInstance().getUserModel().userName + "", "", "Http:\\google.com", jsonObject.toString());
                                        }

                                    }
                                } else {
                                    jsonArray.put(Constants.token);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("title", ApplicationController.getInstance().getUserModel().userName + " send New Query");
                                        jsonObject.put("message", message + "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!isNotSend) {

                                        if (!tokenList.contains(token)) {
                                            tokenList.add(token);
                                            if (isQuerySend) {
                                                sendMessage(jsonArray, ApplicationController.getInstance().getUserModel().userName + "", "", "Http:\\google.com", jsonObject.toString());
                                            }
                                        }
                                        isNotSend = true;
                                    }
                                }
                            }
                        }
                    }
                }
                isQuerySend = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
