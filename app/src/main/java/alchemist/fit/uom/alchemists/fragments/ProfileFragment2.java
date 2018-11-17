package alchemist.fit.uom.alchemists.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import alchemist.fit.uom.alchemists.models.FileUploadInfo;
import alchemist.fit.uom.alchemists.adapters.MyVideosAdapter;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.activities.EditProfileActivity;
import alchemist.fit.uom.alchemists.activities.ReportMakeActivity;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class ProfileFragment2 extends Fragment {

    private LinearLayout makeTestForReport;
    private Button makePhotoToReport;
    private Button makeVideoToReport;
    private ImageButton personalSettings;
    private int Image_Request_Code = 7;
    private Uri FilePathUri;
    // Creating DatabaseReference.
    private DatabaseReference databaseReference;
    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter adapter;
    // Creating Progress dialog
    private ProgressDialog progressDialog;
    // Root Database Name for Firebase Database.
    private static final String Database_Path = "All_Image_Uploads_Database";
    // Creating List of FileUploadInfo class.
    private List<FileUploadInfo> list = new ArrayList<>();
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private String[] retrievedData;



    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;

    Picasso p;

    public static ProfileFragment2 newInstance() {
        ProfileFragment2 fragment = new ProfileFragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
        ButterKnife.bind((Activity) getContext());

        p = Picasso.with(getContext());

        alchemistsDataSource = new AlchemistsDataSource(getActivity());
        alchemistsDataSource.open();
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);


    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait ..."); // Setting Message
       // progressDialog.setTitle("Please wait until profile is loaded!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragment_profile = inflater.inflate(R.layout.fragment_profile2, container, false);
        makeTestForReport = fragment_profile.findViewById(R.id.makeTestForReport);
        makePhotoToReport = fragment_profile.findViewById(R.id.makePhotoToReport);
        makeVideoToReport = fragment_profile.findViewById(R.id.makeVideoToReport);
        personalSettings = fragment_profile.findViewById(R.id.personal_settings);
        TextView t1 = fragment_profile.findViewById(R.id.fragment_profile2_profile_name);
        TextView t2 = fragment_profile.findViewById(R.id.fragment_profile2_user_location);
        TextView t3 = fragment_profile.findViewById(R.id.fragment_profile2_mobile_number);


        if (!retrievedData[0].equals("NO EXIST")) {
            if (retrievedData[0] !=null) {
                t1.setText(retrievedData[0]);
            }
            if (retrievedData[1] != null) {
                t2.setText(retrievedData[1]);
            }
            if(retrievedData[2] !=null){
                t3.setText(retrievedData[2]);
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

        showProgressDialog();

        // MyVideosAdapter mAdapter = new MyVideosAdapter(modelList, p);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());


        recyclerView = fragment_profile.findViewById(R.id.fragment_profile_rv_home);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //todo before setAdapter
        recyclerView.setActivity((Activity) getContext());

        //optional - to play only first visible video
        recyclerView.setPlayOnlyFirstVideo(true); // false by default

        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        recyclerView.setCheckForMp4(false); //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

        recyclerView.setDownloadVideos(true); // false by default

        recyclerView.setVisiblePercent(80); // percentage of View that needs to be visible to start playing

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    /* */

                    /* */
                    FileUploadInfo fileUploadInfo = postSnapshot.getValue(FileUploadInfo.class);
                    list.add(fileUploadInfo);
                }

                MyVideosAdapter adapter = new MyVideosAdapter(list, p);

                recyclerView.setAdapter(adapter);
                //call this functions when u want to start autoplay on loading async lists (eg firebase)
                recyclerView.smoothScrollBy(0, 1);
                recyclerView.smoothScrollBy(0, -1);


                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

        });


        ScrollView parentScroll=fragment_profile.findViewById(R.id.parent_scrollview);
        AAH_CustomRecyclerView childScroll= fragment_profile.findViewById(R.id.fragment_profile_rv_home);

        parentScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG,"PARENT TOUCH");
                fragment_profile.findViewById(R.id.fragment_profile_rv_home).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        childScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {
                Log.v(TAG,"CHILD TOUCH");
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });



        return fragment_profile;
    }

    @Override
    public void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        recyclerView.stopVideos();
    }
}