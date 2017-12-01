package com.repkap11.rep2phone.activities;

import com.repkap11.rep2phone.activities.base.FirebaseAdapterFractivity;
import com.repkap11.rep2phone.fragments.UsersFractivityFragment;
import com.repkap11.rep2phone.models.User;


public class UsersFractivity extends FirebaseAdapterFractivity<UsersFractivityFragment.Holder, User> {
    @Override
    protected FirebaseAdapterFragment createFirebaseFragment() {
        return new UsersFractivityFragment();
    }
}
