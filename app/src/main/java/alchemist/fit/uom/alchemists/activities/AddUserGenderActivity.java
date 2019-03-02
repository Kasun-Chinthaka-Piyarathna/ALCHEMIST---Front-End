package alchemist.fit.uom.alchemists.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.Utility;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;

public class AddUserGenderActivity extends AppCompatActivity {
    private EditText userGenderEditText;
    private String userGenderTxt;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private String sharedPrefUserId;
    private ProgressDialog progressDialog;
    private String[] retrievedData;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private String[] retrievedBehaviouralData;
    private String context_identification,user_history_identification,
            user_behaviour_identification,simulation;


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


        if(user_history_identification.equals("on")|| simulation.equals("on")) {
            updateTheme();
        }

        setContentView(R.layout.activity_add_user_gender);


       // userGenderEditText = findViewById(R.id.activity_add_user_gender_edit_text);
        radioSexGroup =findViewById(R.id.radioSex);

        RadioButton b = (RadioButton) findViewById(R.id.radioMale);
        RadioButton b2 = (RadioButton) findViewById(R.id.radioFemale);
        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);
        String genderCategory = null;
        if(retrievedData!=null) {
            if (!retrievedData[0].equals("NO EXIST")) {
                if (retrievedData[10] != null) {
                    genderCategory = retrievedData[10];
                }
            }
        }
        try {
            if (genderCategory.equals("male")) {
                b.setChecked(true);
            } else {
                b2.setChecked(true);
            }
        }catch (Exception e){
        }


    }

    public void confirmUserGender(View view) {
        // get selected radio button from radioGroup
        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioSexButton = findViewById(selectedId);
        userGenderTxt = radioSexButton.getText().toString();
        if (userGenderTxt.equals("") || userGenderTxt.equals(null)) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show();
        } else {

            alchemistsDataSource.updateUserDetails(null, null, null,
                    sharedPrefEmailAddress, null, null,
                    null, null, null, null, userGenderTxt);

            if (retrievedData[9] != null) {


                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                Pattern p = Pattern.compile(Pattern.quote("D:") + "(.*?)" + Pattern.quote("|"));
                Matcher m = p.matcher(retrievedData[9]);
                Pattern p2 = Pattern.compile(Pattern.quote("|M:") + "(.*?)" + Pattern.quote("|"));
                Matcher m2 = p2.matcher(retrievedData[9]);
                Pattern p3 = Pattern.compile(Pattern.quote("|Y:") + "(.*?)" + Pattern.quote("|"));
                Matcher m3 = p3.matcher(retrievedData[9]);
                if (m.find()) {
                    System.out.println("date: " + m.group(1));
                }
                if (m2.find()) {
                    System.out.println("month: " + m2.group(1));
                }
                if (m3.find()) {
                    System.out.println("year: " + m3.group(1));
                }
                int yourAge = year - Integer.parseInt(m3.group(1));

                if (!(userGenderTxt == null || userGenderTxt.isEmpty() || yourAge == 0)) {
                    dynamicBehaviourBasedOnAgeGender(yourAge, userGenderTxt);
                } else {
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }

            }


            finish();
            Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
            startActivity(intent);
//            showProgressDialog();
//            new AddUserBirthdayActivity.AddUserBirthdayAsyncTask().execute(Constants.localAddress + "rest/ureportservice/updateMobile?user_id=" + URLEncoder.encode(sharedPrefUserId) + "&mobile=" + URLEncoder.encode(userBirthdayTxt));

        }
    }

    public void dynamicBehaviourBasedOnAgeGender(int age, String gender) {
        if (!(gender == null || gender.isEmpty() || age == 0)) {

            if (age <= 19) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 1);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    Utility.setTheme(getApplicationContext(), 6);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            } else if (age > 20 && age < 30) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 2);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    Utility.setTheme(getApplicationContext(), 7);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }

            } else if (age >= 30 && age < 40) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 3);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    Utility.setTheme(getApplicationContext(), 8);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            } else if (age > 40 && age < 55) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 4);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    Utility.setTheme(getApplicationContext(), 9);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            } else if (age >= 55) {
                if (gender.equals("male")) {
                    Utility.setTheme(getApplicationContext(), 5);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    Utility.setTheme(getApplicationContext(), 10);
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }

            } else {

            }

        } else {
            Toast.makeText(AddUserGenderActivity.this, "This feature is only available once you filled your profile data.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(AddUserGenderActivity.this,R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Update is processing!"); // Setting Message
        progressDialog.setTitle("UReport"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
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

    private class AddUserGenderAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            switch (result) {
                case "Updated":
                    finish();
                    Intent intent = new Intent(AddUserGenderActivity.this, EditProfileActivity.class);
                    alchemistsDataSource.updateUserDetails(null, null, null,
                            sharedPrefEmailAddress, null, null,
                            null, null, null, null, userGenderTxt);
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

    public void activityAddUserGenderCancel(View view) {
        finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
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
