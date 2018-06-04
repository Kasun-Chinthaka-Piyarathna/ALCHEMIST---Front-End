package alchemist.fit.uom.alchemists.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import alchemist.fit.uom.alchemists.R;

public class SplashScreenActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent i = new Intent(SplashScreenActivity.this, GetStartActivity.class);
                startActivity(i);
            }
        }, SPLASH_TIME_OUT);
    }
}
