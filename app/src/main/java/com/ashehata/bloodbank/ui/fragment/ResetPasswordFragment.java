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
import com.ashehata.bloodbank.data.model.resetPassword.ResetPassword;
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

public class ResetPasswordFragment extends BaseFragment {

    @BindView(R.id.reset_password_fragment_et_phone)
    EditText forget_password_fragment_et_phone;
    @BindView(R.id.forget_password_fragment_btn_send)
    Button forgetPasswordFragmentBtnSend;

    String userPhone ;
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
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        ButterKnife.bind(this,view);
        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);



        return view;
    }

    @OnClick(R.id.forget_password_fragment_btn_send)

    public void onViewClicked() {

        getPassword();

    }

    private void getPassword() {
        disappearKeypad(getActivity(),getView());
        userPhone = forget_password_fragment_et_phone.getText().toString().trim();

        if (userPhone.isEmpty()) {
            createToast(getContext(),getString(R.string.insert_phone),Toast.LENGTH_SHORT);
        }else if(userPhone.length() <11){
            createToast(getContext(),getString(R.string.insert_correct_phone),Toast.LENGTH_SHORT);

        }else {
            if (HelperMethod.InternetState.isConnected(getContext())) {
                getNewPassword();

            }else
                createToast(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT);

        }


    }

    private void getNewPassword(){

        showProgressDialog(getActivity(), getString(R.string.wait_moment));

        Call<ResetPassword> resetPasswordCall =getDataService.resetPassword(userPhone);
        resetPasswordCall.enqueue(new Callback<ResetPassword>() {
            @Override
            public void onResponse(Call<ResetPassword> call, Response<ResetPassword> response) {

                dismissProgressDialog();

                ResetPassword resetPassword = response.body();
                if (resetPassword.getStatus()==1){


                    NewPasswordFragment newPasswordFragment =new NewPasswordFragment() ;
                    newPasswordFragment.phone = userPhone ;

                    HelperMethod.ReplaceFragment(getFragmentManager()
                            ,newPasswordFragment
                            ,R.id.user_cycle_activity_fl_user_login
                            , true);
                    createToast(getContext(),resetPassword.getMsg(),Toast.LENGTH_SHORT);

                }else {

                    createToast(getContext(),resetPassword.getMsg(),Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void onFailure(Call<ResetPassword> call, Throwable t) {
                dismissProgressDialog();
                createSnackBar(getView(),getString(R.string.error),getContext(),null,null);

            }
        });

    }
}
