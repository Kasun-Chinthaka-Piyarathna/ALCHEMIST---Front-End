package alchemist.fit.uom.alchemists.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.Utility;

public class GetStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_start);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
    }

    public void getStart(View view){
        finish();
        Intent intent = new Intent(this,AddEmailActivity.class);
        startActivity(intent);
    }
    public void login(View view){
        finish();
        Intent intent = new Intent(this,LoginActivity.class);
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
