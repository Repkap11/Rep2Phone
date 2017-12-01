package com.repkap11.rep2phone.activities;

import android.os.Bundle;

import com.repkap11.rep2phone.activities.base.Fractivity;
import com.repkap11.rep2phone.fragments.EditUserFractivityFragment;

public class EditUserFractivity extends Fractivity<EditUserFractivityFragment> {
    @Override
    protected EditUserFractivityFragment createFragment(Bundle savedInstanceState) {
        return new EditUserFractivityFragment();
    }

}
