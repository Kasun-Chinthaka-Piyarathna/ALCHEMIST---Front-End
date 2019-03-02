package alchemist.fit.uom.alchemists.activities;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.app.PendingIntent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alchemist.fit.uom.alchemists.ActivitiesAdapter;
import alchemist.fit.uom.alchemists.ActivityIntentService;
import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.Utility;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import alchemist.fit.uom.alchemists.fragments.NewsFeedFragment;

public class DetectContextActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context mContext;
    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
//Define an ActivityRecognitionClient//

    private ActivityRecognitionClient mActivityRecognitionClient;
    private ActivitiesAdapter mAdapter;
    private Switch switch_1, switch_2, switch_3, switch_4;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress, sharedPreContext, battery_level;
    private String[] retrievedData;
    private String[] retrievedBehaviourData;
    private int userAge;
    private String userGender;
    private String startTime;
    private String[] retrievedBehaviouralData;
    private String context_identification, user_history_identification,
            user_behaviour_identification, simulation;
    private String newsFeedTime, notificationTime, profileTime, videoFreq, imageFreq, muteFreq, unMuteFreq;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private Dialog popupDialog;
    private String preference_tab,sound_preference,upload_preference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
        sharedPreContext = prefs.getString("user_context", "No name defined");
        battery_level = prefs.getString("battery_level", "No name defined");

        preference_tab = prefs.getString("preference_tab", "No name defined");
        sound_preference = prefs.getString("sound_preference", "No name defined");
        upload_preference = prefs.getString("upload_preference", "No name defined");

        retrievedBehaviouralData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);

        if (retrievedBehaviouralData != null) {
            if (!retrievedBehaviouralData[0].equals("NO EXIST")) {
                if (retrievedBehaviouralData[6] != null) {
                    context_identification = retrievedBehaviouralData[6];
                }
                if (retrievedBehaviouralData[7] != null) {
                    user_history_identification = retrievedBehaviouralData[7];
                }
                if (retrievedBehaviouralData[8] != null) {
                    user_behaviour_identification = retrievedBehaviouralData[8];
                }

                if (retrievedBehaviouralData[0] != null) {
                    newsFeedTime = retrievedBehaviouralData[0];
                }
                if (retrievedBehaviouralData[1] != null) {
                    notificationTime = retrievedBehaviouralData[1];
                }
                if (retrievedBehaviouralData[2] != null) {
                    profileTime = retrievedBehaviouralData[2];
                }
                if (retrievedBehaviouralData[3] != null) {
                    imageFreq = retrievedBehaviouralData[3];
                }
                if (retrievedBehaviouralData[4] != null) {
                    videoFreq = retrievedBehaviouralData[4];
                }
                if (retrievedBehaviouralData[9] != null) {
                    muteFreq = retrievedBehaviouralData[9];
                }
                if (retrievedBehaviouralData[10] != null) {
                    unMuteFreq = retrievedBehaviouralData[10];
                }
                if (retrievedBehaviouralData[11] != null) {
                    simulation = retrievedBehaviouralData[11];
                }
            }
        }


        if (user_history_identification.equals("on") || simulation.equals("on")) {
            updateTheme();
        }

        setContentView(R.layout.activity_detect_context);

        TextView t1 = findViewById(R.id.frequency_1);
        TextView t2 = findViewById(R.id.frequency_2);
        TextView t3 = findViewById(R.id.frequency_3);
        TextView t4 = findViewById(R.id.frequency_4);
        TextView t5 = findViewById(R.id.frequency_5);
        TextView t6 = findViewById(R.id.frequency_6);
        TextView t7 = findViewById(R.id.frequency_7);

        Long freq1;
        try {
            freq1 = (Long.parseLong(newsFeedTime) * 100) /
                    (Long.parseLong(notificationTime) + Long.parseLong(newsFeedTime) + Long.parseLong(profileTime));

        } catch (Exception e) {
            freq1 = Long.valueOf(0);
        }
        Long freq2;
        try {
            freq2 = (Long.parseLong(notificationTime) * 100) /
                    (Long.parseLong(notificationTime) + Long.parseLong(newsFeedTime) + Long.parseLong(profileTime));
        } catch (Exception e) {
            freq2 = Long.valueOf(0);
        }
        Long freq3;
        try {
            freq3 = (Long.parseLong(profileTime) * 100) /
                    (Long.parseLong(notificationTime) + Long.parseLong(newsFeedTime) + Long.parseLong(profileTime));
        } catch (Exception e) {
            freq3 = Long.valueOf(0);
        }
        Long freq4;
        try {
            freq4 = (Long.parseLong(imageFreq) * 100) /
                    (Long.parseLong(imageFreq) + Long.parseLong(videoFreq));
        } catch (Exception e) {
            freq4 = Long.valueOf(0);
        }
        Long freq5;
        try {
            freq5 = (Long.parseLong(videoFreq) * 100) /
                    (Long.parseLong(imageFreq) + Long.parseLong(videoFreq));
        } catch (Exception e) {
            freq5 = Long.valueOf(0);
        }


        Long freq6;
        try {
            freq6 = (Long.parseLong(muteFreq) * 100) /
                    (Long.parseLong(muteFreq) + Long.parseLong(unMuteFreq));
        } catch (Exception e) {
            freq6 = Long.valueOf(0);
        }

        Long freq7;
        try {
            freq7 = (Long.parseLong(unMuteFreq) * 100) /
                    (Long.parseLong(muteFreq) + Long.parseLong(unMuteFreq));
        } catch (Exception e) {
            freq7 = Long.valueOf(0);
        }


        t1.setText(String.valueOf(freq1) + " %");
        t2.setText(String.valueOf(freq2) + " %");
        t3.setText(String.valueOf(freq3) + " %");
        t4.setText(String.valueOf(freq4) + " %");
        t5.setText(String.valueOf(freq5) + " %");
        t6.setText(String.valueOf(freq6) + " %");
        t7.setText(String.valueOf(freq7) + " %");


        RadioButton cg_1 = findViewById(R.id.cg_1);
        RadioButton cg_2 = findViewById(R.id.cg_2);
        RadioButton cg_3 = findViewById(R.id.cg_3);
        RadioButton cg_4 = findViewById(R.id.cg_4);
        RadioButton cg_5 = findViewById(R.id.cg_5);
        RadioButton cg_6 = findViewById(R.id.cg_6);
        RadioButton cg_7 = findViewById(R.id.cg_7);

        switch (sharedPreContext) {
            case "still":
                cg_1.setChecked(true);
                break;
            case "walking":
                cg_2.setChecked(true);
                break;
            case "running":
                cg_3.setChecked(true);
                break;
            case "vehicle":
                cg_4.setChecked(true);
                break;
            case "bicycle":
                cg_5.setChecked(true);
                break;
            case "tilting":
                cg_6.setChecked(true);
                break;
            case "unknown":
                cg_7.setChecked(true);
                break;
            default:
                break;
        }
