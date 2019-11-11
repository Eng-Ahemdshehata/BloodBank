package com.ashehata.bloodbank.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

import butterknife.ButterKnife;

public class NotificationSettingsFragment extends BaseFragment {

    private HomeCycleActivity homeCycleActivity;

    public NotificationSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_notification_settings, container, false);

        ButterKnife.bind(this, view);


        // set fragment config
        homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.GONE, View.GONE, View.VISIBLE, View.GONE, getString(R.string.notification_settings));



        return view;
    }


    @Override
    public void onBack() {
        super.onBack();

        //HelperMethod.dialogCheckBackButton(getContext(),baseActivity.baseFragment);

    }
}
