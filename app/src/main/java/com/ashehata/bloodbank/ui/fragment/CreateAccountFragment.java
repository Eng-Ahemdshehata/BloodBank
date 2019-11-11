package com.ashehata.bloodbank.ui.fragment;

import android.content.Intent;
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
import com.ashehata.bloodbank.data.model.bloodType.BloodType;
import com.ashehata.bloodbank.data.model.bloodType.BloodTypeData;
import com.ashehata.bloodbank.data.model.cities.Cities;
import com.ashehata.bloodbank.data.model.cities.CitiesData;
import com.ashehata.bloodbank.data.model.createAccount.Client;
import com.ashehata.bloodbank.data.model.createAccount.CreateAccount;
import com.ashehata.bloodbank.data.model.governorates.GonernoratesData;
import com.ashehata.bloodbank.data.model.governorates.Governorates;
import com.ashehata.bloodbank.data.model.login.LoginData;
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

public class CreateAccountFragment extends BaseFragment {

    View view;
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

    HomeCycleActivity homeCycleActivity;
    private GetDataService getDataService;
    private String name="";
    private String mail="";
    private DateModel birthDate = new DateModel();
    private String birthDateTxt = "";
    private DateModel lastDateOfDonation = new DateModel();
    private String lastDateOfDonationTxt="";
    private String phone="";
    private String password="";
    private String passwordConfirm="";
    private List<String> bloodTypeNames=new ArrayList<>();
    private List<String> governoratesNames=new ArrayList<>();
    private List<String> citiesNames =new ArrayList<>();
    private List<Integer> bloodTypeId =new ArrayList<>();
    private List<Integer> governoratesId=new ArrayList<>();
    private List<Integer> citiesId=new ArrayList<>();

    private List<GonernoratesData> governorate;
    private List<CitiesData> city;
    private List<BloodTypeData> bloodTypeList;
    // public boolean displayAccountInfo;
    private String mToken;
    private Integer cityId = 0;
    private Integer bloodId = 0;
    private Integer governorateId = 0;
    private String userPassword;


    public CreateAccountFragment() {
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
        view = inflater.inflate(R.layout.fragment_create_account, container, false);
        ButterKnife.bind(this, view);
        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);


        if (HelperMethod.InternetState.isConnected(getContext())) {
            setSpinners();

        } else {
            //createSnackBar(getView(),getString(R.string.connect_internet),getContext(),null,null);

            createToast(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);
        }