//        default
        RadioButton s_g_0 = findViewById(R.id.s_g_0);

//        age <= 19 M --> 1
        RadioButton s_g_1 = findViewById(R.id.s_g_1);
//        age <= 19 F --> 6
        RadioButton s_g_2 = findViewById(R.id.s_g_2);
//        age > 20 && age < 30 M --> 2
        RadioButton s_g_3 = findViewById(R.id.s_g_3);
//        age > 20 && age < 30 F --> 7
        RadioButton s_g_4 = findViewById(R.id.s_g_4);
//        age >= 30 && age < 40 M --> 3
        RadioButton s_g_5 = findViewById(R.id.s_g_5);
//        age >= 30 && age < 40 F --> 8
        RadioButton s_g_6 = findViewById(R.id.s_g_6);
//        age > 40 && age < 55 M --> 4
        RadioButton s_g_7 = findViewById(R.id.s_g_7);
//        age > 40 && age < 55 F --> 9
        RadioButton s_g_8 = findViewById(R.id.s_g_8);
//        age >= 55 M --> 5
        RadioButton s_g_9 = findViewById(R.id.s_g_9);
//        age >= 55 F --> 10
        RadioButton s_g_10 = findViewById(R.id.s_g_10);

        int theme_id = Utility.getTheme(getApplicationContext());
        switch (theme_id) {
            case -1:
                s_g_0.setChecked(true);
                break;
            case 1:
                s_g_1.setChecked(true);
                break;
            case 2:
                s_g_3.setChecked(true);
                break;
            case 3:
                s_g_5.setChecked(true);
                break;
            case 4:
                s_g_7.setChecked(true);
                break;
            case 5:
                s_g_9.setChecked(true);
                break;
            case 6:
                s_g_2.setChecked(true);
                break;
            case 7:
                s_g_4.setChecked(true);
                break;
            case 8:
                s_g_6.setChecked(true);
                break;
            case 9:
                s_g_8.setChecked(true);
                break;
            case 10:
                s_g_10.setChecked(true);
                break;
            case 11:
                s_g_1.setChecked(true);
                break;
            case 12:
                s_g_3.setChecked(true);
                break;
            case 13:
                s_g_5.setChecked(true);
                break;
            case 14:
                s_g_7.setChecked(true);
                break;
            case 15:
                s_g_9.setChecked(true);
                break;
            case 16:
                s_g_2.setChecked(true);
                break;
            case 17:
                s_g_4.setChecked(true);
                break;
            case 18:
                s_g_6.setChecked(true);
                break;
            case 19:
                s_g_8.setChecked(true);
                break;
            case 20:
                s_g_10.setChecked(true);
                break;
            case 0:
                s_g_0.setChecked(true);
                break;
            default:
                break;
        }

        RadioButton bl_1 = findViewById(R.id.bl_1);
        RadioButton bl_2 = findViewById(R.id.bl_2);
        RadioButton bl_3 = findViewById(R.id.bl_3);

        switch (battery_level) {
            case "normal":
                bl_1.setChecked(true);
                break;
            case "average":
                bl_2.setChecked(true);
                break;
            case "critical":
                bl_3.setChecked(true);
                break;
            default:
                break;
        }


        RadioButton nf_1 = findViewById(R.id.nf_1);
        RadioButton nf_2 = findViewById(R.id.nf_2);
        RadioButton nf_3 = findViewById(R.id.nf_3);

        switch (preference_tab) {
            case "News Feed":
                nf_1.setChecked(true);
                break;
            case "Notifications":
                nf_2.setChecked(true);
                break;
            case "Profile":
                nf_3.setChecked(true);
                break;
            default:
                break;
        }


        RadioButton mu_1 = findViewById(R.id.mu_1);
        RadioButton mu_2 = findViewById(R.id.mu_2);

        switch (sound_preference) {
            case "Mute":
                mu_1.setChecked(true);
                break;
            case "UnMute":
                mu_2.setChecked(true);
                break;
            default:
                break;
        }

        RadioButton iv_1 = findViewById(R.id.iv_1);
        RadioButton iv_2 = findViewById(R.id.iv_2);


        switch (upload_preference) {
            case "Image Upload":
                iv_1.setChecked(true);
                break;
            case "Video Upload":
                iv_2.setChecked(true);
                break;
            default:
                break;
        }



        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        startTime = dateFormat.format(date);

        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);
        retrievedBehaviourData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);

        switch_1 = findViewById(R.id.switch_1);
        switch_2 = findViewById(R.id.switch_2);
        switch_3 = findViewById(R.id.switch_3);
        switch_4 = findViewById(R.id.switch_4);
        if (!retrievedData[0].equals("NO EXIST")) {
            if (retrievedData[9] != null) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                Pattern p = Pattern.compile(Pattern.quote("D:") + "(.*?)" + Pattern.quote("|"));
                Matcher m = p.matcher(retrievedData[9]);
                Pattern p2 = Pattern.compile(Pattern.quote("|M:") + "(.*?)" + Pattern.quote("|"));
                Matcher m2 = p2.matcher(retrievedData[9]);
                Pattern p3 = Pattern.compile(Pattern.quote("|Y:") + "(.*?)" + Pattern.quote("|"));
                Matcher m3 = p3.matcher(retrievedData[9]);
                if (m.find()) {
                    System.out.println("date: " + m.group(1));
                }
                if (m2.find()) {
                    System.out.println("month: " + m2.group(1));
                }
                if (m3.find()) {
                    System.out.println("year: " + m3.group(1));
                }
                int yourAge = year - Integer.parseInt(m3.group(1));
                if (yourAge > 0) {
                    userAge = yourAge;
                }
            }
            if (retrievedData[10] != null) {
                userGender = retrievedData[10];
            }
        }

