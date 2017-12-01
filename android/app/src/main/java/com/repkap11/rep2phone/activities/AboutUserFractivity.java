package com.repkap11.rep2phone.activities;

import android.os.Bundle;

import com.repkap11.rep2phone.activities.base.BarMenuFractivity;
import com.repkap11.rep2phone.fragments.AboutUserFractivityFragment;

public class AboutUserFractivity extends BarMenuFractivity {
    @Override
    protected AboutUserFractivityFragment createFragment(Bundle savedInstanceState) {
        return new AboutUserFractivityFragment();
    }

}
