package alchemist.fit.uom.alchemists.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;

public class EditProfileActivity extends AppCompatActivity {

    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private Button activityEditProfileEmailAddressBtn;
    private String[] retrievedData;
    private TextView userName, userCity, userMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
        activityEditProfileEmailAddressBtn = findViewById(R.id.activity_edit_profile_email_address);
        activityEditProfileEmailAddressBtn.setText(sharedPrefEmailAddress);
        retrievedData = alchemistsDataSource.getAllDataFromUserDetails(sharedPrefEmailAddress);
        userName = findViewById(R.id.activity_edit_profile_your_name_text_view);
        userCity = findViewById(R.id.activity_edit_profile_nearest_city_text_view);
        userMobile = findViewById(R.id.activity_edit_profile_mobile_text_view);


        if (!retrievedData[0].equals("NO EXIST")) {
            if (retrievedData[0] !=null) {
                userName.setText(retrievedData[0]);
            }
            if (retrievedData[1] != null) {
                userCity.setText(retrievedData[1]);
            }
            if(retrievedData[2] !=null){
                userMobile.setText(retrievedData[2]);
            }

        }

    }

    public void editProfileCancel(View view) {
        finish();
        Intent intent = new Intent(this,TabContentActivity.class);
        intent.putExtra("FRAGMENT_ID", 2);
        startActivity(intent);
    }

    public void addUserName(View view) {
        finish();
        Intent intent = new Intent(this, AddUserNameActivity.class);
        startActivity(intent);
    }

    public void addUserNearestCity(View view) {
        finish();
        Intent intent = new Intent(this, AddNearestCityActivity.class);
        startActivity(intent);
    }

    public void addUserMobile(View view) {
        finish();
        Intent intent = new Intent(this, AddUserMobileActivity.class);
        startActivity(intent);
    }

    public void accountSettings(View view) {
        finish();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
