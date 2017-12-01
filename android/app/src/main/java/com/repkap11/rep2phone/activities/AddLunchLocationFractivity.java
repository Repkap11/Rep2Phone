package com.repkap11.rep2phone.activities;

import android.os.Bundle;

import com.repkap11.rep2phone.activities.base.Fractivity;
import com.repkap11.rep2phone.fragments.AddLunchLocationFractivityFragment;

public class AddLunchLocationFractivity extends Fractivity<AddLunchLocationFractivityFragment> {
    @Override
    protected AddLunchLocationFractivityFragment createFragment(Bundle savedInstanceState) {
        return new AddLunchLocationFractivityFragment();
    }

}
