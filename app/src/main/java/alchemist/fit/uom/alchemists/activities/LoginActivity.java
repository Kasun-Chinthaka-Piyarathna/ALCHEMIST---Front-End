package alchemist.fit.uom.alchemists.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailEditText, loginPasswordEditText;
    private String loginEmailText, loginPasswordText;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
    }

    public void logining(View view) {
        loginEmailEditText = findViewById(R.id.login_email);
        loginPasswordEditText = findViewById(R.id.login_password);
        loginEmailText = loginEmailEditText.getText().toString();
        loginPasswordText = loginPasswordEditText.getText().toString();
        if (loginPasswordText.equals("") || loginEmailText.equals("")) {
            Toast.makeText(this, "Bad Try", Toast.LENGTH_SHORT).show();
            loginEmailEditText.setText("");
            loginEmailEditText.setError("Field Required!");
            loginPasswordEditText.setText("");
            loginPasswordEditText.setError("Field Required!");
        } else {
            if (isEmailValid(loginEmailText)) {

                String[] authenticateDetails = alchemistsDataSource.getAllDataFromUserDetails(loginEmailText);
                if(!(authenticateDetails[0].equals("NO EXIST"))) {
                    if (authenticateDetails[4].equals(loginPasswordText)) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("email_address", loginEmailText);
                        editor.commit();
                        finish();
                        Toast.makeText(this, "Welcome to Alchemist!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, TabContentActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(this, "Invalid Authentication. Please try it again!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Seems You dont have an account. Please sign up !", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Email Invalid!", Toast.LENGTH_SHORT).show();
                loginEmailEditText.setText("");
                loginEmailEditText.setError("Field Required!");
            }
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

    public void moveToSignUp(View view){
        finish();
        Intent intent = new Intent(this,AddEmailActivity.class);
        startActivity(intent);
    }
}
