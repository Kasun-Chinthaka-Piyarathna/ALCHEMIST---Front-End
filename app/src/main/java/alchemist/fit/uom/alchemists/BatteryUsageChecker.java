package alchemist.fit.uom.alchemists;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import alchemist.fit.uom.alchemists.activities.TabContentActivity;
import alchemist.fit.uom.alchemists.interfaces.OnBatteryStateReceiver;

public class BatteryUsageChecker extends BroadcastReceiver {

    private static OnBatteryStateReceiver listener = null;


    public static void setOnBatteryStatusReceivedListener(TabContentActivity onBatteryStatusReceivedListener) {
        listener = onBatteryStatusReceivedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        //determining the current battery level
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level2 = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 4);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        float batteryPct = level2 / (float) scale;
    //    Toast.makeText(context, String.valueOf(level2) + " " + scale + " " + status + " " + isCharging+ " " + usbCharge, Toast.LENGTH_SHORT).show();



        if (listener != null) {
           // listener.onStatusReceived(level2, scale, status,isCharging,usbCharge);
        }

    }
}
