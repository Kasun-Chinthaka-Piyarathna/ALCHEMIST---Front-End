package alchemist.fit.uom.alchemists.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import alchemist.fit.uom.alchemists.models.FileUploadInfo;
import alchemist.fit.uom.alchemists.adapters.MyVideosAdapter;
import alchemist.fit.uom.alchemists.R;
import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsFeedFragment2 extends Fragment {
    // Creating DatabaseReference.
    DatabaseReference databaseReference;
    // Root Database Name for Firebase Database.
    private static final String Database_Path = "All_Image_Uploads_Database";
    // Creating List of FileUploadInfo class.
    List<FileUploadInfo> list = new ArrayList<>();
    private ProgressDialog progressDialog;


    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;

    Picasso p;

    public static NewsFeedFragment2 newInstance() {
        NewsFeedFragment2 fragment = new NewsFeedFragment2();
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


    }
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait ..."); // Setting Message
      //  progressDialog.setTitle("Please wait until news feed is loaded!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_news_feed = inflater.inflate(R.layout.fragment_news_feed2, container, false);

        showProgressDialog();

       // MyVideosAdapter mAdapter = new MyVideosAdapter(modelList, p);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());


        recyclerView = fragment_news_feed.findViewById(R.id.rv_home);

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
                recyclerView.smoothScrollBy(0,1);
                recyclerView.smoothScrollBy(0,-1);

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });


        return fragment_news_feed;
    }

    @Override
    public void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        recyclerView.stopVideos();
    }



}