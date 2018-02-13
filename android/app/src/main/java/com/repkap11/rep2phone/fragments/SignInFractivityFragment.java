package com.repkap11.rep2phone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.repkap11.rep2phone.R;
import com.repkap11.rep2phone.Rep2PhoneApplication;
import com.repkap11.rep2phone.activities.SettingsActivity;
import com.repkap11.rep2phone.activities.SignInFractivity;
import com.repkap11.rep2phone.activities.base.Fractivity;

import org.w3c.dom.Text;

/**
 * Created by paul on 8/8/17.
 */
public class SignInFractivityFragment extends Fractivity.FractivityFragment implements GoogleApiClient.OnConnectionFailedListener {
    public static final String STARTING_INTENT_WHICH_LUNCH_GROUP = "com.repkap11.rep2phone.STARTING_INTENT_WHICH_LUNCH_GROUP";
    private static final String TAG = SignInFractivityFragment.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private String mLunchGroup;
    private SignInButton mSignInButton;
    private boolean mSignedIn;
    private TextView mSignedInDisplayName;

    @Override
    protected void create(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mSignedIn = mAuth.getCurrentUser() != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(getActivity(), "Settings selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fractivity_sign_in, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fractivity_bar_menu_app_bar_layout);
        toolbar.setTitle(R.string.fractivity_sign_in_title);
        setHasOptionsMenu(true);
        ((Fractivity) getActivity()).setSupportActionBar(toolbar);

        mSignInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
        mSignedInDisplayName = (TextView) rootView.findViewById(R.id.fractivity_sign_in_display_name);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSignedIn) {
                    signOut();
                } else {
                    signIn();
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(
                getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        continueAfterSignIn(mSignedIn, mSignedIn ? mAuth.getCurrentUser().getDisplayName() : null);
        return rootView;
    }

    @Override
    protected void destroyView() {
        mSignInButton = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        getActivity().startActivityForResult(signInIntent, SignInFractivity.REQUEST_CODE_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            continueAfterSignIn(true, mAuth.getCurrentUser().getDisplayName());

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void continueAfterSignIn(boolean logged_in, String displayName) {
        mSignedIn = logged_in;
//        Intent intent = new Intent(this, LunchGroupsFractivity.class);
//        //Clear the back stack
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        int text;
        if (logged_in) {
            text = R.string.fractivity_sign_out_with_google;
            //Toast.makeText(getActivity(), "Sign in done...", Toast.LENGTH_SHORT).show();
            mSignedInDisplayName.setText(getResources().getString(R.string.fractivity_sign_in_signed_in_as, displayName));
        } else {
            text = R.string.fractivity_sign_in_with_google;
            //Toast.makeText(getActivity(), "Sign out done", Toast.LENGTH_SHORT).show();
            mSignedInDisplayName.setText("");

        }
        if (mSignInButton != null) {
            setGooglePlusButtonText(mSignInButton, text);
        }
        boolean result = Rep2PhoneApplication.getUserPerferedNotoficationsEnabled(getActivity());
        Rep2PhoneApplication.updateDeviceToken(getActivity(), result && logged_in);
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, int buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    private void signOut() {
        Rep2PhoneApplication.updateDeviceToken(getActivity(), false);
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                //Intent intent = new Intent(getActivity(), SignInFractivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                continueAfterSignIn(false, null);
            }
        });
    }

}
