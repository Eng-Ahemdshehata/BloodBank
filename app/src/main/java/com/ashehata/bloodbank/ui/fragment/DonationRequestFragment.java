package com.ashehata.bloodbank.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.ListModel;
import com.ashehata.bloodbank.data.model.bloodType.BloodTypeData;
import com.ashehata.bloodbank.data.model.cities.Cities;
import com.ashehata.bloodbank.data.model.cities.CitiesData;
import com.ashehata.bloodbank.data.model.donationRequest.DonationRequest;
import com.ashehata.bloodbank.data.model.governorates.GonernoratesData;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;
import com.ashehata.bloodbank.ui.activity.MapsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ashehata.bloodbank.helper.HelperMethod.createToast;
import static com.ashehata.bloodbank.helper.HelperMethod.dismissProgressDialog;
import static com.ashehata.bloodbank.helper.HelperMethod.setSpinner;
import static com.ashehata.bloodbank.helper.HelperMethod.showProgressDialog;

public class DonationRequestFragment extends BaseFragment {

    @BindView(R.id.donation_request_fragment_et_name)
    EditText donationRequestFragmentEtName;
    @BindView(R.id.donation_request_fragment_et_age)
    EditText donationRequestFragmentEtAge;
    @BindView(R.id.donation_request_fragment_spin_blood_type)
    Spinner donationRequestFragmentSpinBloodType;
    @BindView(R.id.donation_request_fragment_et_bags_num)
    EditText donationRequestFragmentEtBagsNum;
    @BindView(R.id.donation_request_fragment_et_hospital_name)
    EditText donationRequestFragmentEtHospitalName;
    @BindView(R.id.donation_request_fragment_iv_location)
    ImageView donationRequestFragmentIvLocation;
    @BindView(R.id.donation_request_fragment_spin_governorate)
    Spinner donationRequestFragmentSpinGovernorate;
    @BindView(R.id.donation_request_fragment_spin_city)
    Spinner donationRequestFragmentSpinCity;
    @BindView(R.id.donation_request_fragment_et_phone)
    EditText donationRequestFragmentEtPhone;
    @BindView(R.id.donation_request_fragment_et_notes)
    EditText donationRequestFragmentEtNotes;
    @BindView(R.id.donation_request_fragment_btn_send)
    Button donationRequestFragmentBtnSend;
    @BindView(R.id.donation_request_fragment_et_location_address)
    EditText donationRequestFragmentEtLocationAddress;
    private HomeCycleActivity homeCycleActivity;
    private GetDataService getDataService;
    String userName, userAge, hospitalName, phoneNumber, bagsNumber, notes, mToken, hospitalAddress;
    int governorate, bloodType, city = 0;
    public String latitude, longitude;
    private List<String> citiesNames= new ArrayList<>();
    private List<CitiesData> cityList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donation_request, container, false);
        ButterKnife.bind(this, view);


        // set fragment config
        homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, getString(R.string.donation_request));

        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);


        // fill spinners with data
        fillSpinners(getActivity());


        return view;
    }

    private void fillSpinners(Activity activity) {
        final Activity mActivity = activity;
        ListModel listModel = SharedPreferencesManger.loadUserListModel(mActivity);
        if (listModel != null) {

            // Fill blood type spinner
            List<String> stringList = new ArrayList<>();
            stringList.add(getString(R.string.choose_blood_type));
            for (int i = 0; i < listModel.getBloodType().size(); i++) {

                BloodTypeData data = listModel.getBloodType().get(i);
                stringList.add(data.getName());
                setSpinner(mActivity, donationRequestFragmentSpinBloodType, stringList);

            }
            // Fill Governators spinner
            List<String> stringList2 = new ArrayList<>();
            stringList2.add(getString(R.string.choose_governorate));
            for (int i = 0; i < listModel.getGovernorates().size(); i++) {
                GonernoratesData data = listModel.getGovernorates().get(i);
                stringList2.add(data.getName());
                setSpinner(mActivity, donationRequestFragmentSpinGovernorate, stringList2);
            }
            setCity();



        }
    }
    private void setCity() {
        donationRequestFragmentSpinGovernorate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i != 0){
                    setCitiesSpinner(i);
                }else {

                    citiesNames.clear();
                    citiesNames.add(getString(R.string.choose_city));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setCitiesSpinner(int id) {
        String idString = String.valueOf(id);
        Call<Cities> citiesCall = getDataService.getCities(idString);
        citiesCall.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                Cities cities = response.body();
                if (cities.getStatus() == 1) {
                    cityList = cities.getData();

                    citiesNames.clear();
                    citiesNames.add(getString(R.string.choose_city));
                    //citiesId.add(0);

                    for (int i = 0; i < cityList.size(); i++) {
                        citiesNames.add(cityList.get(i).getName());
                        //citiesId.add(city.get(i).getId());

                    }

                    setSpinner(getActivity(), donationRequestFragmentSpinCity, citiesNames);
                    dismissProgressDialog();
                }

            }
            @Override
            public void onFailure(Call<Cities> call, Throwable t) {
                dismissProgressDialog();
                //createSnackBar(getView(),getString(R.string.connect_internet),getContext(),null,null);
            }
        });

    }

    @OnClick({R.id.donation_request_fragment_btn_send, R.id.donation_request_fragment_iv_location})
    public void onViewClicked(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.donation_request_fragment_btn_send: {
                sendRequest();
            }
            break;
            case R.id.donation_request_fragment_iv_location: {
                getLocation();
            }
            break;
        }
    }

    private void getLocation() {
        HelperMethod.startActivity(getContext(), MapsActivity.class, "");
    }

    private void sendRequest() {

        latitude = MapsActivity.latitude + "";
        longitude = MapsActivity.longitude + "";
        mToken = SharedPreferencesManger.LoadData(getActivity(), SharedPreferencesManger.USER_API_TOKEN);
        userName = donationRequestFragmentEtName.getText().toString();
        userAge = donationRequestFragmentEtAge.getText().toString();
        bloodType = donationRequestFragmentSpinBloodType.getSelectedItemPosition() ;
        bagsNumber = donationRequestFragmentEtBagsNum.getText().toString();
        userName = donationRequestFragmentEtName.getText().toString();
        hospitalName = donationRequestFragmentEtHospitalName.getText().toString();
        governorate = donationRequestFragmentSpinGovernorate.getSelectedItemPosition() ;
        city = donationRequestFragmentSpinCity.getSelectedItemPosition() ;
        phoneNumber = donationRequestFragmentEtPhone.getText().toString();
        notes = donationRequestFragmentEtNotes.getText().toString();
        hospitalAddress = donationRequestFragmentEtLocationAddress.getText().toString();


        // Validation
        if(userName.isEmpty()){
            createToast(getContext(),getString(R.string.insert_user_name),Toast.LENGTH_SHORT);
        }else if(userAge.isEmpty()){
            createToast(getContext(),getString(R.string.insert_age),Toast.LENGTH_SHORT);
        }else if(bloodType == 0){
            createToast(getContext(),getString(R.string.insert_blood_type),Toast.LENGTH_SHORT);
        }else if(bagsNumber.isEmpty()){
            createToast(getContext(),getString(R.string.insert_num_bags),Toast.LENGTH_SHORT);
        }else if(hospitalName.isEmpty()){
            createToast(getContext(),getString(R.string.insert_hospital_name),Toast.LENGTH_SHORT);
        }else if(hospitalAddress.isEmpty()){
            createToast(getContext(),getString(R.string.insert_hospital_address),Toast.LENGTH_SHORT);
        }else if( latitude.equals("0.0") || longitude.equals("0.0") ){
            createToast(getContext(),getString(R.string.insert_hospital_location),Toast.LENGTH_SHORT);
        }else if (governorate == 0){
            createToast(getContext(),getString(R.string.insert_governorate),Toast.LENGTH_SHORT);
        }
        else if (city == 0){
            createToast(getContext(),getString(R.string.insert_city),Toast.LENGTH_SHORT);
        }
        else if(phoneNumber.isEmpty()){
            createToast(getContext(),getString(R.string.insert_phone),Toast.LENGTH_SHORT);
        }else if(phoneNumber.length() < 11){
            createToast(getContext(),getString(R.string.insert_correct_phone),Toast.LENGTH_SHORT);
        }else if(notes.isEmpty()){
            createToast(getContext(),getString(R.string.insert_notes),Toast.LENGTH_SHORT);
        }else {
            if(HelperMethod.InternetState.isConnected(getContext())){
                createDonationRequest();
            }else
                createToast(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT);
        }
}

    public void createDonationRequest(){

        showProgressDialog(getActivity(), getString(R.string.wait_moment));
        Call<DonationRequest> donationRequestCall = getDataService.makeDonationRequest(mToken, userName, userAge, bloodType+"", bagsNumber,
                hospitalName, hospitalAddress, 1+"", phoneNumber, notes, longitude, latitude);

        donationRequestCall.enqueue(new Callback<DonationRequest>() {
            @Override
            public void onResponse(Call<DonationRequest> call, Response<DonationRequest> response) {
                dismissProgressDialog();

                DonationRequest donationRequest = response.body();
                if (donationRequest.getStatus() == 1) {

                    createToast(getContext(), donationRequest.getMsg(), Toast.LENGTH_SHORT);
                    onBack();

                } else
                    createToast(getContext(), donationRequest.getMsg(), Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<DonationRequest> call, Throwable t) {
                dismissProgressDialog();
                createToast(getContext(), getString(R.string.error), Toast.LENGTH_SHORT);

            }
        });
    }

    @Override
    public void onBack() {

        super.onBack();
        //HelperMethod.dialogCheckBackButton(getContext(), baseActivity.baseFragment);

    }

    @OnClick()
    public void onViewClicked() {

    }
}
