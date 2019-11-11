package com.ashehata.bloodbank.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.ashehata.bloodbank.ui.fragment.BaseFragment;

public class BaseActivity extends AppCompatActivity {

    public BaseFragment baseFragment;

    public void superBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        baseFragment.onBack();
    }
}
