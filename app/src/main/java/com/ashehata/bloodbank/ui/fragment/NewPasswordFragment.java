package com.ashehata.bloodbank.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.model.GeneralResponse;
import com.ashehata.bloodbank.helper.HelperMethod;

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

public class NewPasswordFragment extends BaseFragment {


    @BindView(R.id.new_password_fragment_et_code)
    EditText newPasswordFragmentEtCode;
    @BindView(R.id.new_password_fragment_et_password)
    EditText newPasswordFragmentEtPassword;
    @BindView(R.id.new_password_fragment_et_password_confirm)
    EditText newPasswordFragmentEtPasswordConfirm;
    @BindView(R.id.new_password_fragment_btn_change)
    Button newPasswordFragmentBtnChange;

    private GetDataService getDataService;
    private String password;
    private String passwordConfirm;
    private String code;
    public String phone ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_password, container, false);
        ButterKnife.bind(this,view);
        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);


        return view;
    }

    @OnClick(R.id.new_password_fragment_btn_change)
    public void onViewClicked() {
        newPassword();
    }

    private void newPassword() {
        disappearKeypad(getActivity(),getView());

        password = newPasswordFragmentEtPassword.getText().toString().trim();
        passwordConfirm = newPasswordFragmentEtPasswordConfirm.getText().toString().trim();
        code = newPasswordFragmentEtCode.getText().toString().trim();

        //Validation
        if (password.isEmpty() || passwordConfirm.isEmpty()) {
            createToast(getContext(),getString(R.string.insert_password),Toast.LENGTH_SHORT);

        } else if (!password.equals(passwordConfirm)) {
            createToast(getContext(),getString(R.string.password_does_not_match),Toast.LENGTH_SHORT);

        }else if(code.isEmpty()){
            createToast(getContext(),getString(R.string.insert_code),Toast.LENGTH_SHORT);

        }else {
            if (HelperMethod.InternetState.isConnected(getContext())){
                setNewPassword();
            }else
                createToast(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT);

        }


    }

    private void  setNewPassword(){
        showProgressDialog(getActivity(), getString(R.string.wait_moment));
        Call<GeneralResponse> resetPasswordCall =getDataService.setNewPassword(password,passwordConfirm
                ,code
                ,phone);

        resetPasswordCall.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                dismissProgressDialog();
                GeneralResponse newPassword = response.body();

                if (newPassword.getStatus()==1){

                    HelperMethod.ReplaceFragment(getFragmentManager()
                            ,new LoginFragment()
                            ,R.id.user_cycle_activity_fl_user_login
                            , true);

                    createToast(getContext(),newPassword.getMsg(),Toast.LENGTH_SHORT);

                }else {
                    createToast(getContext(),newPassword.getMsg(),Toast.LENGTH_SHORT);

                }
            }
            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

                dismissProgressDialog();
                createSnackBar(getView(),getString(R.string.error),getContext(),null,null);
                //createToast(getContext(),getString(R.string.connect_internet), Toast.LENGTH_SHORT);

            }
        });
    }
}
