package alchemist.fit.uom.alchemists.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import alchemist.fit.uom.alchemists.BatteryUsageChecker;
import alchemist.fit.uom.alchemists.fragments.NewsFeedFragment2;
import alchemist.fit.uom.alchemists.fragments.NotificationsFragment;
import alchemist.fit.uom.alchemists.fragments.ProfileFragment2;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.interfaces.OnBatteryStateReceiver;


public class TabContentActivity extends AppCompatActivity implements OnBatteryStateReceiver {

    private int fragmentId;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_content);

        BatteryUsageChecker.setOnBatteryStatusReceivedListener(this);
        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        fragmentId = getIntent().getIntExtra("FRAGMENT_ID", 0);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = NewsFeedFragment2.newInstance();
                                break;
                            case R.id.action_item2:
                                selectedFragment = NotificationsFragment.newInstance();
                                break;
                            case R.id.action_item3:
                                selectedFragment = ProfileFragment2.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        if (fragmentId == 2) {
            //Manually displaying the first fragment - one time only
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, ProfileFragment2.newInstance());
            transaction.commit();
        } else {
            //Manually displaying the first fragment - one time only
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, NewsFeedFragment2.newInstance());
            transaction.commit();
        }


    }


    @Override
    public void onStatusReceived(int batteryLevel, int scale, int status, boolean isCharging, boolean usbCharge){
        AlertDialog alertDialog = new AlertDialog.Builder(
                TabContentActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Warning!");

        // Setting Dialog Message
        alertDialog.setMessage(batteryLevel +" "+scale+" "+ status+" "+isCharging+" "+usbCharge);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.app_icon);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            if(level==15) {
                showDialogMessage(level);
            }

        }
    };


    public void showDialogMessage(int level){
        final AlertDialog alertDialog = new AlertDialog.Builder(
                TabContentActivity.this,R.style.MyAlertDialogStyle2).create();
        // Setting Dialog Title
        alertDialog.setTitle("Warning!");
        // Setting Dialog Message
        alertDialog.setMessage("It seems "+level+"% battery life. Its time to limit some features to protect your remaining battery" );
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.app_icon);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
               alertDialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }


}