//        arr[6] = context_identification;
//        arr[7] = user_history_identification;
//        arr[8] = user_behaviour_identification;

//        switch_1 = Dynamic Behaviour based on Age/Gender[User History]
//        switch_2 = Dynamic Behaviour based on User Context
//        switch_3 = Dynamic Behaviour based on User Behaviour


        if (!retrievedBehaviourData[0].equals("NO EXIST")) {
            if (retrievedBehaviourData[6] != null) {
                if (retrievedBehaviourData[6].equals("off")) {
                    switch_2.setChecked(false);
                } else {
                    switch_2.setChecked(true);
                }

            }
            if (retrievedBehaviourData[7] != null) {
                if (retrievedBehaviourData[7].equals("off")) {
                    switch_1.setChecked(false);
                } else {
                    switch_1.setChecked(true);
                }
            }
            if (retrievedBehaviourData[8] != null) {
                if (retrievedBehaviourData[8].equals("off")) {
                    switch_3.setChecked(false);
                } else {
                    switch_3.setChecked(true);
                }
            }
            if (retrievedBehaviourData[11] != null) {
                if (retrievedBehaviourData[11].equals("off")) {
                    switch_4.setChecked(false);
                } else {
                    switch_4.setChecked(true);
                }
            }
        }

        mContext = this;

