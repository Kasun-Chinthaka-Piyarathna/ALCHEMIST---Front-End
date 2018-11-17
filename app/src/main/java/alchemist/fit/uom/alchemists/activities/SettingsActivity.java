package alchemist.fit.uom.alchemists.activities;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import alchemist.fit.uom.alchemists.R;

public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void cancelAccountSettings(View view){
        finish();
        Intent intent = new Intent(this,EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent intent = new Intent(this,EditProfileActivity.class);
        startActivity(intent);
    }
    public void seeContextAwareness(View view){
        finish();
        Intent intent = new Intent(this,DetectContextActivity.class);
        startActivity(intent);
    }
}
