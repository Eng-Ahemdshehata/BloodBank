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
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.contactUs.ContactUs;
import com.ashehata.bloodbank.data.model.login.LoginData;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.HomeCycleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ashehata.bloodbank.helper.HelperMethod.createToast;
import static com.ashehata.bloodbank.helper.HelperMethod.disappearKeypad;
import static com.ashehata.bloodbank.helper.HelperMethod.dismissProgressDialog;
import static com.ashehata.bloodbank.helper.HelperMethod.showProgressDialog;

public class ContactUsFragment extends BaseFragment {

    HomeCycleActivity homeCycleActivity;
    @BindView(R.id.contact_us_fragment_et_phone)
    EditText contactUsFragmentEtPhone;
    @BindView(R.id.contact_us_fragment_et_title)
    EditText contactUsFragmentEtTitle;
    @BindView(R.id.contact_us_fragment_et_message)
    EditText contactUsFragmentEtMessage;
    @BindView(R.id.contact_us_fragment_et_name)
    EditText contactUsFragmentEtName;
    @BindView(R.id.contact_us_fragment_et_mail)
    EditText contactUsFragmentEtMail;
    @BindView(R.id.contact_us_fragment_btn_send)
    Button contactUsFragmentBtnSend;

    String phone , title , message , mail , name ;
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
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this,view);


        homeCycleActivity = (HomeCycleActivity) getActivity();
        homeCycleActivity.setFragmentConfig(View.GONE, View.GONE, View.VISIBLE, View.GONE, getString(R.string.menu_contact_us));

        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);



        return view;
    }


    @OnClick(R.id.contact_us_fragment_btn_send)
    public void onViewClicked() {
        if(HelperMethod.InternetState.isConnected(getContext())){

            sendData();

        }else {
            createToast(getContext(),getString(R.string.no_internet), Toast.LENGTH_SHORT);
        }
    }

    private void sendData() {
        showProgressDialog(getActivity(),getString(R.string.wait_moment));
        phone = contactUsFragmentEtPhone.getText()+"";
        title = contactUsFragmentEtTitle.getText()+"";
        message = contactUsFragmentEtMessage.getText()+"";
        mail = contactUsFragmentEtMail.getText()+"";
        name = contactUsFragmentEtName.getText()+"";
        LoginData userData = SharedPreferencesManger.loadUserData(getActivity());
        String apiToken = userData.getApiToken();

        getDataService.contactUs(apiToken,title,message).enqueue(new Callback<ContactUs>() {
            @Override
            public void onResponse(Call<ContactUs> call, Response<ContactUs> response) {
                dismissProgressDialog();

                 ContactUs contactUs = response.body();

                if(contactUs.getStatus() == 1){

                    createToast(getContext(),contactUs.getMsg(),Toast.LENGTH_SHORT);

                    onBack();

                }else{
                    createToast(getContext(),contactUs.getMsg(),Toast.LENGTH_SHORT);
                }
                disappearKeypad(getActivity(),getView());

            }

            @Override
            public void onFailure(Call<ContactUs> call, Throwable t) {
                dismissProgressDialog();
                createToast(getContext(),getString(R.string.no_internet), Toast.LENGTH_SHORT);

            }
        });
    }

    @Override
    public void onBack() {

        //homeCycleActivity.homeFragment();
        super.onBack();
    }

}
