package alchemist.fit.uom.alchemists.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.Utility;
import alchemist.fit.uom.alchemists.adapters.MyVideosAdapter;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import alchemist.fit.uom.alchemists.interfaces.OnViewCommentsListener2;
import alchemist.fit.uom.alchemists.models.MyModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewUserPostsActivity extends AppCompatActivity implements OnViewCommentsListener2 {

    @BindView(R.id.view_user_posts_activity_rv_home)
    AAH_CustomRecyclerView recyclerView;

    private SwipeRefreshLayout mySwipeRefreshLayout;
    private MyVideosAdapter mAdapter;
    private SwipeRefreshLayout parentLayout;
    private Context currentContext;
    private static Date parsedDate;
    private ProgressDialog progressDialog;
    private final List<MyModel> modelList = new ArrayList<>();
    private Picasso p;
    private Picasso picasso;
    private PopupWindow popupWindow;
    private ListView listView;
    private ViewUserPostsActivity.CommentsAdapter commentsAdapter;
    private ArrayList<ViewUserPostsActivity.Item> commentList = new ArrayList<>();
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private String sharedPrefUserId;
    private String postUniqueId;
    private AlchemistsDataSource alchemistsDataSource;
    private String startTime;
    private String[] retrievedBehaviouralData;
    private String context_identification,user_history_identification,
            user_behaviour_identification, simulation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
        sharedPrefUserId = prefs.getString("user_id", "No name defined");
        retrievedBehaviouralData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);

        if(retrievedBehaviouralData!=null) {
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
                if (retrievedBehaviouralData[11] != null) {
                    simulation = retrievedBehaviouralData[11];
                }
            }
        }


        if(user_history_identification.equals("on") || simulation.equals("on")) {
            updateTheme();
        }

        setContentView(R.layout.activity_view_user_posts);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        startTime =dateFormat.format(date);

        MyVideosAdapter.setOnCommentsReceivedListener2(this);
        ButterKnife.bind((ViewUserPostsActivity.this));
        p = Picasso.with(ViewUserPostsActivity.this);
        picasso = Picasso.with(ViewUserPostsActivity.this);
        showProgressDialog();
        new ViewUserPostsActivity.downloadProfilePostAsyncTask().execute(Constants.localAddress+"rest/ureportservice/newsfeed");


        mySwipeRefreshLayout = findViewById(R.id.view_user_posts_activity_swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doYourUpdate();
                    }
                }
        );

        currentContext = ViewUserPostsActivity.this;
        parentLayout = findViewById(R.id.view_user_posts_activity_swiperefresh);

    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(ViewUserPostsActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait ..."); // Setting Message
        // progressDialog.setTitle("Please wait until profile is loaded!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }


    private void doYourUpdate() {
        // TODO implement a refresh

        showProgressDialog();
        mAdapter.getData().clear();
        new ViewUserPostsActivity.downloadProfilePostAsyncTask().execute(Constants.localAddress+"rest/ureportservice/newsfeed");
        mySwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
    }

    @Override
    public void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        recyclerView.stopVideos();
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

    @Override
    public void onCommentsReceived(String postId) {
        postUniqueId = postId;
        showProgressDialog();
        new ViewUserPostsActivity.downloadCommentsAsyncTask().execute(Constants.localAddress+"rest/ureportservice/getAllComments?post_id=" + URLEncoder.encode(postId));
    }

    private class downloadProfilePostAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();


            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String userId = jsonObject.getString("_id");
                        String userEmail = jsonObject.getString("email");
                        String userPassword = jsonObject.getString("password");
                        String userName = jsonObject.getString("name");
                        String userNearestCity = jsonObject.getString("nearest_city");
                        String userMobile = jsonObject.getString("mobile");
                        String userProfileImageUrl = jsonObject.getString("profileImageUrl");
                        String userPosts = jsonObject.getString("posts");
                        //  String userComments = jsonObject.getString("comments");

                        JSONObject jsonObject1 = new JSONObject(userId);
                        String userIDKey = jsonObject1.getString("$oid");

                        JSONArray postsArray = new JSONArray(userPosts);
                        for (int j = 0; j < postsArray.length(); j++) {
                            JSONObject postObject = postsArray.getJSONObject(j);
                            String postId = postObject.getString("post_id");
                            String postText = postObject.getString("post_text");
                            String postType = postObject.getString("post_type");
                            String postFileUrl = postObject.getString("file_url");
                            String postTimeStamp = postObject.getString("time_stamp");

                            String timeDif = checkTimeDiffrence(postTimeStamp);

                            if (userIDKey.equals(sharedPrefUserId)) {
                                modelList.add(new MyModel("VP", userProfileImageUrl, userName, userNearestCity, postId, postText, postType, postFileUrl, timeDif));
                            }
                        }
                    } catch (Exception e) {

                    }

                }


                mAdapter = new MyVideosAdapter(modelList, p);
                //mAdapter.notifyDataSetChanged();
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(ViewUserPostsActivity.this);

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                //todo before setAdapter
                recyclerView.setActivity(ViewUserPostsActivity.this);

                //optional - to play only first visible video
                recyclerView.setPlayOnlyFirstVideo(true); // false by default

                //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
                recyclerView.setCheckForMp4(false); //true by default

                //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
                recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

                recyclerView.setDownloadVideos(true); // false by default

                recyclerView.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

                //extra - start downloading all videos in background before loading RecyclerView
                List<String> urls = new ArrayList<>();
                for (MyModel object : modelList) {
                    if (object.getPostFileUrl() != null && object.getPostFileUrl().contains("http"))
                        urls.add(object.getPostFileUrl());
                }
                recyclerView.preDownload(urls);

                recyclerView.setAdapter(mAdapter);
                //call this functions when u want to start autoplay on loading async lists (eg firebase)
                recyclerView.smoothScrollBy(0, 1);
                recyclerView.smoothScrollBy(0, -1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public String checkTimeDiffrence(String parseStr) {
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd:HH.mm.ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            parsedDate = sdf.parse(parseStr);
        } catch (Exception e) {
            System.out.println(e);
        }
        Timestamp timestamp2 = new java.sql.Timestamp(parsedDate.getTime());
        long milliseconds = timestamp.getTime() - timestamp2.getTime();
        int seconds = (int) milliseconds / 1000;
        // calculate hours minutes and seconds
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;

        if (hours > 0 && hours < 24) {
            if (hours == 1) {
                result = hours + " hour ago";
                return result;
            } else {
                result = hours + " hours ago";
                return result;
            }
        } else if (hours >= 24) {
            result = timestamp2.toString();
            return result;
        } else if (minutes > 0) {
            if (minutes == 1) {
                result = minutes + " minute ago";
                return result;
            } else {
                result = minutes + " minutes ago";
                return result;
            }
        } else {
            result = seconds + " seconds ago";
            return result;
        }
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            commentList.clear();
            commentsAdapter.clear();
            commentsAdapter.notifyDataSetChanged();
        } else {
            finish();
            Intent intent = new Intent(this, TabContentActivity.class);
            intent.putExtra("FRAGMENT_ID", 2);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }
    }

