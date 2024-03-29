package com.repkap11.rep2phone.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.repkap11.rep2phone.R;
import com.repkap11.rep2phone.Rep2PhoneApplication;
import com.repkap11.rep2phone.UpdateAppTask;
import com.repkap11.rep2phone.activities.SettingsActivity;
import com.repkap11.rep2phone.activities.SignInFractivity;
import com.repkap11.rep2phone.activities.base.Fractivity;

/**
 * Created by paul on 8/8/17.
 */
public class SignInFractivityFragment extends Fractivity.FractivityFragment {
    public static final String STARTING_INTENT_WHICH_LUNCH_GROUP = "com.repkap11.rep2phone.STARTING_INTENT_WHICH_LUNCH_GROUP";
    private static final String TAG = SignInFractivityFragment.class.getSimpleName();
    private static final int REQUEST_CODE_ASK_FOR_WRITE_EXPERNAL_PERMISSION = 44;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SignInButton mSignInButton;
    private boolean mSignedIn;
    private TextView mSignedInDisplayName;
    private Button mButtonOpenDozeSettings;

    @Override
    protected void create(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mSignedIn = mAuth.getCurrentUser() != null;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Log.e(TAG, "Silent sign in failed");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(getActivity(), "Settings selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update:
                startUpdateAppProcedure();
                return true;
        }
        return false;
    }

    private void startUpdateAppProcedure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_FOR_WRITE_EXPERNAL_PERMISSION);
                return;
            }
        }
        continueUpdateAppWithPermissions();
    }


    @Override
    public void requestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        continueUpdateAppWithPermissions();
    }

    private void continueUpdateAppWithPermissions() {
        new UpdateAppTask(getActivity().getApplicationContext(), true).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean showButton = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            if (pm != null && mButtonOpenDozeSettings != null) {
                showButton = true;
                boolean enableButton = pm.isIgnoringBatteryOptimizations(getActivity().getPackageName());
                mButtonOpenDozeSettings.setEnabled(enableButton);
                if (enableButton) {
                    mButtonOpenDozeSettings.setText(getResources().getString(R.string.fractivity_sign_in_doze_no_warning));
                } else {
                    mButtonOpenDozeSettings.setText(getResources().getString(R.string.fractivity_sign_in_doze_warning));
                }
            }
        }
        mButtonOpenDozeSettings.setVisibility(showButton ? View.VISIBLE : View.GONE);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fractivity_sign_in, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fractivity_bar_menu_app_bar_layout);
        mButtonOpenDozeSettings = (Button) rootView.findViewById(R.id.fractivity_sign_in_doze_button);
        mButtonOpenDozeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    try {
                        getActivity().startActivity(intent);
                    } catch (ActivityNotFoundException e) {

                    }
                }
            }
        });
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
        return rootView;
    }

    @Override
    protected void destroyView() {
        mSignInButton = null;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                continueAfterSignIn(false, null);
            }
        });
    }
}
