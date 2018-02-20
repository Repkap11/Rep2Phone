package com.repkap11.rep2phone.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.repkap11.rep2phone.Rep2PhoneApplication;
import com.repkap11.rep2phone.R;
import com.repkap11.rep2phone.activities.SettingsActivity;
import com.repkap11.rep2phone.activities.SignInFractivity;

import java.util.Map;
import java.util.Random;

public class ServiceRep2PhoneNotifications extends FirebaseMessagingService {

    private static final String TAG = ServiceRep2PhoneNotifications.class.getSimpleName();

    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> map = remoteMessage.getData();
        boolean notificationsEnabled = Rep2PhoneApplication.getUserPerferedNotoficationsEnabled(this);
        if (notificationsEnabled) {
            String title = map.get("title");
            String body = map.get("body");
            if (title == null && body == null) {
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                if (notification != null) {
                    title = notification.getTitle();
                    body = notification.getBody();
                }
            }
            sendNotification(title, body, body);
        }
    }

    private void sendNotification(String title, String body, String url) {
        //Log.e(TAG, "Notification Message Title: " + title + " body:" + body);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String ringToneURI = prefs.getString(SettingsActivity.PREF_NOTIFICATIONS_RINGTONE, getResources().getString(R.string.pref_notifications_ringtone_default));
        boolean enableVibrate = prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS_VIBRATE, true);
        boolean showNotification = prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS_NOTIFY, true);
        boolean openUrl = prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS_OPEN_URL, true);
        Uri ringtoneUri = Uri.parse(ringToneURI);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (openUrl) {
            startActivity(intent);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.drawable.rep2phone_notification_icon)
                .setContentTitle(title == null ? getResources().getString(R.string.app_name) : title)
                .setContentText(body)
                .setAutoCancel(false)
                .setSound(ringtoneUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_LOW);
        if (enableVibrate) {
            //notificationBuilder.setVibrate(new long[]{0, 300, 300, 300});
            if (!showNotification) {
                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vib != null) {
                    vib.vibrate(new long[]{50, 150, 150, 150, 150}, -1);
                }
            } else {
                notificationBuilder.setVibrate(new long[]{50, 150, 150, 150, 150});
            }
        } else {
            notificationBuilder.setVibrate(new long[]{0});
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setColor(getResources().getColor(R.color.colorAccent, getTheme()));
        } else {
            notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int randomId = new Random().nextInt();
        notificationManager.notify(randomId, notificationBuilder.build());
        if (!showNotification) {
            notificationManager.cancel(randomId);
        }
    }


}