//    public void moveToComment(View view) {
//
//
//        showProgressDialog();
//        new ViewUserPostsActivity.downloadCommentsAsyncTask().execute("http://192.168.1.4:8080/rest/ureportservice/getAllComments?post_id=5c2286c1da2e99873e5303d3_p_1");
//
//    }


    private class downloadCommentsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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


                    commentList.add(new ViewUserPostsActivity.Item(time_stamp, profile_img_url, user_name, comment));
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

    public class CommentsAdapter extends ArrayAdapter<ViewUserPostsActivity.Item> {

        private Context mContext;
        private List<ViewUserPostsActivity.Item> commentList;

        public CommentsAdapter(@NonNull Context context, ArrayList<ViewUserPostsActivity.Item> list) {
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

            ViewUserPostsActivity.Item currentCommment = commentList.get(position);

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
    public void showCommentPopupWindow(ArrayList<ViewUserPostsActivity.Item> cl) {

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the comment_section layout/view
        View customView = inflater.inflate(R.layout.comment_section, null);
        Display display = ViewUserPostsActivity.this.getWindowManager().getDefaultDisplay();
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
        commentsAdapter = new ViewUserPostsActivity.CommentsAdapter(ViewUserPostsActivity.this, cl);
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
                    Toast.makeText(ViewUserPostsActivity.this, "Bad Try", Toast.LENGTH_SHORT).show();
                } else {
                    showProgressDialog();
                    String url = Constants.localAddress+"rest/ureportservice/createComment?PostId=" + URLEncoder.encode(postUniqueId) + "&comment=" + URLEncoder.encode(newComment) + "&UserId=" + URLEncoder.encode(sharedPrefUserId);
                    new ViewUserPostsActivity.createCommentAsyncTask().execute(url);
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
                    Toast.makeText(ViewUserPostsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    break;
                case "Successful":
                    Toast.makeText(ViewUserPostsActivity.this, "commented!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ViewUserPostsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

        }
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
            if(diffHours==0 && diffMinutes<2){
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
                    Toast.makeText(ViewUserPostsActivity.this, diffMinutesNew+" "+diffSecondsNew, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                }
                long viewTime = diffMinutesNew*60+diffSecondsNew;
                String[] retrievedData  = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                long profileTime = 0;

                if (!retrievedData[0].equals("NO EXIST")) {
                    if (retrievedData[2] != null) {
                        profileTime = Long.parseLong(retrievedData[2]);
                    }
                }
                profileTime+=viewTime;

                alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress,null,
                        null,String.valueOf(profileTime),null,
                        null,null,null,
                        null,null,null,null,null);
            }else {
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
                        Toast.makeText(ViewUserPostsActivity.this, String.valueOf(viewRange), Toast.LENGTH_SHORT).show();
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
                                null, null,null,
                                null,null,null,
                                null,null);

                    } else {
                        Toast.makeText(ViewUserPostsActivity.this, "out of time range", Toast.LENGTH_SHORT).show();
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
