package com.ashehata.bloodbank.ui.fragment;

import androidx.fragment.app.Fragment;

import com.ashehata.bloodbank.ui.activity.BaseActivity;

public class BaseFragment extends Fragment {

    public BaseActivity baseActivity;

    public void setUpActivity(){
        baseActivity = (BaseActivity) getActivity();
        baseActivity.baseFragment = this;
    }

    public void onBack(){

        baseActivity.superBackPressed();
    }
}