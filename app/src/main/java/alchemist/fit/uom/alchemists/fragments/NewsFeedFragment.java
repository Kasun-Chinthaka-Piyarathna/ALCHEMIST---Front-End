package alchemist.fit.uom.alchemists.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.activities.ReportMakeActivity;
import alchemist.fit.uom.alchemists.activities.ReviewingActivity;
import alchemist.fit.uom.alchemists.activities.TabContentActivity;
import alchemist.fit.uom.alchemists.adapters.MyVideosAdapter;
import alchemist.fit.uom.alchemists.R;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.allattentionhere.autoplayvideos.AAH_VideosAdapter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import alchemist.fit.uom.alchemists.interfaces.InterruptVideoListener;
import alchemist.fit.uom.alchemists.interfaces.OnViewCommentsListener;
import alchemist.fit.uom.alchemists.models.MyModel;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;


public class NewsFeedFragment extends Fragment {


    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;

    private final List<MyModel> modelList = new ArrayList<>();
    private ProgressDialog progressDialog;
    Picasso p;
    static Date parsedDate;
    private SwipyRefreshLayout mySwipeRefreshLayout;
    MyVideosAdapter mAdapter;
    private String startTime;
    private String endTime;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;

    private Context currentContext;
    private SwipyRefreshLayout parentLayout;
    private static InterruptVideoListener listener = null;

    public static NewsFeedFragment newInstance() {
        NewsFeedFragment fragment = new NewsFeedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        startTime = dateFormat.format(date);

        alchemistsDataSource = new AlchemistsDataSource(getActivity());
        alchemistsDataSource.open();
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");

        ButterKnife.bind((Activity) getContext());
        p = Picasso.with(getContext());

        showProgressDialog();
        new NewsFeedFragment.downloadNewsFeedAsyncTask().execute(Constants.localAddress + "rest/ureportservice/newsfeed");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_news_feed = inflater.inflate(R.layout.fragment_news_feed, container, false);
        recyclerView = fragment_news_feed.findViewById(R.id.rv_home);
        mySwipeRefreshLayout = fragment_news_feed.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                                                      @Override
                                                      public void onRefresh(SwipyRefreshLayoutDirection direction) {
                                                          doYourUpdate();
                                                      }
                                                  }
        );
        currentContext = getContext();
        parentLayout = fragment_news_feed.findViewById(R.id.swiperefresh);
        return fragment_news_feed;
    }

    private void doYourUpdate() {
        // TODO implement a refresh

        showProgressDialog();
        mAdapter.getData().clear();
        new NewsFeedFragment.downloadNewsFeedAsyncTask().execute(Constants.localAddress + "rest/ureportservice/newsfeed");
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

    private class downloadNewsFeedAsyncTask extends AsyncTask<String, Void, String> {
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
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String userId = jsonObject.getString("_id");
                    String userEmail = jsonObject.getString("email");
                    String userPassword = jsonObject.getString("password");
                    String userName = jsonObject.getString("name");
                    String userNearestCity = jsonObject.getString("nearest_city");
                    String userMobile = jsonObject.getString("mobile");
                    String userProfileImageUrl = jsonObject.getString("profileImageUrl");


                    try {
                        String userPosts = jsonObject.getString("posts");
                        //  String userComments = jsonObject.getString("comments");

                        JSONArray postsArray = new JSONArray(userPosts);
                        for (int j = 0; j < postsArray.length(); j++) {
                            JSONObject postObject = postsArray.getJSONObject(j);
                            String postId = postObject.getString("post_id");
                            String postText = postObject.getString("post_text");
                            String postType = postObject.getString("post_type");
                            String postFileUrl = postObject.getString("file_url");
                            String postTimeStamp = postObject.getString("time_stamp");

                            String status = postObject.getString("status");
                            if (status.equals("pending")) {
                                Intent intent = new Intent(getActivity(),ReviewingActivity.class);
                                intent.putExtra("post_id",postId);
                                intent.putExtra("post_text",postText);
                                intent.putExtra("post_type",postType);
                                intent.putExtra("file_url",postFileUrl);
                                startActivity(intent);
                            } else {
                                String timeDif = checkTimeDiffrence(postTimeStamp);
                                modelList.add(new MyModel("NF", userProfileImageUrl, userName, userNearestCity, postId, postText, postType, postFileUrl, timeDif));
                            }
                        }
                    } catch (Exception e) {

                    }

                }

                mAdapter = new MyVideosAdapter(modelList, p);
                //mAdapter.notifyDataSetChanged();
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                //todo before setAdapter
                recyclerView.setActivity(getActivity());

                //optional - to play only first visible video
                recyclerView.setPlayOnlyFirstVideo(false); // false by default

                //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
                recyclerView.setCheckForMp4(false); //true by default

                //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
                recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

                recyclerView.setDownloadVideos(true); // false by default

                recyclerView.setVisiblePercent(100); // percentage of View that needs to be visible to start playing

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


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext(), R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait ..."); // Setting Message
        //  progressDialog.setTitle("Please wait until finishing upload!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
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
        long seconds = milliseconds / 1000;
        // calculate hours minutes and seconds
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
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
    public void onDestroy() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateStart = Constants.appStartTime;  //2.15.30 a.m
        String dateStop = dateFormat.format(date);  //2.16.30 a.m
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
            if (diffHours == 0 && diffMinutes < 2) { //0.01.00
                String contextInitialTime = startTime;  //2.16.00
                String contextEndTime = dateStop;//2.16.30
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
                    // Toast.makeText(currentContext, diffMinutesNew + " " + diffSecondsNew, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                }
                long viewTime = diffMinutesNew * 60 + diffSecondsNew; //0.30.00  -->30
                String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                long newsFeedViewTime = 0;

                if (!retrievedData[0].equals("NO EXIST")) {
                    if (retrievedData[0] != null) {
                        newsFeedViewTime = Long.parseLong(retrievedData[0]);
                    }
                }
                newsFeedViewTime += viewTime;

                alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, String.valueOf(newsFeedViewTime),
                        null, null, null,
                        null, null, null,
                        null, null, null,
                        null, null);
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
                        //   Toast.makeText(currentContext, String.valueOf(viewRange), Toast.LENGTH_SHORT).show();
                        String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                        long newsFeedViewTime = 0;
                        if (!retrievedData[0].equals("NO EXIST")) {
                            if (retrievedData[0] != null) {
                                newsFeedViewTime = Long.parseLong(retrievedData[0]);
                            }
                        }
                        newsFeedViewTime += viewRange;

                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, String.valueOf(newsFeedViewTime),
                                null, null, null,
                                null, null, null,
                                null, null,
                                null, null, null);

                    } else {
                        //   Toast.makeText(currentContext, "out of time range", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onPause() {
        super.onPause();
        recyclerView.stopVideos();
    }

    @Override
    public void onResume() {
        super.onResume();
        // recyclerView.playAvailableVideos(0);
    }

}