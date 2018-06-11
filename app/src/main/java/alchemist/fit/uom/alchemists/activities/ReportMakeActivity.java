package alchemist.fit.uom.alchemists.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import alchemist.fit.uom.alchemists.FileUploadInfo;
import alchemist.fit.uom.alchemists.Fragments.ProfileFragment;
import alchemist.fit.uom.alchemists.R;

public class ReportMakeActivity extends AppCompatActivity {

    // Folder path for Firebase Storage.
    private String storagePathImages = "All_Image_Uploads/";
    private String storagePathVideos = "All_Video_Uploads/";
    // Root Database Name for Firebase Database.
    public static final String Database_Path = "All_Image_Uploads_Database";
    // Creating ImageView.
    private ImageView SelectImage;
    private VideoView selectVideo;
    // Creating URI.
    private Uri imageFilePathUri,videoFilePathUri;
    // Creating StorageReference and DatabaseReference object.
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    // Image request code for onActivityResult() .
    private int Image_Request_Code = 7;
    private int Video_Request_Code = 7;
    private ProgressDialog progressDialog;
    private Button activity_report_photo_button;
    private Button activity_report_video_button;
    private TextView top_bar_post_text;
    private TextView top_bar_pending_text;
    private TextView describeTextInReport;
    private Boolean selectedType;
    private LinearLayout back_button_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_make);
        activity_report_photo_button = findViewById(R.id.activity_report_photo_button);
        activity_report_video_button = findViewById(R.id.activity_report_video_button);
        SelectImage = findViewById(R.id.ShowImageView);
        selectVideo = findViewById(R.id.ShowVideoView);
        top_bar_post_text = findViewById(R.id.top_bar_post_text);
        top_bar_pending_text = findViewById(R.id.top_bar_pending_text);
        describeTextInReport = findViewById(R.id.describeTextInReport);
        back_button_layout = findViewById(R.id.back_button_layout);
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();
        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(ReportMakeActivity.this);
        activity_report_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedType = true;
                // Creating intent.
                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");//image/*
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });
        activity_report_video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedType = false;
                // Creating intent.
                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Video"), Video_Request_Code);
            }
        });
        top_bar_post_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedType) {
                    // Calling method to upload selected image on Firebase storage.
                    UploadImageFileToFirebaseStorage();
                }else {
                    UploadVideoFileToFirebaseStorage();
                }
            }
        });
        back_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ReportMakeActivity.this, TabContentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
         if(selectedType) {
             imageFilePathUri = data.getData();
         }else {
             videoFilePathUri = data.getData();
         }
            try {
                if (selectedType) {
                    // Getting selected image into Bitmap.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePathUri);
                    SelectImage.setVisibility(View.VISIBLE);
                    selectVideo.setVisibility(View.GONE);
                    // Setting up bitmap selected image into ImageView.
                    SelectImage.setImageBitmap(bitmap);
                } else {
                    selectVideo.setVisibility(View.VISIBLE);
                    SelectImage.setVisibility(View.GONE);
                    selectVideo.setVideoURI(videoFilePathUri);
                    selectVideo.requestFocus();
                    selectVideo.start();
                }
                // After selecting image change choose button above text.
                //  ChooseButton.setText("Image Selected");
                top_bar_pending_text.setVisibility(View.GONE);
                top_bar_post_text.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = describeTextInReport.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                describeTextInReport.setLayoutParams(params);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {
        // Checking whether imageFilePathUri Is empty or not.
        if (imageFilePathUri != null) {
            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");
            // Showing progressDialog.
            progressDialog.show();
            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(storagePathImages + System.currentTimeMillis() + "." + GetFileExtension(imageFilePathUri));
            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(imageFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Getting image name from EditText and store into string variable.
                            String TempImageName = describeTextInReport.getText().toString().trim();
                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();
                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            @SuppressWarnings("VisibleForTests")
                            FileUploadInfo fileUploadInfo = new FileUploadInfo("image",TempImageName, taskSnapshot.getDownloadUrl().toString());
                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();
                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(fileUploadInfo);
                            finish();
                            Intent intent = new Intent(ReportMakeActivity.this, TabContentActivity.class);
                            startActivity(intent);

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Hiding the progressDialog.
                            progressDialog.dismiss();
                            // Showing exception erro message.
                            Toast.makeText(ReportMakeActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");
                        }
                    });
        } else {
            Toast.makeText(ReportMakeActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }
    public void UploadVideoFileToFirebaseStorage() {
        // Checking whether imageFilePathUri Is empty or not.
        if (videoFilePathUri != null) {
            // Setting progressDialog Title.
            progressDialog.setTitle("Video is Uploading...");
            // Showing progressDialog.
            progressDialog.show();
            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(storagePathVideos + System.currentTimeMillis() + "." + GetFileExtension(videoFilePathUri));
            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(videoFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Getting image name from EditText and store into string variable.
                            String TempVideoName = describeTextInReport.getText().toString().trim();
                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();
                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Video Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            @SuppressWarnings("VisibleForTests")
                            FileUploadInfo videoUploadInfo = new FileUploadInfo("video",TempVideoName, taskSnapshot.getDownloadUrl().toString());
                            // Getting image upload ID.
                            String VideoUploadId = databaseReference.push().getKey();
                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(VideoUploadId).setValue(videoUploadInfo);
                            finish();
                            Intent intent = new Intent(ReportMakeActivity.this, TabContentActivity.class);
                            startActivity(intent);
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Hiding the progressDialog.
                            progressDialog.dismiss();
                            // Showing exception erro message.
                            Toast.makeText(ReportMakeActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Setting progressDialog Title.
                            progressDialog.setTitle("Video is Uploading...");
                        }
                    });
        } else {
            Toast.makeText(ReportMakeActivity.this, "Please Select Video or Add Video Name", Toast.LENGTH_LONG).show();
        }
    }
}
