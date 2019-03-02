package alchemist.fit.uom.alchemists.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.activities.TabContentActivity;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;

import static android.content.Context.MODE_PRIVATE;


public class NotificationsFragment extends Fragment {

    private ProgressDialog progressDialog;
    private Picasso picasso;
    private NotificationsAdapter notificationsAdapter;
    private ArrayList<Item> notificationList = new ArrayList<>();
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private String startTime;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
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

//        if (android.os.Build.VERSION.SDK_INT >= 21) {
//            Window window = getActivity().getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
//        }

        showProgressDialog();
        new NotificationsFragment.getNotificationsAsyncTask().execute(Constants.localAddress + "rest/ureportservice/getAllNotifications");
        picasso = Picasso.with(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_notifications = inflater.inflate(R.layout.fragment_notifications, container, false);
        mySwipeRefreshLayout = fragment_notifications.findViewById(R.id.fragment_notification_swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doYourUpdate();
                    }
                }
        );

        return fragment_notifications;
    }

    private void doYourUpdate() {
        // TODO implement a refresh

        notificationsAdapter.clear();
        notificationList.clear();
        showProgressDialog();
        new NotificationsFragment.getNotificationsAsyncTask().execute(Constants.localAddress + "rest/ureportservice/getAllNotifications");
        mySwipeRefreshLayout.setRefreshing(false);
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

    private class getNotificationsAsyncTask extends AsyncTask<String, Void, String> {
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
                    String time_stamp = jsonObject.getString("time_stamp");
                    String profile_img_url = jsonObject.getString("profile_img_url");
                    String user_name = jsonObject.getString("user_name");
                    String type = jsonObject.getString("type");

                    notificationList.add(new Item(time_stamp, profile_img_url, user_name, type));
                }


                ListView listView = getActivity().findViewById(R.id.fragment_notification_list_view);
                notificationsAdapter = new NotificationsAdapter(getActivity(), notificationList);
                listView.setAdapter(notificationsAdapter);

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

    class Item {
        private String time_stamp;
        private String profile_img_url;
        private String user_name;
        private String type;

        public Item(String time_stamp, String profile_img_url, String user_name,
                    String type) {
            this.time_stamp = time_stamp;
            this.profile_img_url = profile_img_url;
            this.user_name = user_name;
            this.type = type;
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

        public String getType() {
            return type;
        }
    }

    public class NotificationsAdapter extends ArrayAdapter<Item> {

        private Context mContext;
        private List<Item> notificationsList;

        public NotificationsAdapter(@NonNull Context context, ArrayList<Item> list) {
            super(context, 0, list);
            mContext = context;
            notificationsList = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.notification_section, parent, false);

            Item currentNotification = notificationsList.get(position);

            ImageView image = listItem.findViewById(R.id.notification_section_profile);
            picasso.load(currentNotification.getProfile_img_url())
                    .placeholder(R.drawable.s1).error(R.drawable.s1)
                    .into(image);


            if (currentNotification.getType().equals("comment")) {
                TextView t1 = listItem.findViewById(R.id.notification_section_heading);
                String notificationHeading = currentNotification.getUser_name() + " commented to a post.";
                t1.setText(notificationHeading);

            } else {
                TextView t1 = listItem.findViewById(R.id.notification_section_heading);
                String notificationHeading = currentNotification.getUser_name() + " posted in uReport.";
                t1.setText(notificationHeading);
            }

            TextView t2 = listItem.findViewById(R.id.notification_section_time_stamp);
            t2.setText(currentNotification.getTime_stamp());


            return listItem;
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
                   // Toast.makeText(getActivity(), diffMinutesNew + " " + diffSecondsNew, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                }
                long viewTime = diffMinutesNew * 60 + diffSecondsNew;
                String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                long notificationTime = 0;

                if (!retrievedData[0].equals("NO EXIST")) {
                    if (retrievedData[1] != null) {
                        notificationTime = Long.parseLong(retrievedData[1]);
                    }
                }
                notificationTime += viewTime;

                alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                        String.valueOf(notificationTime), null, null,
                        null, null, null,
                        null, null, null,
                        null,null);
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
                        long notificationTime = 0;
                        if (!retrievedData[0].equals("NO EXIST")) {
                            if (retrievedData[1] != null) {
                                notificationTime = Long.parseLong(retrievedData[1]);
                            }
                        }
                        notificationTime += viewRange;

                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                String.valueOf(notificationTime), null, null,
                                null, null, null,
                                null, null,
                                null, null,null);

                    } else {
                       // Toast.makeText(getActivity(), "out of time range", Toast.LENGTH_SHORT).show();
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
    }


    @Override
    public void onResume() {
        super.onResume();
    }


}