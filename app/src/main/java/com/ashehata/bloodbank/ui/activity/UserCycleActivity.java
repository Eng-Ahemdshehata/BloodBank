package com.ashehata.bloodbank.ui.activity;

import android.os.Bundle;
import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.fragment.LoginFragment;


public class UserCycleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_user);

            // Start login fragment
            HelperMethod.ReplaceFragment(getSupportFragmentManager()
                    ,new LoginFragment()
                    ,R.id.user_cycle_activity_fl_user_login
                    ,false);
    }
}
