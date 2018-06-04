package alchemist.fit.uom.alchemists.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alchemist.fit.uom.alchemists.R;

public class AddEmailActivity extends AppCompatActivity {
    private EditText getEmail;
    private String getEmailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_email);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
    }

    public void continueSignUp(View view) {
        getEmail = findViewById(R.id.edit_email);
        getEmailString = getEmail.getText().toString();
        if (isEmailValid(getEmailString)) {
            finish();
            Intent intent = new Intent(this, JoinActivity.class);
            intent.putExtra("passingEmail", getEmailString);
            startActivity(intent);
        } else {
            getEmail.setText("");
            getEmail.setError("Invalid Email Address");
        }
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
            return true;
        else
            return false;
    }
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, GetStartActivity.class);
        startActivity(intent);
    }
    public void moveToLogin(View view){
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
