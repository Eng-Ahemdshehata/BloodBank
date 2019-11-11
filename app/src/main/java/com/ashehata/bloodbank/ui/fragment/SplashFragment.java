package com.ashehata.bloodbank.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

import static com.ashehata.bloodbank.helper.HelperMethod.ReplaceFragment;


public class SplashFragment extends BaseFragment {

    private static final int SPLASH_DISPLAY_LENGTH = 1200;
    private boolean rememberMe ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_splash, container, false) ;

        // Load from preference
        rememberMe = SharedPreferencesManger.LoadBoolean(getActivity(),SharedPreferencesManger.REMEMBER);

        setUpActivity();

        // Set delay time for splash
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                if (rememberMe == true){
                    // Sending intent to home activity
                    homeActivity();
                    getActivity().finish();
                }else {
                    // Show Slider Fragment
                    ReplaceFragment(getFragmentManager()
                            ,new SliderFragment ()
                            , R.id.splash_cycle_activity_fl_splash
                            , false);

                }


            }
        }, SPLASH_DISPLAY_LENGTH);



        return view ;
    }

    @Override
    public void onBack() {
        getActivity().finish();
    }

    private void homeActivity() {
        Intent intent = new Intent(getContext(), HomeCycleActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}


