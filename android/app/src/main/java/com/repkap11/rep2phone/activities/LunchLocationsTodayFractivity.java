package com.repkap11.rep2phone.activities;

import com.repkap11.rep2phone.activities.base.FirebaseAdapterFractivity;
import com.repkap11.rep2phone.fragments.LunchLocationsFractivityFragment;
import com.repkap11.rep2phone.fragments.LunchLocationsTodayFractivityFragment;
import com.repkap11.rep2phone.models.LunchLocation;


public class LunchLocationsTodayFractivity extends FirebaseAdapterFractivity<LunchLocationsFractivityFragment.Holder, LunchLocation> {
    @Override
    protected FirebaseAdapterFragment createFirebaseFragment() {
        return new LunchLocationsTodayFractivityFragment();
    }

}
