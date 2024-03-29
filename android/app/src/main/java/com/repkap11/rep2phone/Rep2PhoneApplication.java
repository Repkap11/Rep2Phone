package com.repkap11.rep2phone;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.repkap11.rep2phone.activities.SettingsActivity;

/**
 * Created by paul on 8/1/17.
 */


public class Rep2PhoneApplication extends Application {
    public static final String REP2PHONE_PREF_GROUP = "users_prefered_group";

    private static final String TAG = Rep2PhoneApplication.class.getSimpleName();

    public static boolean getUserPerferedNotoficationsEnabled(Context context) {
        SharedPreferences prefsDefault = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean nottificationsEnabled = prefsDefault.getBoolean(SettingsActivity.PREF_NOTIFICATIONS_ENABLED, true);
        return nottificationsEnabled;
    }

    public static String getAppVersionName(Context context) {
        String versionString = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0 /* basic info */);
            versionString = info.versionName;
        } catch (Exception e) {
        }
        return versionString;
    }

    public static void showUpdateDialogIfNecessary(Activity activity) {
        try {
            final SharedPreferences prefs = activity.getSharedPreferences("CHANGELOG", Context.MODE_PRIVATE);
            final int currentVersionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
            boolean hasShownPrevious = prefs.getBoolean("has-shown-prefs-" + (currentVersionCode - 1), false);
            boolean hasShownCurrent = prefs.getBoolean("has-shown-prefs-" + currentVersionCode, false);
            //Log.e(TAG, "Neg previous:" + hasShownPrevious + " current:" + hasShownCurrent);
            //Log.e(TAG, "hasShownPrevious:" + hasShownPrevious + " hasShownCurrent:" + hasShownCurrent);
            SharedPreferences.Editor editor = prefs.edit();
            if (!hasShownPrevious) {
                //If we didn't show them last time, don't show them now.
                //that means the user just insatlled the app, and doesn't need a change log
                editor.putBoolean("has-shown-prefs-" + currentVersionCode, true);
            }
            editor.apply();


            if ((hasShownPrevious && !hasShownCurrent)) {
                final AlertDialog d = new AlertDialog.Builder(activity)
                        .setTitle("Changelog: App Version " + currentVersionCode)
                        .setMessage(activity.getResources().getString(R.string.changelog_message))
                        .setCancelable(false)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                writePrefOnDismiss(prefs, currentVersionCode);
                            }
                        }).setOnKeyListener(new Dialog.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                                // TODO Auto-generated method stub
                                if (keyCode == KeyEvent.KEYCODE_BACK &&
                                        event.getAction() == KeyEvent.ACTION_UP) {
                                    writePrefOnDismiss(prefs, currentVersionCode);
                                    dialogInterface.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        }).show();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void writePrefOnDismiss(SharedPreferences prefs, int currentVersionCode) {
        SharedPreferences.Editor editor = prefs.edit();
        //Log.e(TAG, "Negative button clicked");
        editor.putBoolean("has-shown-prefs-" + currentVersionCode, true);
        editor.apply();
    }

    public static void updateDeviceToken(Context context, boolean add) {
        String userKey = getUserKey(context);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String result) {
                String newInstanceToken = result;

                if (newInstanceToken == null || userKey == null) {
                    Log.e(TAG, "Unable to upload user token user:" + user + " instanceId:" + newInstanceToken);
                    return;
                }
                final SharedPreferences prefs = context.getSharedPreferences(REP2PHONE_PREF_GROUP, Context.MODE_PRIVATE);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference(userKey);

                String previousInstanceToken = prefs.getString("current-instance-id", null);
                if (!newInstanceToken.equals(previousInstanceToken) && previousInstanceToken != null) {
                    userRef.child("devices").child(previousInstanceToken).removeValue();
                }
                if (add) {
                    userRef.child("devices").child(newInstanceToken).setValue("");
                } else {
                    userRef.child("devices").child(newInstanceToken).removeValue(null);
                }

            }
        });
    }

    public static String getGroupKey(Context context) {
        return context.getResources().getString(R.string.root_key);
    }


    public static String getUserKey(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String group = getGroupKey(context);
        if (group == null || user == null) {
            return null;
        }
        return group + "/users/" + user.getUid();
    }
}
