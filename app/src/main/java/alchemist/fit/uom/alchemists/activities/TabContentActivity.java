package alchemist.fit.uom.alchemists.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.androidadvance.topsnackbar.TSnackbar;
import com.firebase.client.collection.LLRBNode;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import alchemist.fit.uom.alchemists.ActivityIntentService;
import alchemist.fit.uom.alchemists.BatteryUsageChecker;
import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.Utility;
import alchemist.fit.uom.alchemists.adapters.MyVideosAdapter;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import alchemist.fit.uom.alchemists.fragments.NewsFeedFragment;
import alchemist.fit.uom.alchemists.fragments.NotificationsFragment;
import alchemist.fit.uom.alchemists.fragments.ProfileFragment;
import alchemist.fit.uom.alchemists.interfaces.OnBatteryStateReceiver;
import alchemist.fit.uom.alchemists.interfaces.OnContextListner;
import alchemist.fit.uom.alchemists.interfaces.OnViewCommentsListener;

public class TabContentActivity extends AppCompatActivity implements OnBatteryStateReceiver, OnViewCommentsListener, OnContextListner, View.OnTouchListener {

    private int fragmentId;
    private RelativeLayout parentLayout;
    private PopupWindow popupWindow;
    private ProgressDialog progressDialog;
    private Picasso picasso;
    private ListView listView;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Item> commentList = new ArrayList<>();
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private String sharedPrefUserId;
    private String sharedPreContext;
    private String sharedPreferenceTab;
    private String sharedPreferenceSound;
    private String postUniqueId;
    private ViewGroup editingLayout;
    private AlchemistsDataSource alchemistsDataSource;
    private String[] retrievedData;
    private String userContext;
    private String userDynamicAllowness;
    private long newsFeedFrequency, notificationFrequency,
            profileFrequency;
    private Long maxFrequency;
    private String[] retrievedBehaviouralData;
    private String context_identification, user_history_identification, user_behaviour_identification,
            muteFreq, unMuteFreq, simulation;
    private Dialog popupDialog;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = TabContentActivity.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
        sharedPrefUserId = prefs.getString("user_id", "No name defined");
        sharedPreContext = prefs.getString("user_context", "No name defined");
        sharedPreferenceTab = prefs.getString("preference_tab", "No name defined");
        sharedPreferenceSound = prefs.getString("sound_preference", "No name defined");

        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);
        retrievedBehaviouralData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);

        if (retrievedBehaviouralData != null) {
            if (!retrievedBehaviouralData[0].equals("NO EXIST")) {
                if (retrievedBehaviouralData[0] != null) {
                    newsFeedFrequency = Long.parseLong(retrievedBehaviouralData[0]);
                }
                if (retrievedBehaviouralData[1] != null) {
                    notificationFrequency = Long.parseLong(retrievedBehaviouralData[1]);
                }
                if (retrievedBehaviouralData[2] != null) {
                    profileFrequency = Long.parseLong(retrievedBehaviouralData[2]);
                }
                maxFrequency = Math.max(Math.max(newsFeedFrequency, notificationFrequency), profileFrequency);

                if (retrievedBehaviouralData[6] != null) {
                    context_identification = retrievedBehaviouralData[6];
                }
                if (retrievedBehaviouralData[7] != null) {
                    user_history_identification = retrievedBehaviouralData[7];
                }
                if (retrievedBehaviouralData[8] != null) {
                    user_behaviour_identification = retrievedBehaviouralData[8];
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
        if (user_behaviour_identification.equals("on")) {
            if (Long.parseLong(muteFreq) > Long.parseLong(unMuteFreq)) {
                Constants.setMute = true;
            } else {
                Constants.setMute = false;
            }
        } else {
            if (sharedPreferenceSound.equals("Mute")) {
                Constants.setMute = true;
            } else {
                Constants.setMute = false;
            }
        }

        if (user_history_identification.equals("on") || simulation.equals("on")) {
            updateTheme();
        }
        setContentView(R.layout.activity_tab_content);
        editingLayout = findViewById(R.id.activity_tab_layout);
        parentLayout = findViewById(R.id.activity_tab_layout);
        picasso = Picasso.with(TabContentActivity.this);

//        SharedPreferences prefs = TabContentActivity.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
//        sharedPrefUserId = prefs.getString("user_id", "No name defined");
//        sharedPreContext = prefs.getString("user_context", "No name defined");


        if (sharedPreContext.equals("vehicle")) {
            displaySnackBar(R.drawable.ic_driving, sharedPreContext, ">60");
        } else if (sharedPreContext.equals("bicycle")) {
            displaySnackBar(R.drawable.ic_on_bicycle, sharedPreContext, ">60");
        }
//        else if (sharedPreContext.equals("foot")) {
//            displaySnackBar(R.drawable.ic_walking, sharedPreContext, ">60");
//        }
        else if (sharedPreContext.equals("still")) {
            displaySnackBar(R.drawable.ic_still, sharedPreContext, ">60");
        } else if (sharedPreContext.equals("unknown")) {
            displaySnackBar(R.drawable.ic_unknown, sharedPreContext, ">60");
        } else if (sharedPreContext.equals("walking")) {
            displaySnackBar(R.drawable.ic_walking, sharedPreContext, ">60");
        } else if (sharedPreContext.equals("running")) {
            displaySnackBar(R.drawable.ic_running, sharedPreContext, ">60");
        }

//        alchemistsDataSource = new AlchemistsDataSource(this);
//        alchemistsDataSource.open();
//        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);
//        retrievedBehaviouralData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);

        if (!retrievedData[0].equals("NO EXIST")) {
            if (retrievedData[7] != null) {
                userContext = retrievedData[7];
            }
            if (retrievedData[8] != null) {
                userDynamicAllowness = retrievedData[8];
            }
        }

        BatteryUsageChecker.setOnBatteryStatusReceivedListener(this);
        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        MyVideosAdapter.setOnCommentsReceivedListener(this);
        ActivityIntentService.setOnContextListner(this);

        bottomNavigationView = findViewById(R.id.navigation);
        fragmentId = getIntent().getIntExtra("FRAGMENT_ID", 0);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = NewsFeedFragment.newInstance();
                                break;
                            case R.id.action_item2:
                                selectedFragment = NotificationsFragment.newInstance();
                                break;
                            case R.id.action_item3:
                                selectedFragment = ProfileFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        if (userContext != null && userContext.equals("vehicle") && (context_identification.equals("on") || simulation.equals("on"))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
            transaction.commit();
        } else if (userContext != null && userContext.equals("bicycle") && (context_identification.equals("on") || simulation.equals("on"))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, ProfileFragment.newInstance());
            transaction.commit();
            MenuItem item = bottomNavigationView.getMenu().getItem(2);
            item.setIcon(R.drawable.ic_person_black_24dp);
            item.setChecked(true);
            bottomNavigationView.getMenu().removeItem(R.id.action_item1);
            bottomNavigationView.getMenu().removeItem(R.id.action_item2);
        } else if (userContext != null && userContext.equals("running") && (context_identification.equals("on") || simulation.equals("on"))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, ProfileFragment.newInstance());
            transaction.commit();
            MenuItem item = bottomNavigationView.getMenu().getItem(2);
            item.setIcon(R.drawable.ic_person_black_24dp);
            item.setChecked(true);
            bottomNavigationView.getMenu().removeItem(R.id.action_item1);
            bottomNavigationView.getMenu().removeItem(R.id.action_item2);
        } else if (userContext != null && userContext.equals("walking") && (context_identification.equals("on") || simulation.equals("on"))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
            transaction.commit();
        } else if (userContext != null && userContext.equals("tilting") && (context_identification.equals("on") || simulation.equals("on"))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
            transaction.commit();
        } else if (userContext != null && userContext.equals("unknown") && (context_identification.equals("on") || simulation.equals("on"))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
            transaction.commit();
        } else if (fragmentId == 2) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, ProfileFragment.newInstance());
            transaction.commit();
            MenuItem item = bottomNavigationView.getMenu().getItem(2);
            item.setIcon(R.drawable.ic_person_black_24dp);
            item.setChecked(true);
        } else {
            if (maxFrequency > 0 && (user_behaviour_identification.equals("on"))) {
                DynamicTabPreference();
            } else if (simulation.equals("on")) {
                SimulatedTabPreference();
            } else {
                //Manually displaying the first fragment - one time only
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
                transaction.commit();
            }
        }
        addNewImage();

        popupDialog = new Dialog(this);
    }

    public void DynamicTabPreference() {
        if (maxFrequency == profileFrequency) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, ProfileFragment.newInstance());
            transaction.commit();
            MenuItem item = bottomNavigationView.getMenu().getItem(2);
            item.setIcon(R.drawable.ic_person_black_24dp);
            item.setChecked(true);
        } else if (maxFrequency == notificationFrequency) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, NotificationsFragment.newInstance());
            transaction.commit();
            MenuItem item = bottomNavigationView.getMenu().getItem(1);
            item.setIcon(R.drawable.ic_vpn_lock_black_24dp);
            item.setChecked(true);
        } else {
            //Manually displaying the first fragment - one time only
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
            transaction.commit();
        }
    }

    public void SimulatedTabPreference() {
        switch (sharedPreferenceTab) {
            case "News Feed":
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
                transaction.commit();
                break;
            case "Notifications":
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.frame_layout, NotificationsFragment.newInstance());
                transaction2.commit();
                MenuItem item2 = bottomNavigationView.getMenu().getItem(1);
                item2.setIcon(R.drawable.ic_vpn_lock_black_24dp);
                item2.setChecked(true);
                break;
            case "Profile":
                FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                transaction3.replace(R.id.frame_layout, ProfileFragment.newInstance());
                transaction3.commit();
                MenuItem item3 = bottomNavigationView.getMenu().getItem(2);
                item3.setIcon(R.drawable.ic_person_black_24dp);
                item3.setChecked(true);
                break;
            default:
                FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                transaction4.replace(R.id.frame_layout, NewsFeedFragment.newInstance());
                transaction4.commit();
                break;
        }
    }


    @Override
    public void onStatusReceived(int batteryLevel, int scale, int status, boolean isCharging, boolean usbCharge) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                TabContentActivity.this).create();
        // Setting Dialog Title
        alertDialog.setTitle("Warning!");
        // Setting Dialog Message
        alertDialog.setMessage(batteryLevel + " " + scale + " " + status + " " + isCharging + " " + usbCharge);
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.app_icon);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            if (context_identification.equals("on")) {
                if (level >= 40) {

                    SharedPreferences prefs = TabContentActivity.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    String battery_level = prefs.getString("battery_level", "No name defined");
                    if (!battery_level.equals("normal")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                                edit();
                        editor.putString("battery_level", "normal");
                        editor.putString("activity_detection_interval", "1000");
                        editor.commit();
                        finish();
                        Intent intent1 = new Intent(TabContentActivity.this, SplashScreenActivity.class);
                        startActivity(intent1);
//                    ShowPopup("Battery Monitor has changed some App configurations:\n" +
//                            "Activity Detection Interval has been reduced.!");
                    }
                    // showDialogMessage(level);
                } else if (level < 40 && level >= 20) {
                    SharedPreferences prefs = TabContentActivity.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    String battery_level = prefs.getString("battery_level", "No name defined");
                    if (!battery_level.equals("average")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                                edit();
                        editor.putString("battery_level", "average");
                        editor.putString("activity_detection_interval", "3000");
                        editor.commit();
                        finish();
                        Intent intent1 = new Intent(TabContentActivity.this, SplashScreenActivity.class);
                        startActivity(intent1);
//                    ShowPopup("Battery Monitor has changed some App configurations:\n" +
//                            "Activity Detection Interval has been increased.!");
                    }

                } else {
                    SharedPreferences prefs = TabContentActivity.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    String battery_level = prefs.getString("battery_level", "No name defined");
                    if (!battery_level.equals("critical")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).
                                edit();
                        editor.putString("battery_level", "critical");
                        editor.commit();
                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                null, null, null, null,
                                null, "off", null,
                                null, null,
                                null, null);
                        finish();
                        Intent intent1 = new Intent(TabContentActivity.this, TabContentActivity.class);
                        startActivity(intent1);
//                    ShowPopup("Battery Monitor has changed some App configurations:\n" +
//                            "Remove Adaptation based on Context Awareness.! \n" +
//                            "Disable Video Uploading.! \n" +
//                            "But Enable Image Uploading.!");

                    }
                }
            }

        }


    };

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

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            commentList.clear();
            commentsAdapter.clear();
            commentsAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(TabContentActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait ..."); // Setting Message
        //  progressDialog.setTitle("Please wait until news feed is loaded!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    @Override
    public void onCommentsReceived(String postId) {
        postUniqueId = postId;
        showProgressDialog();
        //  Toast.makeText(this, postId, Toast.LENGTH_SHORT).show();
        new TabContentActivity.downloadCommentsAsyncTask().execute(Constants.localAddress
                + "rest/ureportservice/getAllComments?post_id=" + URLEncoder.encode(postId));
    }

    @Override
    public void onContextReceived(int context, int percentage) {
        if (context_identification.equals("on")) {
            if (context == 0 && percentage > 60) {
                if (!sharedPreContext.equals("vehicle")) {
                    // displaySnackBar("VEHICLE",percentage);
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
                    Intent intent = new Intent(TabContentActivity.this,
                            TabContentActivity.class);
                    startActivity(intent);
                }
            } else if (context == 1 && percentage > 60) {
                if (!sharedPreContext.equals("bicycle")) {
                    //   displaySnackBar("BICYCLE",percentage);
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
                    Intent intent = new Intent(TabContentActivity.this,
                            TabContentActivity.class);
                    startActivity(intent);
                }
            } else if (context == 2 && percentage > 60) {
                if (!sharedPreContext.equals("walking")) {
                    // displaySnackBar("WALKING",percentage);
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
                    Intent intent = new Intent(TabContentActivity.this, TabContentActivity.class);
                    startActivity(intent);
                }
            } else if (context == 3 && percentage > 60) {
                if (!sharedPreContext.equals("still")) {
                    //  displaySnackBar("STILL",percentage);
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
                    Intent intent = new Intent(TabContentActivity.this, TabContentActivity.class);
                    startActivity(intent);
                }
            } else if (context == 4 && percentage > 60) {
                if (!sharedPreContext.equals("unknown")) {
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
                    Intent intent = new Intent(TabContentActivity.this, TabContentActivity.class);
                    startActivity(intent);
                }
            } else if (context == 5 && percentage > 80) {
                if (!sharedPreContext.equals("tilting")) {
                    // displaySnackBar("TILTING",percentage);
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
                    Intent intent = new Intent(TabContentActivity.this, TabContentActivity.class);
                    startActivity(intent);
                }


            } else if (context == 7 && percentage > 60) {
                if (!sharedPreContext.equals("walking")) {
                    //  displaySnackBar("WALKING",percentage);
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

                    //   Toast.makeText(this, "walking awa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TabContentActivity.this, TabContentActivity.class);
                    startActivity(intent);
                }

            } else if (context == 8 && percentage > 60) {
                if (!sharedPreContext.equals("running")) {
                    //  displaySnackBar("RUNNING",percentage);
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

                    Intent intent = new Intent(TabContentActivity.this, TabContentActivity.class);
                    startActivity(intent);
                }
            } else if (context == 16 && percentage > 60) {
//            alchemistsDataSource.updateUserDetails(null, null, null,
//                    sharedPrefEmailAddress, null, null,
//                    null, "road_vehicle", null);
            } else if (context == 17 && percentage > 60) {
//            alchemistsDataSource.updateUserDetails(null, null, null,
//                    sharedPrefEmailAddress, null, null,
//                    null, "rail_vehicle", null);
            }
        }
    }

    private void displaySnackBar(int drawablePath, String context, String accuracy) {
        TSnackbar snackbar = TSnackbar
                .make(findViewById(R.id.activity_tab_layout), "Identified context: " + String.valueOf(context) + "  Accuracy: " + String.valueOf(accuracy), TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(drawablePath, 24); //Size in dp - 24 is great!
        // snackbar.setIconRight(R.drawable.ic_android_green_24dp, 48); //Resize to bigger dp
        snackbar.setIconPadding(4);
        snackbar.setMaxWidth(3000); //if you want fullsize on tablets
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC00CC"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    private class downloadCommentsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            commentList.clear();

            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String time_stamp = jsonObject.getString("time_stamp");
                    String profile_img_url = jsonObject.getString("profile_img_url");
                    String user_name = jsonObject.getString("user_name");
                    String comment = jsonObject.getString("comment");


                    commentList.add(new Item(time_stamp, profile_img_url, user_name, comment));
                }
                showCommentPopupWindow(commentList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class Item {
        private String time_stamp;
        private String profile_img_url;
        private String user_name;
        private String comment;

        public Item(String time_stamp, String profile_img_url, String user_name,
                    String comment) {
            this.time_stamp = time_stamp;
            this.profile_img_url = profile_img_url;
            this.user_name = user_name;
            this.comment = comment;
        }

        public String getTime_stamp() {
            return time_stamp;
        }

        public String getProfile_img_url() {
            return profile_img_url;
        }

        public String getUser_name() {
            return user_name;
        }

        public String getComment() {
            return comment;
        }
    }

    public class CommentsAdapter extends ArrayAdapter<Item> {

        private Context mContext;
        private List<Item> commentList;

        public CommentsAdapter(@NonNull Context context, ArrayList<Item> list) {
            super(context, 0, list);
            mContext = context;
            commentList = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.comment_section_list_item, parent, false);

            Item currentCommment = commentList.get(position);

            ImageView image = listItem.findViewById(R.id.comment_section_list_item_profile_url);
            picasso.load(currentCommment.getProfile_img_url())
                    .placeholder(R.drawable.s1).error(R.drawable.s1)
                    .into(image);

            TextView t1 = listItem.findViewById(R.id.comment_section_list_item_user_name);
            t1.setText(currentCommment.getUser_name());

            TextView t2 = listItem.findViewById(R.id.comment_section_list_item_comment);
            t2.setText(currentCommment.getComment());

            TextView t3 = listItem.findViewById(R.id.comment_section_list_item_time_stamp);
            t3.setText(currentCommment.getTime_stamp());

            return listItem;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showCommentPopupWindow(ArrayList<Item> cl) {

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the comment_section layout/view
        View customView = inflater.inflate(R.layout.comment_section, null);
        Display display = TabContentActivity.this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        // Initialize a new instance of popup window
        popupWindow = new PopupWindow(
                customView,
                width - 100,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        // Set an elevation value for popup window
        // Call requires API level 21
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        listView = customView.findViewById(R.id.comment_list);
        commentsAdapter = new CommentsAdapter(TabContentActivity.this, cl);
        listView.setAdapter(commentsAdapter);

        final EditText new_comment_edit_text = customView.findViewById(R.id.new_comment_edit_text);
        new_comment_edit_text.setFocusable(true);
        new_comment_edit_text.setShowSoftInputOnFocus(true);
        ImageButton new_comment_submit_button = customView.findViewById(R.id.new_comment_submit_button);
        new_comment_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newComment = new_comment_edit_text.getText().toString();
                if (newComment.equals("")) {
                    Toast.makeText(TabContentActivity.this, "Bad Try", Toast.LENGTH_SHORT).show();
                } else {
                    showProgressDialog();
                    String url = Constants.localAddress + "rest/ureportservice/createComment?PostId=" + URLEncoder.encode(postUniqueId) + "&comment=" + URLEncoder.encode(newComment) + "&UserId=" + URLEncoder.encode(sharedPrefUserId);
                    new TabContentActivity.createCommentAsyncTask().execute(url);
                    new_comment_edit_text.setText("");
                }
            }
        });


        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.setElevation(5.0f);
        }
        popupWindow.showAtLocation(parentLayout, Gravity.CENTER, 0, 0);
    }

    private class createCommentAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            switch (result) {
                case "404":
                    Toast.makeText(TabContentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    break;
                case "Successful":
                    Toast.makeText(TabContentActivity.this, "commented!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(TabContentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void addNewImage() {
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.report_icon);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
        iv.setLayoutParams(layoutParams);
        editingLayout.addView(iv, layoutParams);
        iv.setOnTouchListener(TabContentActivity.this);
    }

    int clickCount;
    private int posX;
    private int posY;
    long startTime = 0;

    public boolean onTouch(final View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        int pointerCount = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                posX = X - layoutParams.leftMargin;
                posY = Y - layoutParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                if (startTime == 0) {
                    startTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - startTime < 200) {

                        Intent intent = new Intent(TabContentActivity.this, ReportMakeActivity.class);
                        startActivity(intent);
                        TabContentActivity.this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                    }
                    startTime = System.currentTimeMillis();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    Params.leftMargin = X - posX;
                    Params.topMargin = Y - posY;
                    Params.rightMargin = -500;
                    Params.bottomMargin = -500;
                    view.setLayoutParams(Params);
                }
                if (pointerCount == 2) {
//                    Log.e("TAG","2 finger touched");
//                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                    layoutParams1.width = posX +(int)event.getX();
//                    layoutParams1.height = posY + (int)event.getY();
//                    view.setLayoutParams(layoutParams1);
                }
//Rotation
                if (pointerCount == 3) {
//Rotate the ImageView
//                    view.setRotation(view.getRotation() + 10.0f);
                }
                break;
        }
// Schedules a repaint for the root Layout.
        editingLayout.invalidate();
        return true;
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
}