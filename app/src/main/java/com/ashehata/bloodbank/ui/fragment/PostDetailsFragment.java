package com.ashehata.bloodbank.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostDetailsFragment extends BaseFragment {

    @BindView(R.id.post_details_fragment_iv_image)
    ImageView postDetailsFragmentIvImage;
    @BindView(R.id.post_details_fragment_tv_title)
    TextView postDetailsFragmentTvTitle;
    @BindView(R.id.post_details_fragment_tv_description)
    TextView postDetailsFragmentTvDescription;

    public String postTitle;
    public String postDescription;
    public String postImage;
    private HomeCycleActivity homeCycleActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        ButterKnife.bind(this, view);

        // set config
        homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, postTitle);


        // Set post details
        postDetailsFragmentTvTitle.setText(postTitle);
        postDetailsFragmentTvDescription.setText(postDescription);
        HelperMethod.onLoadImageFromUrl(postDetailsFragmentIvImage, postImage, getContext(), 0);


        return view;
    }

    @Override
    public void onBack() {
        super.onBack();
    }
}