//Retrieve the ListView where we’ll display our activity data//
        ListView detectedActivitiesListView = findViewById(R.id.activities_listview);

        ArrayList<DetectedActivity> detectedActivities = ActivityIntentService.detectedActivitiesFromJson(
                PreferenceManager.getDefaultSharedPreferences(this).getString(
                        DETECTED_ACTIVITY, ""));

//Bind the adapter to the ListView//
        mAdapter = new ActivitiesAdapter(this, detectedActivities);
        detectedActivitiesListView.setAdapter(mAdapter);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);

//        Dynamic Behaviour based on Age/Gender[User History]
        switch_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (!switch_4.isChecked()) {
                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                null, null, null, null,
                                null, null, "on",
                                null, null, null, null);
                        //  Toast.makeText(mContext, switch_1.getTextOn().toString(), Toast.LENGTH_SHORT).show();
                        dynamicBehaviourBasedOnAgeGender(userAge, userGender);
                    } else {
                        switch_1.setChecked(false);
                        Toast.makeText(mContext, "Please Disable Simulation Mode!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, null, "off",
                            null, null,
                            null, null);
                    Toast.makeText(mContext, switch_1.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                    recreateActivity();
                }
            }
        });

//Dynamic Behaviour based on User Context
        switch_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (!switch_4.isChecked()) {
                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                null, null, null, null,
                                null, "on", null,
                                null, null, null, null);
                        Toast.makeText(mContext, switch_1.getTextOn().toString(), Toast.LENGTH_SHORT).show();

                        //Set the activity detection interval. I’m using 1 seconds//
                        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                                1000,
                                getActivityDetectionPendingIntent());
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                updateDetectedActivitiesList();
                            }
                        });

                    } else {
                        switch_2.setChecked(false);
                        Toast.makeText(mContext, "Please Disable Simulation Mode!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, "off", null,
                            null, null,
                            null, null);
                    Toast.makeText(mContext, switch_1.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                    recreateActivity();
                }
            }
        });

