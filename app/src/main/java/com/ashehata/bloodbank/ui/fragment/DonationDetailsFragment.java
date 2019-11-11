package com.ashehata.bloodbank.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.model.donation.DonationData;
import com.ashehata.bloodbank.data.model.login.LoginData;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DonationDetailsFragment extends BaseFragment {

    @BindView(R.id.donation_details_fragment_tv_name)
    TextView donationDetailsFragmentTvName;
    @BindView(R.id.donation_details_fragment_tv_age)
    TextView donationDetailsFragmentTvAge;
    @BindView(R.id.donation_details_fragment_tv_blood_type)
    TextView donationDetailsFragmentTvBloodType;
    @BindView(R.id.donation_details_fragment_tv_bag_number)
    TextView donationDetailsFragmentTvBagNumber;
    @BindView(R.id.donation_details_fragment_tv_hospital)
    TextView donationDetailsFragmentTvHospital;
    @BindView(R.id.donation_details_fragment_tv_hospital_address)
    TextView donationDetailsFragmentTvHospitalAddress;
    @BindView(R.id.donation_details_fragment_tv_phone)
    TextView donationDetailsFragmentTvPhone;
    @BindView(R.id.donation_details_fragment_tv_notes)
    TextView donationDetailsFragmentTvNotes;
    @BindView(R.id.donation_call)
    Button donationCall;
    private HomeCycleActivity homeCycleActivity;

    public DonationDetailsFragment() {
        // Required empty public constructor
    }

    public DonationData data;

    public String donationName;
    public String donationAge;
    public String donationBloodType;
    public String donationBagNumber;
    public String donationHospital;
    public String donationHospitalAddress;
    public String donationPhone;
    public String donationNotes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donation_details, container, false);
        ButterKnife.bind(this, view);


        getPatientData();
        displayData();

        homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.GONE
                , View.GONE
                , View.VISIBLE
                , View.GONE
                , getString(R.string.donation_request) + " : " + donationName);


        return view;
    }

    private void displayData() {
        donationDetailsFragmentTvName.setText(donationName);
        donationDetailsFragmentTvAge.setText(donationAge);
        donationDetailsFragmentTvBagNumber.setText(donationBagNumber);
        donationDetailsFragmentTvBloodType.setText(donationBloodType);
        donationDetailsFragmentTvHospital.setText(donationHospital);
        donationDetailsFragmentTvHospitalAddress.setText(donationHospitalAddress);
        donationDetailsFragmentTvPhone.setText(donationPhone);
        donationDetailsFragmentTvNotes.setText(donationNotes);


    }

    private void getPatientData() {
        if (data != null) {
            donationName = data.getPatientName();
            donationAge = data.getPatientAge();
            donationBagNumber = data.getBagsNum();
            donationBloodType = data.getBloodType().getName();
            donationHospital = data.getHospitalName();
            donationHospitalAddress = data.getHospitalAddress();
            donationPhone = data.getPhone();
            donationNotes = data.getNotes();

        }
    }

    @Override
    public void onBack() {
        super.onBack();
        //homeCycleActivity.homeFragment();
    }

    @OnClick(R.id.donation_call)
    public void onViewClicked() {
        callIntent(donationPhone);

    }

    private void callIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        String permission = android.Manifest.permission.CALL_PHONE;

        //ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)


        if (HelperMethod.checkGrantedPermission(getContext(), permission)) {

            getActivity().startActivity(intent);

        } else {
            HelperMethod.onPermission(getActivity());

        }
    }
}
