package com.ashehata.bloodbank.ui.activity;

import android.os.Bundle;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.fragment.SliderFragment;
import com.ashehata.bloodbank.ui.fragment.SplashFragment;

import static com.ashehata.bloodbank.helper.HelperMethod.changeLang;

public class SplashCycleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLang(this,"ar");
        setContentView(R.layout.activity_cycle_splash);

        // Hide status bar
        HelperMethod.hideStatusBar(this);


        if(getIntent().getAction().equals("login")){

            HelperMethod.ReplaceFragment(getSupportFragmentManager()
                    ,new SliderFragment()
                    ,R.id.splash_cycle_activity_fl_splash
                    , false);
        }else {
            // Show Splash Fragment
            HelperMethod.ReplaceFragment(getSupportFragmentManager()
                    ,new SplashFragment()
                    , R.id.splash_cycle_activity_fl_splash
                    , false);
        }

    }
}
