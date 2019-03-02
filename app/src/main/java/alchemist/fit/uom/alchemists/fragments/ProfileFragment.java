package alchemist.fit.uom.alchemists.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.activities.DetectContextActivity;
import alchemist.fit.uom.alchemists.activities.EditProfileActivity;
import alchemist.fit.uom.alchemists.activities.ReportMakeActivity;
import alchemist.fit.uom.alchemists.activities.TabContentActivity;
import alchemist.fit.uom.alchemists.activities.ViewUserPostsActivity;
import alchemist.fit.uom.alchemists.adapters.MyVideosAdapter;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    private LinearLayout makeTestForReport;
    private Button makePhotoToReport;
    private Button makeVideoToReport;
    private ImageButton personalSettings;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private String[] retrievedData;
    private Button vviewProfilePosts;
    private Picasso picasso;
    private String sharedPreContext;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private String startTime;
    private View fragment_profile;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        startTime = dateFormat.format(date);
//        if (android.os.Build.VERSION.SDK_INT >= 21) {
//            Window window = getActivity().getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
//        }
        alchemistsDataSource = new AlchemistsDataSource(getActivity());
        alchemistsDataSource.open();
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);
        picasso = Picasso.with(getActivity());
        sharedPreContext = prefs.getString("user_context", "No name defined");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment_profile = inflater.inflate(R.layout.fragment_profile, container, false);
        makeTestForReport = fragment_profile.findViewById(R.id.makeTestForReport);
        makePhotoToReport = fragment_profile.findViewById(R.id.makePhotoToReport);
        makeVideoToReport = fragment_profile.findViewById(R.id.makeVideoToReport);
        personalSettings = fragment_profile.findViewById(R.id.personal_settings);
        TextView t1 = fragment_profile.findViewById(R.id.fragment_profile2_profile_name);
        TextView t2 = fragment_profile.findViewById(R.id.fragment_profile2_user_location);
        TextView t3 = fragment_profile.findViewById(R.id.fragment_profile2_mobile_number);
        vviewProfilePosts = fragment_profile.findViewById(R.id.viewProfilePosts);
        CircleImageView circleImageView = fragment_profile.findViewById(R.id.fragment_profile_profile_url);
        CircleImageView circleImageView2 = fragment_profile.findViewById(R.id.fragment_profile_profile_image);
        CircleImageView fragment_profile_location_imageview = fragment_profile.findViewById(R.id.fragment_profile_location_imageview);
        CircleImageView fragment_profile_mobile_number_imageview = fragment_profile.findViewById(R.id.fragment_profile_mobile_number_imageview);
        ImageView imageView = fragment_profile.findViewById(R.id.fragment_profile_cover_image);
        if (sharedPreContext.equals("bicycle")) {
            personalSettings.setVisibility(View.INVISIBLE);
        } else if (sharedPreContext.equals("running")) {
            personalSettings.setVisibility(View.INVISIBLE);
        } else {
            personalSettings.setVisibility(View.VISIBLE);
        }


        if (!retrievedData[0].equals("NO EXIST")) {
            if (retrievedData[0] != null) {
                t1.setText(retrievedData[0]);
            }
            if (retrievedData[1] != null) {
                t2.setText(retrievedData[1]);
            }
            if (retrievedData[2] != null) {
                t3.setText(retrievedData[2]);
            }
            if (retrievedData[5] != null) {
                picasso.load(retrievedData[5])
                        .placeholder(R.drawable.s1).error(R.drawable.s1)
                        .into(circleImageView);
                picasso.load(retrievedData[5])
                        .placeholder(R.drawable.s1).error(R.drawable.s1)
                        .into(circleImageView2);

                picasso.load(retrievedData[5])
                        .placeholder(R.drawable.s1).error(R.drawable.s1)
                        .into(fragment_profile_location_imageview);

                picasso.load(retrievedData[5])
                        .placeholder(R.drawable.s1).error(R.drawable.s1)
                        .into(fragment_profile_mobile_number_imageview);

            }
            if (retrievedData[6] != null) {
                picasso.load(retrievedData[6])
                        .placeholder(R.drawable.s1).error(R.drawable.s1)
                        .into(imageView);
            }


        }

        makeTestForReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                getActivity().finish();
                Intent intent = new Intent(getActivity(), ReportMakeActivity.class);
                startActivity(intent);
            }
        });
        makePhotoToReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), ReportMakeActivity.class);
                startActivity(intent);
            }
        });
        makeVideoToReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), ReportMakeActivity.class);
                startActivity(intent);
            }
        });

        personalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        vviewProfilePosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewUserPostsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        mySwipeRefreshLayout = fragment_profile.findViewById(R.id.fragment_profile_swipe_refresh_layout);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doYourUpdate();
                    }
                }
        );


        return fragment_profile;
    }

    private void doYourUpdate() {
        // TODO implement a refresh
        getActivity().finish();
        Intent intent = new Intent(getActivity(), DetectContextActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        mySwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
    }

    @Override
    public void onDestroy() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateStart = Constants.appStartTime;
        String dateStop = dateFormat.format(date);
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d1;
        Date d2;
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
            if (diffHours == 0 && diffMinutes < 2) {
                String contextInitialTime = startTime;
                String contextEndTime = dateStop;
                Date d3;
                Date d4;
                long diffMinutesNew = 0;
                long diffSecondsNew = 0;
                try {
                    d3 = format.parse(contextInitialTime);
                    d4 = format.parse(contextEndTime);
                    //in milliseconds
                    long difference = d4.getTime() - d3.getTime();
                    diffSecondsNew = difference / 1000 % 60;
                    diffMinutesNew = difference / (60 * 1000) % 60;
                    final long diffHoursNew = difference / (60 * 60 * 1000) % 24;
                    // Toast.makeText(getActivity(), diffMinutesNew+" "+diffSecondsNew, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                }
                long viewTime = diffMinutesNew * 60 + diffSecondsNew;
                String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                long profileTime = 0;

                if (!retrievedData[0].equals("NO EXIST")) {
                    if (retrievedData[2] != null) {
                        profileTime = Long.parseLong(retrievedData[2]);
                    }
                }
                profileTime += viewTime;

                alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                        null, String.valueOf(profileTime), null,
                        null, null, null, null,
                        null, null, null, null);
            } else {
                //check wether beigin time is in the application startup 2minutes range.
                String x = Constants.appStartTime; // 2.15.30 a.m
                String y = startTime; // start 2.16.00 a.m finish 2.18 a.m
                Date d5;
                Date d6;
                try {
                    d5 = format.parse(x); //2.15.30 a.m
                    d6 = format.parse(y);  // 2.16.00 a.m
                    //in milliseconds
                    long difference = d6.getTime() - d5.getTime(); //0.00.30
                    long seconds = difference / 1000 % 60;
                    long minutes = difference / (60 * 1000) % 60;
                    long hours = diff / (60 * 60 * 1000) % 24;
                    if (hours == 0 && (minutes * 60 + seconds) < 120) {
                        long viewRange = 120 - (minutes * 60 + seconds); //0.0.90
                        // Toast.makeText(getActivity(), String.valueOf(viewRange), Toast.LENGTH_SHORT).show();
                        String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                        long profileTime = 0;
                        if (!retrievedData[0].equals("NO EXIST")) {
                            if (retrievedData[2] != null) {
                                profileTime = Long.parseLong(retrievedData[2]);
                            }
                        }
                        profileTime += viewRange;

                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                null, String.valueOf(profileTime), null,
                                null, null, null,
                                null, null, null,
                                null, null);

                    } else {
                        //  Toast.makeText(getActivity(), "out of time range", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d("Found an exception", e.toString());
                }
            }
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }
        super.onDestroy();
    }
}