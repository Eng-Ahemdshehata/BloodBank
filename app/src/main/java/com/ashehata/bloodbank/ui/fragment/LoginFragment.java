package com.ashehata.bloodbank.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.login.Login;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ashehata.bloodbank.helper.HelperMethod.createSnackBar;
import static com.ashehata.bloodbank.helper.HelperMethod.createToast;
import static com.ashehata.bloodbank.helper.HelperMethod.disappearKeypad;
import static com.ashehata.bloodbank.helper.HelperMethod.dismissProgressDialog;
import static com.ashehata.bloodbank.helper.HelperMethod.showProgressDialog;

public class LoginFragment extends BaseFragment {


    @BindView(R.id.login_fragment_cb_remember_me)
    CheckBox loginFragmentCbRememberMe;
    @BindView(R.id.login_fragment_tv_forget_password)
    TextView loginFragmentTvForgetPassword;
    @BindView(R.id.login_fragment_btn_login)
    Button loginFragmentBtnLogin;
    @BindView(R.id.login_fragment_btn_create_account)
    Button loginFragmentBtnCreateAccount;
    @BindView(R.id.login_fragment_et_phone_number)
    EditText loginFragmentEtPhoneNumber;
    @BindView(R.id.login_fragment_et_password)
    EditText loginFragmentEtPassword;

    String userPhone, userPassword;
    @BindView(R.id.login_fragment_linear)
    LinearLayout loginFragmentLinear;
    private GetDataService getDataService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);


        return view;
    }

    @Override
    public void onBack() {

        //HelperMethod.startActivity(getContext(), SplashCycleActivity.class, "login");
        getActivity().finish();

    }
    @OnClick({R.id.login_fragment_tv_forget_password,
            R.id.login_fragment_btn_login,
            R.id.login_fragment_linear,
            R.id.login_fragment_btn_create_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_fragment_tv_forget_password:

                forgetPasswordFragment();
                break;

            case R.id.login_fragment_btn_login:
                login();

                break;

            case R.id.login_fragment_btn_create_account:

                createAccountFragment();

                break;
            case R.id.login_fragment_linear:

               disappearKeypad(getActivity(), getView());

                break;
        }
    }

    private void login() {
        userPhone = loginFragmentEtPhoneNumber.getText().toString().trim();
        userPassword = loginFragmentEtPassword.getText().toString().trim();
        disappearKeypad(getActivity(),getView());

        // Validation
        if(userPhone.isEmpty()){
            createToast(getContext(),getString(R.string.insert_phone),Toast.LENGTH_SHORT);
        }else if(userPhone.length() < 11){
            createToast(getContext(),getString(R.string.insert_correct_phone),Toast.LENGTH_SHORT);
        }else if(userPassword.isEmpty()){
            createToast(getContext(),getString(R.string.insert_password),Toast.LENGTH_SHORT);

        }else {
            if (HelperMethod.InternetState.isConnected(getContext())) {
                startConnection();
            }else
                createToast(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT);

        }


    }

    private void startConnection() {

        showProgressDialog(getActivity(), getString(R.string.wait_moment));

        Call<Login> loginCall = getDataService.getUser(userPhone, userPassword);

        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                dismissProgressDialog();
                Login login = response.body();

                if (login.getStatus()==1) {

                    SharedPreferencesManger.setSharedPreferences(getActivity());

                    if(loginFragmentCbRememberMe.isChecked()){
                        // Remember me checkbox
                        SharedPreferencesManger.SaveData(getActivity(),SharedPreferencesManger.REMEMBER,true);

                    }
                    // Save user data in preference
                    SharedPreferencesManger.saveUserData(getActivity(),login.getData());

                    // Save user apiToken in preference
                    SharedPreferencesManger.SaveData(getActivity(),SharedPreferencesManger.USER_API_TOKEN,login.getData().getApiToken());


                    // Save user password in preference
                    SharedPreferencesManger.SaveData(getActivity(),SharedPreferencesManger.USER_PASSWORD,userPassword);

                    // Show a toast message to user
                    createToast(getContext(),login.getMsg(),Toast.LENGTH_SHORT);

                    homeActivity();

                }else {
                    createToast(getContext(),login.getMsg(),Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                dismissProgressDialog();
                createSnackBar(getView(),getString(R.string.error),getContext(),null,null);
                //createToast(getContext(),getString(R.string.connect_internet), Toast.LENGTH_SHORT);

            }
        });

    }

    private void homeActivity() {
        Intent intent = new Intent(getContext(), HomeCycleActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void createAccountFragment() {
        CreateAccountFragment createAccountFragment = new CreateAccountFragment() ;

        HelperMethod.ReplaceFragment(getFragmentManager()
                , createAccountFragment
                , R.id.user_cycle_activity_fl_user_login
                , true);
    }

    private void forgetPasswordFragment() {
        HelperMethod.ReplaceFragment(getFragmentManager()
                , new ResetPasswordFragment()
                , R.id.user_cycle_activity_fl_user_login
                , true);
    }


}
