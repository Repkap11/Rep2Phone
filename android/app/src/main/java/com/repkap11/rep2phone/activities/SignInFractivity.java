package com.repkap11.rep2phone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.repkap11.rep2phone.Rep2PhoneApplication;
import com.repkap11.rep2phone.R;
import com.repkap11.rep2phone.activities.base.Fractivity;
import com.repkap11.rep2phone.fragments.SignInFractivityFragment;


public class SignInFractivity extends Fractivity {

    private static final String TAG = SignInFractivity.class.getSimpleName();
    public static final int REQUEST_CODE_SIGN_IN = 43;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Rep2PhoneApplication.showUpdateDialogIfNecessary(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected FractivityFragment createFragment(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String instanceToken = FirebaseInstanceId.getInstance().getToken();
        boolean result = Rep2PhoneApplication.getUserPerferedNotoficationsEnabled(this);
        Rep2PhoneApplication.updateDeviceToken(this, result);
        //Log.e(TAG, "instanceToken:" + instanceToken);

        String groupKey = Rep2PhoneApplication.getGroupKey(this);
        String rootGroupsName = FirebaseDatabase.getInstance().getReference(groupKey).getParent().getKey();
        if (rootGroupsName != null) {
            String expectedRootGroup = getResources().getString(R.string.root_key);
            if (!expectedRootGroup.equals(rootGroupsName)) {
                Log.e(TAG, "Wrong root group expected:" + expectedRootGroup + " got:" + rootGroupsName);
                Toast.makeText(this, "Wrong group Do something?", Toast.LENGTH_SHORT);
                //TODO Do something if you change groups by switing to dev
                //Rep2PhoneApplication.setUserPerferedLunchGroup(this, null);
            }
        }
        if (currentUser != null) {
            //Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
        }
        return new SignInFractivityFragment();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(TAG, "Activity result");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //Log.e(TAG, "Sign in ok");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                ((SignInFractivityFragment) mFragment).firebaseAuthWithGoogle(account);
            } else {
                //Log.e(TAG, "Sign in failed");

                // Google Sign In failed, update UI appropriately
                // ...
            }
        } else {
            //Log.e(TAG, "Activity Wrong code:" + requestCode);

        }
    }
}
