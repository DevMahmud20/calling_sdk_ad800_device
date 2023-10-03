package com.yuma.caller_id;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.yuma.caller_id.usbController.UsbHelper;


abstract  public class BaseActivity extends Activity implements PhoneCallInterface{
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    public static final String ACTION_USB_PERMISSION = "net.xprinter.xprintersdk.USB_PERMISSION";
    private BroadcastReceiver broadcastReceiver;

    PhoneCallInterface phoneCallInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission has not been granted, therefore prompt the user to grant permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        1111);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission has not been granted, therefore prompt the user to grant permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},
                        11);
            }
        }
        phoneCallInterface = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case "PHONE_CALL":
                        Log.e("---------------", "onReceive: " + intent.getStringExtra("Number") );
                        updateState(intent);
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("PHONE_CALL"));
    }

    private void updateState(Intent intent) {
        if(UsbHelper.channelList.get(UsbHelper.selectedChannel).LineStatus.equals("Ring On") ||
                UsbHelper.channelList.get(UsbHelper.selectedChannel).LineStatus.equals("Incoming Call") ||
                UsbHelper.channelList.get(UsbHelper.selectedChannel).LineStatus.equals("Ring Off")){
            try {
                String phone = UsbHelper.channelList.get(UsbHelper.selectedChannel).CallerId.replaceAll("[^\\d.]", "");
                phoneCallInterface.onPhoneCall(true, phone);
            } catch (Exception e) {
                e.printStackTrace();

                String phone = UsbHelper.channelList.get(UsbHelper.selectedChannel).CallerId;
                phoneCallInterface.onPhoneCall(true, phone);
            }
        }else {
            phoneCallInterface.onPhoneCall(false, UsbHelper.channelList.get(UsbHelper.selectedChannel).CallerId);
        }
    }


}
interface PhoneCallInterface {
    void onPhoneCall(boolean isVisible, String phone);
}
