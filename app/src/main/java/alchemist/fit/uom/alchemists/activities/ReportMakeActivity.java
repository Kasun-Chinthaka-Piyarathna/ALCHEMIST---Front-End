package alchemist.fit.uom.alchemists.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import alchemist.fit.uom.alchemists.CameraUtils;
import alchemist.fit.uom.alchemists.models.FileUploadInfo;
import alchemist.fit.uom.alchemists.R;

public class ReportMakeActivity extends AppCompatActivity implements View.OnTouchListener {

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
    private PopupWindow popupWindow;


    ViewGroup editingLayout =null;
    private LinearLayout parentLayout;
    private Context currentContext;




    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";

    private static String imageStoragePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_make);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
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
                showProgressDialog();
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

        editingLayout = findViewById(R.id.activity_report_make);
        addNewImage();
        clickCount = 0;
        parentLayout = findViewById(R.id.activity_report_make);

        currentContext = getApplicationContext();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(ReportMakeActivity.this,R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait ..."); // Setting Message
      //  progressDialog.setTitle("Please wait until finishing upload!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
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
        }  // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // successfully captured the image
                // display it in image view
                // previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
                //  previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
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


    private void addNewImage() {
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.camera_icon2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        iv.setLayoutParams(layoutParams);
        editingLayout.addView(iv, layoutParams);
        iv.setOnTouchListener(this);
    }

    int clickCount;
    private int posX;
    private int posY;
    long startTime = 0 ;
    public boolean onTouch(final View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        int pointerCount = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                posX = X - layoutParams.leftMargin;
                posY = Y - layoutParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                if (startTime == 0){
                    startTime = System.currentTimeMillis();
                }else {
                    if (System.currentTimeMillis() - startTime < 200) {
                        if (CameraUtils.checkPermissions(getApplicationContext())) {
                            captureImage();
                        } else {
                            requestCameraPermission(MEDIA_TYPE_IMAGE);
                        }
                    }
                    startTime = System.currentTimeMillis();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1){
                    LinearLayout.LayoutParams Params = (LinearLayout.LayoutParams) view.getLayoutParams();
                    Params.leftMargin = X - posX;
                    Params.topMargin = Y - posY;
                    Params.rightMargin = -500;
                    Params.bottomMargin = -500;
                    view.setLayoutParams(Params);
                }
                if (pointerCount == 2){
//                    Log.e("TAG","2 finger touched");
//                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                    layoutParams1.width = posX +(int)event.getX();
//                    layoutParams1.height = posY + (int)event.getY();
//                    view.setLayoutParams(layoutParams1);
                }
//Rotation
                if (pointerCount == 3){
//Rotate the ImageView
//                    view.setRotation(view.getRotation() + 10.0f);
                }
                break;
        }
// Schedules a repaint for the root Layout.
        editingLayout.invalidate();
        return true;
    }


    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Restoring store image path from saved instance state
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + IMAGE_EXTENSION)) {
                        // previewCapturedImage();
                    } else if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + VIDEO_EXTENSION)) {
                        //  previewVideo();
                    }
                }
            }
        }
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
                                //    captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Saving stored image path to saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }


    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(ReportMakeActivity.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent intent = new Intent(this,TabContentActivity.class);
        intent.putExtra("FRAGMENT_ID", 3);
        startActivity(intent);
    }
}
