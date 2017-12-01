package com.repkap11.burger.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.repkap11.burger.Rep2PhoneApplication;

public class ServiceInstanceIdChange extends FirebaseInstanceIdService {
    private static final String TAG = ServiceInstanceIdChange.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        //Log.e(TAG, "Token Changed!");
        boolean notificationsEnabled = Rep2PhoneApplication.getUserPerferedNotoficationsEnabled(this);
        Rep2PhoneApplication.updateDeviceToken(this, notificationsEnabled);
    }
}
