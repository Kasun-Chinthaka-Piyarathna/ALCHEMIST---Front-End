package alchemist.fit.uom.alchemists.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;

public class AddUserMobileActivity extends AppCompatActivity {
    private EditText userMobileEditText;
    private String userMobileTxt;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_mobile);


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");


        userMobileEditText = findViewById(R.id.activity_add_user_mobile_edit_text);
        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
    }

    public void confirmUserMobile(View view) {
        userMobileTxt = userMobileEditText.getText().toString();
        if (userMobileTxt.equals("") || userMobileTxt.equals(null)) {
            Toast.makeText(this, "Please enter a valid mobile number!", Toast.LENGTH_SHORT).show();
        } else {
            finish();
            Intent intent = new Intent(this, EditProfileActivity.class);
            alchemistsDataSource.updateUserDetails(null,null,userMobileTxt,sharedPrefEmailAddress,null);
            startActivity(intent);
        }
    }

    public void activityAddUserMobileCancel(View view) {
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
}
