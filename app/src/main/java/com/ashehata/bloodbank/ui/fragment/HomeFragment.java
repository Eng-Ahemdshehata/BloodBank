package com.ashehata.bloodbank.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.adapter.ViewPagerAdapter;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ashehata.bloodbank.helper.HelperMethod.ReplaceFragment;
import static com.ashehata.bloodbank.helper.HelperMethod.hideAlertDialog;
import static com.ashehata.bloodbank.helper.HelperMethod.showAlertDialog;

public class HomeFragment extends BaseFragment implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.home_fragment_vp_posts_donations)
    ViewPager homeFragmentVpPostsDonations;

    ViewPagerAdapter viewPagerAdapter;
    @BindView(R.id.home_fragment_tl_names)
    TabLayout homeFragmentTlNames;
    @BindView(R.id.home_fragment_float_btn_add)
    FloatingActionButton homeFragmentFloatBtnAdd;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        // Set fragment configuration
        HomeCycleActivity homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.VISIBLE, View.VISIBLE, View.GONE,View.VISIBLE, getString(R.string.menu_home));

        setViewPagerAndTab();
        setUpActivity();



        return view;
    }

    private void setViewPagerAndTab() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addPager(new PostsFragment(), getString(R.string.posts));
        viewPagerAdapter.addPager(new DonationFragment(), getString(R.string.donation));

//        homeFragmentTlNames.addTab(homeFragmentTlNames.newTab().setText(viewPagerAdapter.getPageTitle(1)));
//        homeFragmentTlNames.addTab(homeFragmentTlNames.newTab().setText(viewPagerAdapter.getPageTitle(0)));
        homeFragmentVpPostsDonations.setAdapter(viewPagerAdapter);
        homeFragmentTlNames.setupWithViewPager(homeFragmentVpPostsDonations);

//        homeFragmentVpPostsDonations.setOnPageChangeListener(this);
//        homeFragmentTlNames.setOnTabSelectedListener(this);


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        homeFragmentVpPostsDonations.setCurrentItem(homeFragmentTlNames.getSelectedTabPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        homeFragmentTlNames.setScrollPosition(position, positionOffset, true);

    }

    @Override
    public void onPageSelected(int position) {

        homeFragmentTlNames.selectTab(homeFragmentTlNames.getTabAt(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @OnClick(R.id.home_fragment_float_btn_add)
    public void onViewClicked(View view) {
        //HelperMethod.createSnackBar(view,getString(R.string.connect_internet),getContext(),null,"");

        // create a donation request
        ReplaceFragment(getFragmentManager()
                , new DonationRequestFragment()
                , R.id.home_activity_fl_home
                , false);

    }

    @Override
    public void onBack() {
        //HelperMethod.createToast(getContext(),"dgdf",Toast.LENGTH_SHORT);

        exitApp();
        //super.onBack();
    }
    private void exitApp() {
        final Context mContext = getContext();

        showAlertDialog(mContext
                , getString(R.string.exit_app)
                , null
                , false
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideAlertDialog(mContext);

                    }
                }, getString(R.string.yes), getString(R.string.no));
    }

}
