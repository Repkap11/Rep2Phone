package com.repkap11.rep2phone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.repkap11.rep2phone.R;
import com.repkap11.rep2phone.Rep2PhoneApplication;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected FractivityFragment createFragment(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean result = Rep2PhoneApplication.getUserPerferedNotoficationsEnabled(this);
        Rep2PhoneApplication.updateDeviceToken(this, result);
        //Log.e(TAG, "instanceToken:" + instanceToken);

        String groupKey = Rep2PhoneApplication.getGroupKey(this);
        String rootGroupsName = FirebaseDatabase.getInstance().getReference(groupKey).getParent().getKey();
        if (rootGroupsName != null) {
            String expectedRootGroup = getResources().getString(R.string.root_key);
            if (!expectedRootGroup.equals(rootGroupsName)) {
                Log.e(TAG, "Wrong root group expected:" + expectedRootGroup + " got:" + rootGroupsName);
                Toast.makeText(this, "Wrong group Do something?", Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // Google Sign In was successful, authenticate with Firebase
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                ((SignInFractivityFragment) mFragment).firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "Sign in failed:" + data.getExtras());
            }
        } else {
            Log.e(TAG, "Activity Wrong code:" + requestCode);
        }
    }
}
