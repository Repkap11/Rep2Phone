package com.repkap11.rep2phone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DummyActivity extends Activity {

    private static final String TAG = DummyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "No user for sending");
        }
        Intent intent = getIntent();
        if (intent != null) {
            String url = null;
            if (intent.getAction().equals(Intent.ACTION_SEND)) {
                url = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
            if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                url = intent.getDataString();
            }
            if (url != null) {
                Log.e(TAG, "Sending message:" + url);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("/user_group/users/" + user.getUid() + "/notify_pc");
                ref.push().setValue(url);
            }
        }
        finish();
    }
}
