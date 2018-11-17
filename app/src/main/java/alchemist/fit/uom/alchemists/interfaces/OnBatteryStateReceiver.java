package alchemist.fit.uom.alchemists.interfaces;

public interface OnBatteryStateReceiver {

        void onStatusReceived(int batteryLevel, int scale,int  status,boolean isCharging,boolean usbCharge);
}
