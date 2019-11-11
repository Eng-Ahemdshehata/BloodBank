package com.ashehata.bloodbank.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.DateModel;
import com.ashehata.bloodbank.data.model.ListModel;
import com.ashehata.bloodbank.data.model.bloodType.BloodTypeData;
import com.ashehata.bloodbank.data.model.cities.Cities;
import com.ashehata.bloodbank.data.model.cities.CitiesData;
import com.ashehata.bloodbank.data.model.createAccount.City;
import com.ashehata.bloodbank.data.model.createAccount.Client;
import com.ashehata.bloodbank.data.model.governorates.GonernoratesData;
import com.ashehata.bloodbank.data.model.login.LoginData;
import com.ashehata.bloodbank.data.model.updateProfile.UpdateProfile;
import com.ashehata.bloodbank.data.model.updateProfile.UpdateProfileData;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

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

public class EditProfileFragment extends BaseFragment {

    @BindView(R.id.user_cycle_activity_fl_head)
    FrameLayout userCycleActivityFlHead;
    @BindView(R.id.create_account_fragment_et_name)
    EditText createAccountFragmentEtName;
    @BindView(R.id.create_account_fragment_et_mail)
    EditText createAccountFragmentEtMail;
    @BindView(R.id.create_account_fragment_et_birth_date)
    LinearLayout createAccountFragmentEtBirthDate;
    @BindView(R.id.create_account_fragment_spin_blood_type)
    Spinner createAccountFragmentSpinBloodType;
    @BindView(R.id.create_account_fragment_et_the_latest_date_of_donation)
    LinearLayout createAccountFragmentEtTheLatestDateOfDonation;
    @BindView(R.id.create_account_fragment_spin_city)
    Spinner createAccountFragmentSpinCity;
    @BindView(R.id.create_account_fragment_et_phone)
    EditText createAccountFragmentEtPhone;
    @BindView(R.id.create_account_fragment_et_password)
    EditText createAccountFragmentEtPassword;
    @BindView(R.id.create_account_fragment_et_password_confirm)
    EditText createAccountFragmentEtPasswordConfirm;
    @BindView(R.id.create_account_fragment_btn_create)
    Button createAccountFragmentBtnCreate;
    @BindView(R.id.create_account_fragment_spin_governorate)
    Spinner createAccountFragmentSpinGovernorate;
    @BindView(R.id.create_account_fragment_tv_birth_date_txt)
    TextView createAccountFragmentTvBirthDateTxt;
    @BindView(R.id.create_account_fragment_tv_latest_date_of_donation_txt)
    TextView createAccountFragmentTvLatestDateOfDonationTxt;

