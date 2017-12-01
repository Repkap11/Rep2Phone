package com.repkap11.rep2phone.activities;

import android.os.Bundle;

import com.repkap11.rep2phone.activities.base.Fractivity;
import com.repkap11.rep2phone.fragments.AddLunchGroupFractivityFragment;

public class AddLunchGroupFractivity extends Fractivity<Fractivity.FractivityFragment> {
    @Override
    protected AddLunchGroupFractivityFragment createFragment(Bundle savedInstanceState) {
        return new AddLunchGroupFractivityFragment();
    }

}
