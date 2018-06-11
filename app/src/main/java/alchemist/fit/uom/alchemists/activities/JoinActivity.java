package alchemist.fit.uom.alchemists.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;

public class JoinActivity extends AppCompatActivity {
    private TextView email;
    private EditText password;
    private EditText confirmPassword;
    private String emailText;
    private String passwordText;
    private String confirmPasswordText;
    public static String passEmail;
    private AlchemistsDataSource alchemistsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();
        Bundle bundle = getIntent().getExtras();
        passEmail = bundle.getString("passingEmail");
        email = findViewById(R.id.email);
        email.setText(passEmail);
    }

    public void joinnow(View view) {
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmpassword);
        emailText = passEmail;
        passwordText = password.getText().toString();
        confirmPasswordText = confirmPassword.getText().toString();
        if (emailText.equals("") || passwordText.equals("") || confirmPasswordText.equals("")) {
            Toast.makeText(this, "Bad Try", Toast.LENGTH_SHORT).show();
            email.setText(passEmail);
            password.setText("");
            password.setError("Field Required!");
            confirmPassword.setText("");
            confirmPassword.setError("Field Required!");
        } else {
            if (isEmailValid(emailText)) {
                if (passwordText.equals(confirmPasswordText)) {
                    alchemistsDataSource.insertDataUserDetails("Jhon Doe",null,null,emailText,passwordText);
                    finish();
                    Intent intent = new Intent(JoinActivity.this, TabContentActivity.class);
                    startActivity(intent);
                } else {
                    password.setText("");
                    password.setError("Field Required!");
                    confirmPassword.setText("");
                    confirmPassword.setError("Field Required!");
                    Toast.makeText(JoinActivity.this, "Password Didn't Match", Toast.LENGTH_SHORT).show();
                }
            } else {
                email.setText("");
                email.setError("Invalid Email Address");
                Toast.makeText(JoinActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
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

    public void moveToSignIn(View view) {
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, AddEmailActivity.class);
        startActivity(intent);
    }
}