    private GetDataService getDataService;
    private HomeCycleActivity homeCycleActivity;
    private List<String> bloodTypeNames =new ArrayList<>();
    private List<String>  governoratesNames=new ArrayList<>();
    private List<String> citiesNames = new ArrayList<>();
    private String name;
    private String mail;
    private DateModel birthDate = new DateModel();
    private String birthDateTxt;
    private DateModel lastDateOfDonation = new DateModel();
    private String lastDateOfDonationTxt;
    private String phone;
    private String password;
    private String passwordConfirm;
    private List<LoginData> governorate, city, bloodType;
    private List<Integer> governorateId= new ArrayList<>();
    private List<Integer> cityID=new ArrayList<>();
    private List<Integer> bloodTypeId=new ArrayList<>();
    private String mToken="";
    private Integer bloodClientId=0;
    private Integer GovId=0;
    private int cityId=0;
    private List<CitiesData> cityDataList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_create_account, container, false) ;
        ButterKnife.bind(this, view);
        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);


        setFragmentConfiguration();

        displayAccountInformation();

        return view;
    }
    private void setCitiesSpinner(int id) {
        String idString = String.valueOf(id);
        Call<Cities> citiesCall = getDataService.getCities(idString);
        citiesCall.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                Cities cities = response.body();
                if (cities.getStatus() == 1) {
                    cityDataList = cities.getData();

                    citiesNames.clear();
                    citiesNames.add(getString(R.string.choose_city));
                    cityID.add(0);

                    for (int i = 0; i < cityDataList.size(); i++) {
                        citiesNames.add(cityDataList.get(i).getName());
                        cityID.add(cityDataList.get(i).getId());

                    }

                    setSpinner(getActivity(), createAccountFragmentSpinCity, citiesNames);
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

    private void setFragmentConfiguration() {

        // Set fragment configuration
        homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.GONE, View.GONE, View.GONE,View.GONE, getString(R.string.menu_account));
        userCycleActivityFlHead.setVisibility(View.GONE);
        createAccountFragmentBtnCreate.setText(getString(R.string.modify));
    }

    private void displayAccountInformation() {
        // Get data from preference
        LoginData data = SharedPreferencesManger.loadUserData(getActivity());
        String mPassword = SharedPreferencesManger.LoadData(getActivity(), SharedPreferencesManger.USER_PASSWORD);

        // set user info in required fields
        Client client = data.getClient();
        createAccountFragmentEtName.setText(client.getName());
        createAccountFragmentEtPhone.setText(client.getPhone());
        createAccountFragmentEtMail.setText(client.getEmail());
        createAccountFragmentEtPassword.setText(mPassword);
        createAccountFragmentEtPasswordConfirm.setText(mPassword);
        createAccountFragmentTvBirthDateTxt.setText(client.getBirthDate());
        createAccountFragmentTvLatestDateOfDonationTxt.setText(client.getDonationLastDate());
        birthDate.setDateTxt(client.getBirthDate());
        lastDateOfDonation.setDateTxt(client.getDonationLastDate());

        ListModel listModel = SharedPreferencesManger.loadUserListModel(getActivity());
        List<BloodTypeData> bloodTypeDataList = listModel.getBloodType();

        bloodTypeNames.add(getString(R.string.choose_blood_type));
        bloodTypeId.add(0);

        for (int i = 0 ; i < bloodTypeDataList.size()  ;i++){
            bloodTypeNames.add(bloodTypeDataList.get(i).getName());
            bloodTypeId.add(bloodTypeDataList.get(i).getId());

        }
        List<GonernoratesData> gonernoratesDataList = listModel.getGovernorates();


        governoratesNames.add(getString(R.string.choose_governorate));
        governorateId.add(0);
        for (int i = 0 ; i < gonernoratesDataList.size()  ;i++){
            governoratesNames.add(gonernoratesDataList.get(i).getName());
            governorateId.add(gonernoratesDataList.get(i).getId());

        }


        setSpinner(getActivity(),createAccountFragmentSpinBloodType,bloodTypeNames);
        //setSpinner(getActivity(),createAccountFragmentSpinCity,citiesNames);
        setSpinner(getActivity(),createAccountFragmentSpinGovernorate,governoratesNames);

        bloodClientId = client.getBloodType().getId();

        createAccountFragmentSpinBloodType.setSelection(bloodTypeId.get(bloodClientId));

        if(client.getCity() != null){
            GovId = Integer.parseInt(client.getCity().getGovernorateId());
        }

        //Toast.makeText(getContext(), ""+GovId, Toast.LENGTH_SHORT).show();

        createAccountFragmentSpinGovernorate.setSelection(governorateId.get(GovId));

        //Toast.makeText(getContext(), ""+GovId, Toast.LENGTH_SHORT).show();

        createAccountFragmentSpinCity.setSelection(Integer.valueOf(client.getCityId()));

        createAccountFragmentSpinGovernorate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    //citiesNames.add(getString(R.string.choose_city));
                    createAccountFragmentSpinCity.setVisibility(View.GONE);
                }else {
                    createAccountFragmentSpinCity.setVisibility(View.VISIBLE);
                    setCitiesSpinner(governorateId.get(i));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    @OnClick({R.id.create_account_fragment_et_birth_date
            , R.id.create_account_fragment_et_the_latest_date_of_donation
            , R.id.create_account_fragment_btn_create})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.create_account_fragment_btn_create:
                updateProfile();
                break;
            case R.id.create_account_fragment_et_birth_date:
                HelperMethod.showCalender(getContext(), getString(R.string.birth_date), createAccountFragmentTvBirthDateTxt, birthDate);
                break;
            case R.id.create_account_fragment_et_the_latest_date_of_donation:
                HelperMethod.showCalender(getContext(), getString(R.string.the_latest_date_of_donation), createAccountFragmentTvLatestDateOfDonationTxt, lastDateOfDonation);
                break;
        }
    }


    private void updateProfile() {

        mToken = SharedPreferencesManger.LoadData(getActivity(),SharedPreferencesManger.USER_API_TOKEN);
        name = createAccountFragmentEtName.getText().toString();
        mail = createAccountFragmentEtMail.getText().toString();
        birthDateTxt = birthDate.getDateTxt();
        lastDateOfDonationTxt = lastDateOfDonation.getDateTxt();
        //Log.v("birth",birthDateTxt);
        phone = createAccountFragmentEtPhone.getText().toString();
        password = createAccountFragmentEtPassword.getText().toString();
        passwordConfirm = createAccountFragmentEtPasswordConfirm.getText().toString();
        bloodClientId = createAccountFragmentSpinBloodType.getSelectedItemPosition();
        cityId  = createAccountFragmentSpinCity.getSelectedItemPosition();

        Toast.makeText(getContext(), ""+cityId, Toast.LENGTH_SHORT).show();

        //Validation
        if (name.isEmpty()){
            createToast(getContext(),getString(R.string.insert_user_name),Toast.LENGTH_SHORT);
        }else if(mail.isEmpty()) {
            createToast(getContext(),getString(R.string.insert_mail),Toast.LENGTH_SHORT);

        }else if (bloodClientId.equals(0)){
            createToast(getContext(),getString(R.string.insert_blood_type),Toast.LENGTH_SHORT);

        }

        else if (cityId == 0){
            createToast(getContext(),getString(R.string.insert_city),Toast.LENGTH_SHORT);

        }

        else if (governorateId.equals(0)){

            createToast(getContext(),getString(R.string.insert_governorate),Toast.LENGTH_SHORT);
        }else if(phone.isEmpty()){
            createToast(getContext(),getString(R.string.insert_phone),Toast.LENGTH_SHORT);
        }else if(phone.length() < 11){
            createToast(getContext(),getString(R.string.insert_correct_phone),Toast.LENGTH_SHORT);

        }else if (password.isEmpty() || passwordConfirm.isEmpty()) {
            createToast(getContext(),getString(R.string.insert_password),Toast.LENGTH_SHORT);
        } else if (!password.equals(passwordConfirm)) {
            createToast(getContext(),getString(R.string.password_does_not_match),Toast.LENGTH_SHORT);

        }else {
            if (HelperMethod.InternetState.isConnected(getContext())) {
                updateProfileRequest();

            }else
                createToast(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT);
        }
    }

    private void updateProfileRequest() {

        //Toast.makeText(getContext(), ""+bloodClientId, Toast.LENGTH_SHORT).show();


        showProgressDialog(getActivity(), getString(R.string.wait_moment));
        Call<UpdateProfile> updateProfileCall = getDataService.updateProfile(name
                , mail
                , birthDateTxt
                , cityId +""//cityID
                , phone
                , lastDateOfDonationTxt
                , password
                , passwordConfirm
                , bloodClientId + ""//bloodTypeId
                ,mToken);

        updateProfileCall.enqueue(new Callback<UpdateProfile>() {
            @Override
            public void onResponse(Call<UpdateProfile> call, Response<UpdateProfile> response) {
                dismissProgressDialog();

                UpdateProfile updateProfile = response.body();
                if (updateProfile.getStatus() == 1) {
                    createToast(getContext(), updateProfile.getMsg(), Toast.LENGTH_SHORT);
                    UpdateProfileData updatedData = updateProfile.getData() ;
                    SharedPreferencesManger.saveUserData(getActivity(),updatedData);
                    homeCycleActivity.homeFragment();


                } else
                    createToast(getContext(), updateProfile.getMsg(), Toast.LENGTH_SHORT);

            }
            @Override
            public void onFailure(Call<UpdateProfile> call, Throwable t) {
                dismissProgressDialog();
                //createSnackBar(getView(),getString(R.string.connect_internet),getContext(),null,null);
                createToast(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);

            }
        });
    }
    @Override
    public void onBack() {

        //HelperMethod.dialogCheckBackButton(getContext(),baseActivity.baseFragment);
        super.onBack();
    }
}
