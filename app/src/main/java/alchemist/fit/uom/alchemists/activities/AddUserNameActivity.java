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

public class AddUserNameActivity extends AppCompatActivity {
    private EditText addName;
    private String userName;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private AlchemistsDataSource alchemistsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_name);
        addName = findViewById(R.id.activity_add_user_name_edit_text);
        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");

    }

    public void confirmUserName(View view){
        userName = addName.getText().toString();
        if(userName.equals("")||userName.equals(null)){
            Toast.makeText(this, "Please enter a profile name!", Toast.LENGTH_SHORT).show();
        }else {
            finish();
            Intent intent = new Intent(this, EditProfileActivity.class);
            alchemistsDataSource.updateUserDetails(userName,null,null,sharedPrefEmailAddress,null);
            startActivity(intent);
        }
    }
    public void activityAddUserNameCancel(View view){
        finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}
