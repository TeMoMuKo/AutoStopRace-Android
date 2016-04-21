package pl.temomuko.autostoprace.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.temomuko.autostoprace.data.Event;
import pl.temomuko.autostoprace.util.EventUtil;
import pl.temomuko.autostoprace.util.LogUtil;

/**
 * Created by Rafał Naniewicz on 18.02.2016.
 */
public class GpsStatusChangeReceiver extends BroadcastReceiver {

    private final static String TAG = GpsStatusChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "GPS status changed.");
        EventUtil.post(new Event.GpsStatusChanged());
    }
}
