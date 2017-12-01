package com.repkap11.rep2phone.activities;

import com.repkap11.rep2phone.activities.base.FirebaseAdapterFractivity;
import com.repkap11.rep2phone.fragments.LunchGroupsFractivityFragment;
import com.repkap11.rep2phone.models.LunchGroup;


public class LunchGroupsFractivity extends FirebaseAdapterFractivity<LunchGroupsFractivityFragment.Holder, LunchGroup> {
    @Override
    protected FirebaseAdapterFragment createFirebaseFragment() {
        return new LunchGroupsFractivityFragment();
    }


}
