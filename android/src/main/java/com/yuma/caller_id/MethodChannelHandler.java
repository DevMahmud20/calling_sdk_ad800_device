package com.yuma.caller_id;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.yuma.caller_id.usbController.UsbHelper;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MethodChannelHandler extends FlutterActivity implements MethodChannel.MethodCallHandler {
    private final String TAG = MethodChannelHandler.class.getSimpleName();
    private UsbHelper usbHelper;
    private EventChannelHelper connectionListener;
    private EventChannelHelper callingListener;
    private Activity context;

    public MethodChannelHandler(Activity activity, EventChannelHelper connectionListener, EventChannelHelper callingListener){
        this.context = activity;
        this.connectionListener = connectionListener;
        this.callingListener = callingListener;
        usbHelper = new UsbHelper(context, connectionListener, callingListener);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method){
            case "connect":
                usbHelper.initUSB();
                result.success("connected");
                break;
            default:
                result.notImplemented();
                break;
        }
    }
}
