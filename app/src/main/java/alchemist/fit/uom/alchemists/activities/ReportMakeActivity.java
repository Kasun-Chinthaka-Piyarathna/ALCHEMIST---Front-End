package alchemist.fit.uom.alchemists.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
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
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alchemist.fit.uom.alchemists.CameraUtils;
import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.Utility;
import alchemist.fit.uom.alchemists.VolleyMultipartRequest;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import alchemist.fit.uom.alchemists.models.FileUploadInfo;
import alchemist.fit.uom.alchemists.R;
import de.hdodenhof.circleimageview.CircleImageView;

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
    private Uri imageFilePathUri, videoFilePathUri;
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


    ViewGroup editingLayout = null;
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

    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private String sharedPrefUserId;
    private String sharedPrefBatteryLevel;
    private String sharedPrefUploadFreq;
    private AlchemistsDataSource alchemistsDataSource;
    private String[] retrievedData;
    private String[] retrievedBehaviouralData;
    private String context_identification, user_history_identification,
            user_behaviour_identification, simulation;
    private CircleImageView circleImageView;
    private Picasso picasso;
    private Bitmap bitmapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
        sharedPrefUserId = prefs.getString("user_id", "No name defined");
        sharedPrefBatteryLevel = prefs.getString("battery_level", "No name defined");
        sharedPrefUploadFreq = prefs.getString("upload_preference", "No name defined");
        retrievedBehaviouralData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
        picasso = Picasso.with(ReportMakeActivity.this);
        if (retrievedBehaviouralData != null) {
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


        if (user_history_identification.equals("on") || simulation.equals("on")) {
            updateTheme();
        }


        setContentView(R.layout.activity_report_make);
        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);

        circleImageView = findViewById(R.id.activity_report_make_profile_image_url);
        TextView textView = findViewById(R.id.activity_report_make_user_name);


        if (!retrievedData[0].equals("NO EXIST")) {
            if (retrievedData[0] != null) {
                textView.setText(retrievedData[0]);
            }

            if (retrievedData[5] != null) {
                picasso.load(retrievedData[5])
                        .placeholder(R.drawable.s1).error(R.drawable.s1)
                        .into(circleImageView);
            }
        }

        activity_report_photo_button = findViewById(R.id.activity_report_photo_button);
        activity_report_video_button = findViewById(R.id.activity_report_video_button);

        String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
        if (!retrievedData[0].equals("NO EXIST")) {
            int photoFrequency = 0;
            if (retrievedData[3] != null) {
                photoFrequency = Integer.parseInt(retrievedData[3]);
            }
            int videoFrequency = 0;
            if (retrievedData[4] != null) {
                videoFrequency = Integer.parseInt(retrievedData[4]);
            }
            // Toast.makeText(ReportMakeActivity.this, String.valueOf(photoFrequency) + " " + String.valueOf(videoFrequency), Toast.LENGTH_SHORT).show();
            if (user_behaviour_identification.equals("on")) {
                if (!(photoFrequency == videoFrequency)) {
                    if (Math.max(photoFrequency, videoFrequency) == photoFrequency) {
                        activity_report_video_button.getBackground().setAlpha(100);
                    } else {
                        activity_report_photo_button.getBackground().setAlpha(100);
                    }
                }
            }
            if (simulation.equals("on")) {
                if (sharedPrefUploadFreq.equals("Image Upload")) {
                    activity_report_video_button.getBackground().setAlpha(100);
                }
                if (sharedPrefUploadFreq.equals("Video Upload")) {
                    activity_report_photo_button.getBackground().setAlpha(100);
                }
            }
        }
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
                    //  UploadImageFileToFirebaseStorage();
                    uploadBitmap(bitmapImage);
                } else {
                    uploadVideo(videoFilePathUri);
//                    UploadVideoFileToFirebaseStorage();
                }
            }
        });
        back_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ReportMakeActivity.this, TabContentActivity.class);
                intent.putExtra("FRAGMENT_ID", 2);
                startActivity(intent);
            }
        });

        editingLayout = findViewById(R.id.activity_report_make);
        addNewImage();
        clickCount = 0;
        parentLayout = findViewById(R.id.activity_report_make);
        currentContext = getApplicationContext();

        if (sharedPrefBatteryLevel.equals("critical")) {
            activity_report_video_button.setVisibility(View.GONE);
        } else {
            activity_report_video_button.setVisibility(View.VISIBLE);
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(ReportMakeActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait ..."); // Setting Message
        //  progressDialog.setTitle("Please wait until finishing upload!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    private void uploadBitmap(final Bitmap bitmap) {
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            Toast.makeText(getApplicationContext(),"Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(ReportMakeActivity.this,TabContentActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(currentContext, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "5c2f93f6c4554504705ed6af");
                params.put("location", Constants.location);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void uploadVideo(final Uri videoFilePathUri) {
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            //JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(),"Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(ReportMakeActivity.this,TabContentActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ReportMakeActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(ReportMakeActivity.this,TabContentActivity.class);
                            startActivity(intent);
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "5c2f93f6c4554504705ed6af");
                params.put("location", Constants.location);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart(System.currentTimeMillis() + ".mp4", UploadHelper.getFileDataFromDrawable(ReportMakeActivity.this, videoFilePathUri)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public static class UploadHelper {

        /**
         * Turn drawable resource into byte array.
         *
         * @param context parent context
         * @param id      drawable resource id
         * @return byte array
         */
        public static byte[] getFileDataFromDrawable(Context context, int id) {
            Drawable drawable = ContextCompat.getDrawable(context, id);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

        /**
         * Turn drawable into byte array.
         *
         * @return byte array
         */
        public static  byte[] getFileDataFromDrawable(Context context, Uri uri) {
            // Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                InputStream iStream =   context.getContentResolver().openInputStream(uri);
                int bufferSize = 2048;
                byte[] buffer = new byte[bufferSize];

                // we need to know how may bytes were read to write them to the byteBuffer
                int len = 0;
                if (iStream != null) {
                    while ((len = iStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (selectedType) {
                imageFilePathUri = data.getData();
            } else {
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
                    bitmapImage = bitmap;

//                    arr[0] = newsfeed_time;
//                    arr[1] = notification_time;
//                    arr[2] = profile_time;
//                    arr[3] = select_photo_count;
//                    arr[4] = select_video_coount;
//                    arr[5] = current_context;
                    String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                    if (!retrievedData[0].equals("NO EXIST")) {
                        int photoFrequency = 0;
                        if (retrievedData[3] != null) {
                            photoFrequency = Integer.parseInt(retrievedData[3]);
                        }
                        photoFrequency += 1;
                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress,
                                null,
                                null,
                                null, String.valueOf(photoFrequency),
                                null, null, null,
                                null, null,
                                null, null, null);
                    }

                } else {
                    String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                    if (!retrievedData[0].equals("NO EXIST")) {
                        int videoFrequency = 0;
                        if (retrievedData[4] != null) {
                            videoFrequency = Integer.parseInt(retrievedData[4]);
                        }
                        videoFrequency += 1;
                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress,
                                null,
                                null, null,
                                null, String.valueOf(videoFrequency),
                                null, null,
                                null, null, null,
                                null, null);
                    }


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
                previewCapturedImage();
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
                previewVideo();
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

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    private void previewCapturedImage() {
        try {

            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            SelectImage.setVisibility(View.VISIBLE);
            selectVideo.setVisibility(View.GONE);
            // Setting up bitmap selected image into ImageView.
            SelectImage.setImageBitmap(bitmap);
            // After selecting image change choose button above text.
            //  ChooseButton.setText("Image Selected");
            top_bar_pending_text.setVisibility(View.GONE);
            top_bar_post_text.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = describeTextInReport.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            describeTextInReport.setLayoutParams(params);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     * Displaying video in VideoView
     */
    private void previewVideo() {
        try {

            SelectImage.setVisibility(View.GONE);
            selectVideo.setVisibility(View.VISIBLE);
            selectVideo.setVideoPath(imageStoragePath);
            // start playing
            selectVideo.requestFocus();
            selectVideo.seekTo(100);
            selectVideo.start();
            // After selecting image change choose button above text.
            //  ChooseButton.setText("Image Selected");
            top_bar_pending_text.setVisibility(View.GONE);
            top_bar_post_text.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = describeTextInReport.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            describeTextInReport.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
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
                            FileUploadInfo fileUploadInfo = new FileUploadInfo("image", TempImageName, taskSnapshot.getDownloadUrl().toString());
                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();
                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(fileUploadInfo);

                            String imageDownloadUrl = taskSnapshot.getDownloadUrl().toString();
                            new ReportMakeActivity.CreatePostAsyncTask().execute(Constants.localAddress + "rest/ureportservice/createPost?UserId=" + URLEncoder.encode(sharedPrefUserId) + "&PostText=" + URLEncoder.encode(TempImageName) + "&PostType=" + URLEncoder.encode("image") + "&fileUrl=" + URLEncoder.encode(imageDownloadUrl));


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
                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Video Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            @SuppressWarnings("VisibleForTests")
                            FileUploadInfo videoUploadInfo = new FileUploadInfo("video", TempVideoName, taskSnapshot.getDownloadUrl().toString());
                            // Getting image upload ID.
                            String VideoUploadId = databaseReference.push().getKey();
                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(VideoUploadId).setValue(videoUploadInfo);
                            String videoDownloadUrl = taskSnapshot.getDownloadUrl().toString();
                            new ReportMakeActivity.CreatePostAsyncTask().execute(Constants.localAddress + "rest/ureportservice/createPost?UserId=" + URLEncoder.encode(sharedPrefUserId) + "&PostText=" + URLEncoder.encode(TempVideoName) + "&PostType=" + URLEncoder.encode("video") + "&fileUrl=" + URLEncoder.encode(videoDownloadUrl));


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

    private class CreatePostAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            switch (result) {
                case "Successful":
                    finish();
                    Intent intent = new Intent(ReportMakeActivity.this, TabContentActivity.class);
                    startActivity(intent);
                    break;
                case "404":
                    System.out.println("Something went wrong!");
                    break;
                default:
                    System.out.println("Something went wrong!");
            }
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
    long startTime = 0;

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
                if (startTime == 0) {
                    startTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - startTime < 200) {
                        if (CameraUtils.checkPermissions(getApplicationContext())) {
                             captureImage();
//                            captureVideo();
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
                if (pointerCount == 1) {
                    LinearLayout.LayoutParams Params = (LinearLayout.LayoutParams) view.getLayoutParams();
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


    /**
     * Capturing Camera Image will launch camera app requested image capture
     */


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
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, TabContentActivity.class);
        intent.putExtra("FRAGMENT_ID", 2);
        startActivity(intent);
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
