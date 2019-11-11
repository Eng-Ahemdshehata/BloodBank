package com.ashehata.bloodbank.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

import static com.ashehata.bloodbank.helper.HelperMethod.ReplaceFragment;

public class AboutAppFragment extends BaseFragment {

    HomeCycleActivity homeCycleActivity ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_about_app, container, false) ;


        homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.GONE,View.GONE,View.VISIBLE,View.GONE,getString(R.string.menu_about_app));



        return view;
    }

    @Override
    public void onBack() {

        //homeCycleActivity.homeFragment();
        super.onBack();
    }

}