//Dynamic Behaviour based on User Behaviour
        switch_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (!switch_4.isChecked()) {
                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                null, null, null, null,
                                null, null, null,
                                "on", null, null, null);
                        Toast.makeText(mContext, switch_1.getTextOn().toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        switch_3.setChecked(false);
                        Toast.makeText(mContext, "Please Disable Simulation Mode!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, null, null,
                            "off", null,
                            null, null);
                    Toast.makeText(mContext, switch_1.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                    recreateActivity();
                }
            }
        });


        //Enabling Disabling Simulation
        switch_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, null, null,
                            null, null, null, "on");
                    Toast.makeText(mContext, switch_4.getTextOn().toString(), Toast.LENGTH_SHORT).show();

                    switch_1.setChecked(false);
                    switch_2.setChecked(false);
                    switch_3.setChecked(false);

                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, null, "off",
                            null, null,
                            null, null);
                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, "off", null,
                            null, null,
                            null, null);
                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, null, null,
                            "off", null,
                            null, null);


                } else {
                    alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                            null, null, null, null,
                            null, null, null,
                            null, null,
                            null, "off");
                    Toast.makeText(mContext, switch_4.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                    recreateActivity();
                }
            }
        });

        mySwipeRefreshLayout = findViewById(R.id.activity_detect_context);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doYourUpdate();
                    }
                }
        );

        popupDialog = new Dialog(this);
    }

    private void doYourUpdate() {
        // TODO implement a refresh
        finish();
        Intent intent = new Intent(this, TabContentActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        mySwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
    }


    public void dynamicBehaviourBasedOnAgeGender(int age, String gender) {
        if (!(gender == null || gender.isEmpty() || age == 0)) {

            if (age <= 19) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 1);
                    recreateActivity();
                } else {
                    Utility.setTheme(getApplicationContext(), 6);
                    recreateActivity();
                }
            } else if (age > 20 && age < 30) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 2);
                    recreateActivity();
                } else {
                    Utility.setTheme(getApplicationContext(), 7);
                    recreateActivity();
                }

            } else if (age >= 30 && age < 40) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 3);
                    recreateActivity();
                } else {
                    Utility.setTheme(getApplicationContext(), 8);
                    recreateActivity();
                }
            } else if (age > 40 && age < 55) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 4);
                    recreateActivity();
                } else {
                    Utility.setTheme(getApplicationContext(), 9);
                    recreateActivity();
                }
            } else if (age >= 55) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 5);
                    recreateActivity();
                } else {
                    Utility.setTheme(getApplicationContext(), 10);
                    recreateActivity();
                }

            } else {

            }

        } else {
            Toast.makeText(mContext, "This feature is only available once you filled your profile data.", Toast.LENGTH_SHORT).show();
            alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                    null, null, null, null,
                    null, null, "off",
                    null, null, null, null);
            recreateActivity();
        }
    }

    public void recreateActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        updateDetectedActivitiesList();
    }

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void requestUpdatesHandler(View view) {
//Set the activity detection interval. I’m using 1 seconds//
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                1000,
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateDetectedActivitiesList();
            }
        });
    }

    //Get a PendingIntent//
    private PendingIntent getActivityDetectionPendingIntent() {
//Send the activity data to our DetectedActivitiesIntentService class//
        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    //Process the list of activities//
    protected void updateDetectedActivitiesList() {
        ArrayList<DetectedActivity> detectedActivities = ActivityIntentService.detectedActivitiesFromJson(
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString(DETECTED_ACTIVITY, ""));

        mAdapter.updateActivities(detectedActivities);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(DETECTED_ACTIVITY)) {
            updateDetectedActivitiesList();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String dateStart = startTime;
        String dateStop = dateFormat.format(date);

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;


        long diffMinutes = 0;
        long diffSeconds = 0;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            diffSeconds = diff / 1000 % 60;
            diffMinutes = diff / (60 * 1000) % 60;
            final long diffHours = diff / (60 * 60 * 1000) % 24;
//            diffDays = diff / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        Toast.makeText(DetectContextActivity.this, diffMinutes + " " + diffSeconds, Toast.LENGTH_SHORT).show();

        super.onDestroy();

    }

    public void updateTheme() {
        if (Utility.getTheme(getApplicationContext()) == 1) {
            setTheme(R.style.AppThemeForDG1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg1_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 2) {
            setTheme(R.style.AppThemeForDG2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg2_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 3) {
            setTheme(R.style.AppThemeForDG3);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg3_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 4) {
            setTheme(R.style.AppThemeForDG4);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg4_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 5) {
            setTheme(R.style.AppThemeForDG5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg5_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 6) {
            setTheme(R.style.AppThemeForDG6);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg6_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 7) {
            setTheme(R.style.AppThemeForDG7);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg7_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 8) {
            setTheme(R.style.AppThemeForDG8);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg8_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 9) {
            setTheme(R.style.AppThemeForDG9);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg9_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 10) {
            setTheme(R.style.AppThemeForDG10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg10_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 11) {
            setTheme(R.style.AppThemeForDG11);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg1_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 12) {
            setTheme(R.style.AppThemeForDG12);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg2_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 13) {
            setTheme(R.style.AppThemeForDG13);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg3_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 14) {
            setTheme(R.style.AppThemeForDG14);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg4_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 15) {
            setTheme(R.style.AppThemeForDG15);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg5_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 16) {
            setTheme(R.style.AppThemeForDG16);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg6_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 17) {
            setTheme(R.style.AppThemeForDG17);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg7_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 18) {
            setTheme(R.style.AppThemeForDG18);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg8_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 19) {
            setTheme(R.style.AppThemeForDG19);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg9_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 20) {
            setTheme(R.style.AppThemeForDG20);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dg10_status_bar_background_color));
            }
        } else if (Utility.getTheme(getApplicationContext()) == 0) {
            setTheme(R.style.AppThemeDefault0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_background_color));
            }
        } else {
            setTheme(R.style.AppThemeDefault);
        }
    }

    public void setContext(View view) {
        if (switch_4.isChecked()) {
            RadioGroup radioGroup = findViewById(R.id.context_group);
            // get selected radio button from radioGroup
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            RadioButton radioButton = findViewById(selectedId);
            String x = radioButton.getText().toString();
            switch (x) {
                case "Still":
                    stillContext();
                    break;
                case "Walking":
                    walkingContext();
                    break;
                case "Running":
                    runningContext();
                    break;
                case "In a Vehicle":
                    vehicleContext();
                    break;
                case "On a Bicycle":
                    bicycleContext();
                    break;
                case "Tilting":
                    tiltingContext();
                    break;
                case "Unknown activity":
                    unknownContext();
                    break;
                default:
                    System.out.println("Something Went Wrong!");
            }

        } else {
            Toast.makeText(mContext, "Please enable simulation mode!.", Toast.LENGTH_SHORT).show();
        }
    }

    //    ------------------------Dynamic context adaptability-----------------------
    public void stillContext() {
        alchemistsDataSource.updateUserDetails(null, null, null,
                sharedPrefEmailAddress, null, null,
                null, "still", null, null, null);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_context", "still");
        editor.commit();

        //downgrading theme
        if (Utility.getTheme(getApplicationContext()) > 10) {
            Utility.setTheme(getApplicationContext(), Utility.getTheme(getApplicationContext()) - 10);
        }
        if (Utility.getTheme(getApplicationContext()) == 0) {
            Utility.setTheme(getApplicationContext(), -1);
        }
        finish();
        Intent intent = new Intent(DetectContextActivity.this, TabContentActivity.class);
        startActivity(intent);
    }

    public void walkingContext() {
        alchemistsDataSource.updateUserDetails(null, null, null,
                sharedPrefEmailAddress, null, null,
                null, "walking", null, null, null);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_context", "walking");
        editor.commit();

        //upgrading theme
        if (Utility.getTheme(getApplicationContext()) == -1) {
            Utility.setTheme(getApplicationContext(), 0);
        } else if (Utility.getTheme(getApplicationContext()) > 0 && Utility.getTheme(getApplicationContext()) <= 10) {
            Utility.setTheme(getApplicationContext(), Utility.getTheme(getApplicationContext()) + 10);
        }

        finish();
        Intent intent = new Intent(DetectContextActivity.this, TabContentActivity.class);
        startActivity(intent);

    }

    public void runningContext() {
        alchemistsDataSource.updateUserDetails(null, null, null,
                sharedPrefEmailAddress, null, null,
                null, "running", null, null, null);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_context", "running");
        editor.commit();

        //upgrading theme
        if (Utility.getTheme(getApplicationContext()) == -1) {
            Utility.setTheme(getApplicationContext(), 0);
        } else if (Utility.getTheme(getApplicationContext()) > 0 && Utility.getTheme(getApplicationContext()) <= 10) {
            Utility.setTheme(getApplicationContext(), Utility.getTheme(getApplicationContext()) + 10);
        }

        Intent intent = new Intent(DetectContextActivity.this, TabContentActivity.class);
        startActivity(intent);
    }

    public void vehicleContext() {
        alchemistsDataSource.updateUserDetails(null, null, null,
                sharedPrefEmailAddress, null, null,
                null, "vehicle", null, null, null);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                edit();
        editor.putString("user_context", "vehicle");
        editor.commit();

        //upgrading theme
        if (Utility.getTheme(getApplicationContext()) == -1) {
            Utility.setTheme(getApplicationContext(), 0);
        } else if (Utility.getTheme(getApplicationContext()) > 0 && Utility.getTheme(getApplicationContext()) <= 10) {
            Utility.setTheme(getApplicationContext(), Utility.getTheme(getApplicationContext()) + 10);
        }


        finish();
        Intent intent = new Intent(DetectContextActivity.this,
                TabContentActivity.class);
        startActivity(intent);
    }

    public void bicycleContext() {
        alchemistsDataSource.updateUserDetails(null, null, null,
                sharedPrefEmailAddress, null, null,
                null, "bicycle", null, null, null);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                edit();
        editor.putString("user_context", "bicycle");
        editor.commit();

        //upgrading theme
        if (Utility.getTheme(getApplicationContext()) == -1) {
            Utility.setTheme(getApplicationContext(), 0);
        } else if (Utility.getTheme(getApplicationContext()) > 0 && Utility.getTheme(getApplicationContext()) <= 10) {
            Utility.setTheme(getApplicationContext(), Utility.getTheme(getApplicationContext()) + 10);
        }

        finish();
        Intent intent = new Intent(DetectContextActivity.this,
                TabContentActivity.class);
        startActivity(intent);

    }

    public void tiltingContext() {
        alchemistsDataSource.updateUserDetails(null, null, null,
                sharedPrefEmailAddress, null, null,
                null, "tilting", null, null, null);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_context", "tilting");
        editor.commit();


        //upgrading theme
        if (Utility.getTheme(getApplicationContext()) == -1) {
            Utility.setTheme(getApplicationContext(), 0);
        } else if (Utility.getTheme(getApplicationContext()) > 0 && Utility.getTheme(getApplicationContext()) <= 10) {
            Utility.setTheme(getApplicationContext(), Utility.getTheme(getApplicationContext()) + 10);
        }

        finish();
        Intent intent = new Intent(DetectContextActivity.this, TabContentActivity.class);
        startActivity(intent);

    }

    public void unknownContext() {
        alchemistsDataSource.updateUserDetails(null, null, null,
                sharedPrefEmailAddress, null, null,
                null, "unknown", null, null, null);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_context", "unknown");
        editor.commit();

        //downgrading theme
        if (Utility.getTheme(getApplicationContext()) > 10) {
            Utility.setTheme(getApplicationContext(), Utility.getTheme(getApplicationContext()) - 10);
        }
        if (Utility.getTheme(getApplicationContext()) == 0) {
            Utility.setTheme(getApplicationContext(), -1);
        }

        finish();
        Intent intent = new Intent(DetectContextActivity.this, TabContentActivity.class);
        startActivity(intent);
    }

