package alchemist.fit.uom.alchemists.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alchemist.fit.uom.alchemists.FileUploadInfo;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.RecyclerViewAdapter;
import alchemist.fit.uom.alchemists.activities.ReportMakeActivity;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private LinearLayout makeTestForReport;
    private Button makePhotoToReport;
    private Button makeVideoToReport;
    private int Image_Request_Code = 7;
    private   Uri FilePathUri;
    // Creating DatabaseReference.
    private DatabaseReference databaseReference;
    // Creating RecyclerView.
    private RecyclerView recyclerView;
    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter adapter ;
    // Creating Progress dialog
    private ProgressDialog progressDialog;
    // Root Database Name for Firebase Database.
    private static final String Database_Path = "All_Image_Uploads_Database";
    // Creating List of FileUploadInfo class.
    private List<FileUploadInfo> list = new ArrayList<>();

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_profile = inflater.inflate(R.layout.fragment_profile, container, false);
        makeTestForReport = fragment_profile.findViewById(R.id.makeTestForReport);
        makePhotoToReport = fragment_profile.findViewById(R.id.makePhotoToReport);
        makeVideoToReport = fragment_profile.findViewById(R.id.makeVideoToReport);

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

        // Assign id to RecyclerView.
        recyclerView = fragment_profile.findViewById(R.id.fragment_profile_recyclerView);
        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setNestedScrollingEnabled(true);
        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getActivity());
        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading your profile");
        // Showing progress dialog.
        progressDialog.show();
        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FileUploadInfo fileUploadInfo = postSnapshot.getValue(FileUploadInfo.class);
                    list.add(fileUploadInfo);
                }
                adapter = new RecyclerViewAdapter(getActivity(), list);
                recyclerView.setAdapter(adapter);
                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
        return fragment_profile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FilePathUri);
//                // Setting up bitmap selected image into ImageView.
//                SelectImage.setImageBitmap(bitmap);
//
//                // After selecting image change choose button above text.
//                ChooseButton.setText("Image Selected");
                Intent intent = new Intent(getActivity(),ReportMakeActivity.class);
                intent.putExtra("urlOfSelectedImage",FilePathUri.toString());
                startActivity(intent);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

}