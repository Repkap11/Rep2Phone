package com.repkap11.rep2phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShareUrlReceiver extends BroadcastReceiver {

    private static final String TAG = ShareUrlReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Got Intent");

    }
}