//    ----------------------------------------------------------

    public void setStyleGroup(View view) {
        if (switch_4.isChecked()) {
            RadioGroup radioGroup = findViewById(R.id.style_group);
            // get selected radio button from radioGroup
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            RadioButton radioButton = (RadioButton) findViewById(selectedId);
            String x = radioButton.getText().toString();
            switch (x) {
                case "Teenagers[Less than 19] Male":
                    String d1 = "D:22|M:3|Y:2005|";
                    String g1 = "male";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d1, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g1);
                    dynamicBehaviourBasedOnAgeGender(14, g1);
                    break;
                case "Teenagers[less than 19] Female":
                    String d2 = "D:22|M:3|Y:2005|";
                    String g2 = "female";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d2, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g2);
                    dynamicBehaviourBasedOnAgeGender(14, g2);
                    break;
                case "Young Adult[20-30 years] Male":
                    String d3 = "D:22|M:3|Y:1995|";
                    String g3 = "male";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d3, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g3);
                    dynamicBehaviourBasedOnAgeGender(24, g3);
                    break;
                case "Young Adult[20-30 years] Female":
                    String d4 = "D:22|M:3|Y:1995|";
                    String g4 = "female";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d4, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g4);
                    dynamicBehaviourBasedOnAgeGender(24, g4);
                    break;
                case "Adults[30-40 years] Male":
                    String d5 = "D:22|M:3|Y:1985|";
                    String g5 = "male";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d5, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g5);
                    dynamicBehaviourBasedOnAgeGender(34, g5);
                    break;
                case "Adults[30-40 years] Female":
                    String d6 = "D:22|M:3|Y:1985|";
                    String g6 = "female";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d6, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g6);
                    dynamicBehaviourBasedOnAgeGender(34, g6);
                    break;
                case "Middle Aged[40-55 years] Male":
                    String d7 = "D:22|M:3|Y:1975|";
                    String g7 = "male";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d7, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g7);
                    dynamicBehaviourBasedOnAgeGender(44, g7);
                    break;
                case "Middle Aged[40-55 years] Female":
                    String d8 = "D:22|M:3|Y:1975|";
                    String g8 = "female";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d8, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g8);
                    dynamicBehaviourBasedOnAgeGender(44, g8);
                    break;
                case "Senior Citizens(55 years or More) Male":
                    String d9 = "D:22|M:3|Y:1960|";
                    String g9 = "male";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d9, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g9);
                    dynamicBehaviourBasedOnAgeGender(59, g9);
                    break;
                case "Senior Citizens(55 years or More) Female":
                    String d10 = "D:22|M:3|Y:1960|";
                    String g10 = "female";
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, d10, null);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, g10);
                    dynamicBehaviourBasedOnAgeGender(59, g10);
                    break;
                default:
                    System.out.println("Something Went Wrong!");
            }
        } else {
            Toast.makeText(mContext, "Please enable simulation mode!.", Toast.LENGTH_SHORT).show();
        }

    }

    public void setBatteryLevel(View view) {
        if (switch_4.isChecked()) {
            RadioGroup radioGroup = findViewById(R.id.batteryLevelRG);
            // get selected radio button from radioGroup
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            RadioButton radioButton = (RadioButton) findViewById(selectedId);
            String x = radioButton.getText().toString();
            switch (x) {
                case "Normal":
                    ShowPopup("Battery Monitor has changed some App configurations:\n" +
                            "Activity Detection Interval has been reduced.!");
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor.putString("battery_level", "normal");
                    editor.putString("activity_detection_interval", "1000");
                    editor.commit();
                    break;
                case "Average":
                    ShowPopup("Battery Monitor has changed some App configurations:\n" +
                            "Activity Detection Interval has been increased.!");
                    SharedPreferences.Editor editor2 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor2.putString("battery_level", "average");
                    editor2.putString("activity_detection_interval", "3000");
                    editor2.commit();
                    break;
                case "Critical":
                    ShowPopup("Battery Monitor has changed some App configurations:\n" +
                            "Remove Adaptation based on Context Awareness.! \n" +
                            "Disable Video Uploading.! \n" +
                            "But Enable Image Uploading.!");
                    SharedPreferences.Editor editor3 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor3.putString("battery_level", "critical");
                    editor3.commit();
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(mContext, "Please enable simulation mode!.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTabPreference(View view) {
        if (switch_4.isChecked()) {
            RadioGroup radioGroup = findViewById(R.id.tabPreferenceRG);
            // get selected radio button from radioGroup
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            RadioButton radioButton = findViewById(selectedId);
            String x = radioButton.getText().toString();
            switch (x) {
                case "News Feed":
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor.putString("preference_tab", "News Feed");
                    editor.commit();
                    recreateActivity();
                    break;
                case "Notifications":
                    SharedPreferences.Editor editor2 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor2.putString("preference_tab", "Notifications");
                    editor2.commit();
                    recreateActivity();
                    break;
                case "Profile":
                    SharedPreferences.Editor editor3 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor3.putString("preference_tab", "Profile");
                    editor3.commit();
                    recreateActivity();
                    break;
                default:
                    break;

            }
        } else {
            Toast.makeText(mContext, "Please enable simulation mode!", Toast.LENGTH_SHORT).show();
        }
    }

    public void setMuteUnMutePreference(View view) {
        if (switch_4.isChecked()) {
            RadioGroup radioGroup = findViewById(R.id.muteUnmutePrefernceRG);
            // get selected radio button from radioGroup
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            RadioButton radioButton = findViewById(selectedId);
            String x = radioButton.getText().toString();
            switch (x) {
                case "Mute":
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor.putString("sound_preference", "Mute");
                    editor.commit();
                    recreateActivity();
                    break;
                case "UnMute":
                    SharedPreferences.Editor editor2 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor2.putString("sound_preference", "UnMute");
                    editor2.commit();
                    recreateActivity();
                    break;
                default:
                    break;

            }
        } else {
            Toast.makeText(mContext, "Please enable simulation mode.!", Toast.LENGTH_SHORT).show();
        }
    }

    public void setUploadPreferene(View view) {
        if (switch_4.isChecked()) {
            RadioGroup radioGroup = findViewById(R.id.uploadPreferenceRG);
            // get selected radio button from radioGroup
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            RadioButton radioButton = findViewById(selectedId);
            String x = radioButton.getText().toString();
            switch (x) {
                case "Image Upload":
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor.putString("upload_preference", "Image Upload");
                    editor.commit();
                    recreateActivity();
                    break;
                case "Video Upload":
                    SharedPreferences.Editor editor2 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                            edit();
                    editor2.putString("upload_preference", "Video Upload");
                    editor2.commit();
                    recreateActivity();
                    break;
                default:
                    break;

            }
        } else {
            Toast.makeText(mContext, "Please enable simulation mode!.", Toast.LENGTH_SHORT).show();
        }
    }

    public void ShowPopup(String text) {
        TextView descriptionText;
        Button okButton;
        popupDialog.setContentView(R.layout.custom_popup);
        descriptionText = popupDialog.findViewById(R.id.descriptionText);
        descriptionText.setText(text);
        descriptionText.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        descriptionText.setTextColor(Color.parseColor("#FFFFFF"));
        okButton = popupDialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(popupDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 50;
        lp.y = 50;
        popupDialog.getWindow().setAttributes(lp);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, matches));
//            // matches is the result of voice input. It is a list of what the
//            // user possibly said.
//            // Using an if statement for the keyword you want to use allows the
//            // use of any activity if keywords match
//            // it is possible to set up multiple keywords to use the same
//            // activity so more than one word will allow the user
//            // to use the activity (makes it so the user doesn't have to
//            // memorize words from a list)
//            // to use an activity from the voice input information simply use
//            // the following format;
//            // if (matches.contains("keyword here") { startActivity(new
//            // Intent("name.of.manifest.ACTIVITY")
//
//            if (matches.contains("information")) {
//                informationMenu();
//            }
        }
    }

}