        return view;
    }

    private void setSpinners() {
        setBloodSpinner();
        setGovernoratesSpinner();
    }

    private void setGovernoratesSpinner() {
        Call<Governorates> governoratesCall = getDataService.getGovernorates();
        governoratesCall.enqueue(new Callback<Governorates>() {
            @Override
            public void onResponse(Call<Governorates> call, Response<Governorates> response) {
                Governorates governorates = response.body();
                if (governorates.getStatus() == 1) {

                    governorate = governorates.getData();

                    governoratesNames.add(getString(R.string.choose_governorate));
                    governoratesId.add(0);

                    for (int i = 0; i < governorate.size(); i++) {
                        governoratesNames.add(governorate.get(i).getName());
                        governoratesId.add(governorate.get(i).getId());

                    }
                    setSpinner(getActivity(), createAccountFragmentSpinGovernorate, governoratesNames);
                    setCity();
                }

            }

            @Override
            public void onFailure(Call<Governorates> call, Throwable t) {
                //createSnackBar(getView(),getString(R.string.connect_internet),getContext(),null,null);

            }
        });
    }

    private void setCity() {
        createAccountFragmentSpinGovernorate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if(i ==0){
                        createAccountFragmentSpinCity.setVisibility(View.GONE);

                    }else {
                        createAccountFragmentSpinCity.setVisibility(View.VISIBLE);
                        setCitiesSpinner(governoratesId.get(i));
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
                    city = cities.getData();

                    citiesNames.clear();
                    citiesNames.add(getString(R.string.choose_city));
                    citiesId.add(0);

                    for (int i = 0; i < city.size(); i++) {
                        citiesNames.add(city.get(i).getName());
                        citiesId.add(city.get(i).getId());

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

    private void setBloodSpinner() {
        showProgressDialog(getActivity(), getString(R.string.wait_moment));
        Call<BloodType> bloodTypeCall = getDataService.getBloodType();
        bloodTypeCall.enqueue(new Callback<BloodType>() {
            @Override
            public void onResponse(Call<BloodType> call, Response<BloodType> response) {

                dismissProgressDialog();

                BloodType bloodTypeResponse = response.body();

                if (bloodTypeResponse.getStatus() == 1) {

                    bloodTypeList = bloodTypeResponse.getData();

                    bloodTypeNames.add(getString(R.string.choose_blood_type));
                    bloodTypeId.add(0);

                    for (int i = 0; i < bloodTypeList.size(); i++) {
                        bloodTypeNames.add(bloodTypeList.get(i).getName());
                        bloodTypeId.add(bloodTypeList.get(i).getId());
                    }

                    setSpinner(getActivity(), createAccountFragmentSpinBloodType, bloodTypeNames);

                } else {
                    createToast(getContext(), bloodTypeResponse.getMsg(), Toast.LENGTH_SHORT);
                    dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Call<BloodType> call, Throwable t) {
                dismissProgressDialog();

                //createSnackBar(getView(),getString(R.string.connect_internet),getContext(),null,null);
                createToast(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);

            }
        });


    }


    @OnClick({R.id.create_account_fragment_et_birth_date
            , R.id.create_account_fragment_et_the_latest_date_of_donation
            , R.id.create_account_fragment_btn_create})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.create_account_fragment_btn_create:
                getData();
                break;
            case R.id.create_account_fragment_et_birth_date:
                HelperMethod.showCalender(getContext(), getString(R.string.birth_date), createAccountFragmentTvBirthDateTxt, birthDate);
                break;
            case R.id.create_account_fragment_et_the_latest_date_of_donation:
                HelperMethod.showCalender(getContext(), getString(R.string.the_latest_date_of_donation), createAccountFragmentTvLatestDateOfDonationTxt, lastDateOfDonation);
                break;
        }
    }


    private void getData() {

        name = createAccountFragmentEtName.getText().toString().trim();
        mail = createAccountFragmentEtMail.getText().toString().trim();
        birthDateTxt = birthDate.getDateTxt();
        lastDateOfDonationTxt = lastDateOfDonation.getDateTxt();
        //Log.v("birth",birthDateTxt);
        phone = createAccountFragmentEtPhone.getText().toString().trim();
        password = createAccountFragmentEtPassword.getText().toString().trim();
        passwordConfirm = createAccountFragmentEtPasswordConfirm.getText().toString().trim();

        cityId = citiesId.get(createAccountFragmentSpinCity.getSelectedItemPosition()) ;

        bloodId = bloodTypeId.get(createAccountFragmentSpinBloodType.getSelectedItemPosition()) ;
        governorateId = governoratesId.get(createAccountFragmentSpinGovernorate.getSelectedItemPosition()) ;

        //Validation
        if (name.isEmpty()){
            createToast(getContext(),getString(R.string.insert_user_name),Toast.LENGTH_SHORT);
        }else if(mail.isEmpty()) {
            createToast(getContext(),getString(R.string.insert_mail),Toast.LENGTH_SHORT);
        }else if(birthDateTxt.isEmpty()){
            createToast(getContext(),getString(R.string.insert_birth_data),Toast.LENGTH_SHORT);

        }else if(lastDateOfDonationTxt.isEmpty()){
            createToast(getContext(),getString(R.string.insert_last_donation_data),Toast.LENGTH_SHORT);
        }else if (bloodId.equals(0)){
            createToast(getContext(),getString(R.string.insert_blood_type),Toast.LENGTH_SHORT);

        }else if (cityId.equals(0)){
            createToast(getContext(),getString(R.string.insert_city),Toast.LENGTH_SHORT);

        }else if (governorateId.equals(0)){
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
                createAccount();

            }else
                createToast(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT);

        }

    }

    private void createAccount() {
         showProgressDialog(getActivity(), getString(R.string.wait_moment));
         Call<CreateAccount> createAccountCall = getDataService.createAccount(name
                    , mail
                    , birthDateTxt
                    ,  cityId+ ""
                    , phone
                    , lastDateOfDonationTxt
                    , password
                    , passwordConfirm,
                 bloodId + "");

         createAccountCall.enqueue(new Callback<CreateAccount>() {
                @Override
                public void onResponse(Call<CreateAccount> call, Response<CreateAccount> response) {
                    dismissProgressDialog();

                    CreateAccount createAccount = response.body();
                    if (createAccount.getStatus() == 1) {
                        createToast(getContext(), createAccount.getMsg(), Toast.LENGTH_SHORT);
                        //loginFragment();
                        userPassword = createAccountFragmentEtPassword.getText().toString().trim();

                        // Remember me checkbox
                        SharedPreferencesManger.SaveData(getActivity(),SharedPreferencesManger.REMEMBER,true);

                        // Save user data in preference
                        SharedPreferencesManger.saveUserData(getActivity(),createAccount.getData());

                        // Save user apiToken in preference
                        SharedPreferencesManger.SaveData(getActivity(),SharedPreferencesManger.USER_API_TOKEN,createAccount.getData().getApiToken());

                        // Save user password in preference
                        SharedPreferencesManger.SaveData(getActivity(),SharedPreferencesManger.USER_PASSWORD,userPassword);

                        // Show a toast message to user
                        createToast(getContext(),createAccount.getMsg(),Toast.LENGTH_SHORT);

                        homeActivity();

                    } else
                        createToast(getContext(), createAccount.getMsg(), Toast.LENGTH_SHORT);

                }

                @Override
                public void onFailure(Call<CreateAccount> call, Throwable t) {

                    dismissProgressDialog();
                    //createSnackBar(getView(),getString(R.string.connect_internet),getContext(),null,null);
                    createToast(getContext(), getString(R.string.error), Toast.LENGTH_SHORT);

                }
            });
}


    private void homeActivity() {
        Intent intent = new Intent(getContext(), HomeCycleActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onBack() {

        super.onBack();

    }
}