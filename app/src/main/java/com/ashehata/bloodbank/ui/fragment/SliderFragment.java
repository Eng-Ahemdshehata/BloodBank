package com.ashehata.bloodbank.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.adapter.SliderAdapter;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.UserCycleActivity;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SliderFragment extends BaseFragment {


    SliderAdapter sliderAdapter;
    @BindView(R.id.slider_fragment_vp_slider)
    ViewPager sliderFragmentVpSlider;
    @BindView(R.id.slider_fragment_btn_next)
    Button sliderFragmentBtnNext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider, container, false);
        ButterKnife.bind(this, view);


        setUpActivity();

        // Show status bar
        HelperMethod.showStatusBar(getActivity());

        // Make status bar transparent
        StatusBarUtil.setTransparent(getActivity());


        // Set view pager adapter
       setAdapter();
        return view;
    }

    private void setAdapter() {
        sliderAdapter = new SliderAdapter(getContext());
        sliderAdapter.addPage(R.drawable.slider1);
        sliderAdapter.addPage(R.drawable.slider2);


        if (sliderAdapter != null) {
            sliderFragmentVpSlider.setAdapter(sliderAdapter);
        }

    }

    @Override
    public void onBack() {
        getActivity().finish();
    }

    @OnClick(R.id.slider_fragment_btn_next)
    public void onViewClicked() {

        // Start user cycle activity
        HelperMethod.startActivity(getContext(),UserCycleActivity.class,"");
        getActivity().finish();

    }
}
