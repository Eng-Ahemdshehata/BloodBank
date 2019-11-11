package com.ashehata.bloodbank.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.ListModel;
import com.ashehata.bloodbank.data.model.bloodType.BloodType;
import com.ashehata.bloodbank.data.model.bloodType.BloodTypeData;
import com.ashehata.bloodbank.data.model.categey.Categey;
import com.ashehata.bloodbank.data.model.categey.CategeyData;
import com.ashehata.bloodbank.data.model.governorates.GonernoratesData;
import com.ashehata.bloodbank.data.model.governorates.Governorates;
import com.ashehata.bloodbank.data.model.login.LoginData;
import com.ashehata.bloodbank.data.model.notificationCount.NotificationCount;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.fragment.AboutAppFragment;
import com.ashehata.bloodbank.ui.fragment.ContactUsFragment;
import com.ashehata.bloodbank.ui.fragment.EditProfileFragment;
import com.ashehata.bloodbank.ui.fragment.HomeFragment;
import com.ashehata.bloodbank.ui.fragment.NotificationSettingsFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ashehata.bloodbank.helper.HelperMethod.ReplaceFragment;
import static com.ashehata.bloodbank.helper.HelperMethod.createToast;
import static com.ashehata.bloodbank.helper.HelperMethod.hideAlertDialog;
import static com.ashehata.bloodbank.helper.HelperMethod.showAlertDialog;

public class HomeCycleActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    NavigationView navigationView;
    DrawerLayout drawer;
    @BindView(R.id.home_activity_iv_open_drawer)
    ImageView homeActivityIvOpenDrawer;
    @BindView(R.id.home_activity_iv_notification)
    ImageView homeActivityIvNotification;
    @BindView(R.id.home_activity_iv_back)
    ImageView homeActivityIvBack;
    @BindView(R.id.home_activity_tv_title)
    TextView homeActivityTvTitle;
    @BindView(R.id.home_activity_tv_notification_count)
    TextView homeActivityTvNotificationCount;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    //private HomeFragment active;
    ListModel listModel = new ListModel();

    private GetDataService getDataService;
    private boolean isListsDownloaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setTitle("");
        //****************

        // Make user accept permissions
        HelperMethod.onPermission(this);

        setNavigationView();
        homeFragment();
        setNotificationCount();


        // Download Lists from server
        isListsDownloaded = SharedPreferencesManger.LoadBoolean(this, SharedPreferencesManger.IS_LIST_DOWNLOADED);
        if (isListsDownloaded) {
            //dismissProgressDialog();

        } else {
            downloadLists();
            //dismissProgressDialog();
        }


    }

    private void downloadLists() {
        Activity activity = this;
        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        downloadBloodList(activity);
    }

    private void downloadBloodList(Activity activity) {
        getDataService.getBloodType().enqueue(new Callback<BloodType>() {
            @Override
            public void onResponse(Call<BloodType> call, Response<BloodType> response) {
                BloodType bloodType = response.body();
                if (bloodType.getStatus() == 1) {

                    List<BloodTypeData> dataList = bloodType.getData();
                    listModel.setBloodType(dataList);
                    downloadGovernoratesList(activity);

                } else {

                }
            }

            @Override
            public void onFailure(Call<BloodType> call, Throwable t) {
                createToast(getBaseContext(), "error", Toast.LENGTH_SHORT);
            }
        });
    }

    private void downloadGovernoratesList(Activity activity) {

        getDataService.getGovernorates().enqueue(new Callback<Governorates>() {
            @Override
            public void onResponse(Call<Governorates> call, Response<Governorates> response) {
                Governorates governorates = response.body();
                if (governorates.getStatus() == 1) {

                    List<GonernoratesData> dataList = governorates.getData();
                    //listModel = new ListModel();
                    listModel.setGovernorates(dataList);

                    downloadCategey(activity);


                } else {

                }
            }

            @Override
            public void onFailure(Call<Governorates> call, Throwable t) {
                createToast(getBaseContext(), "error", Toast.LENGTH_SHORT);
            }
        });


    }

    private void downloadCategey(Activity activity) {
        getDataService.getCategories().enqueue(new Callback<Categey>() {
            @Override
            public void onResponse(Call<Categey> call, Response<Categey> response) {

                Categey categey = response.body();
                if (categey.getStatus() == 1) {

                    List<CategeyData> dataList = categey.getData();
                    //listModel = new ListModel();
                    listModel.setCategories(dataList);
                    //createToast(getBaseContext(),dataList.get(0).getName(), Toast.LENGTH_SHORT);
                    SharedPreferencesManger.SaveData(activity, SharedPreferencesManger.LIST_MODEL, listModel);

                    SharedPreferencesManger.SaveData(activity, SharedPreferencesManger.IS_LIST_DOWNLOADED, true);

                    HelperMethod.createToast(activity.getBaseContext(), "Done", Toast.LENGTH_SHORT);
                    homeFragment();

                } else {

                }
            }

            @Override
            public void onFailure(Call<Categey> call, Throwable t) {
                createToast(getBaseContext(), "error", Toast.LENGTH_SHORT);
            }
        });


    }

    private void setNotificationCount() {
        LoginData userData = SharedPreferencesManger.loadUserData(this);
        String token = userData.getApiToken();
        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        getDataService.getNotificationCount(token).enqueue(new Callback<NotificationCount>() {
            @Override
            public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {

                try {

                    NotificationCount notificationCount = response.body();
                    if (notificationCount.getStatus() == 1) {
                        // Set notification count
                        String count = String.valueOf(notificationCount.getData().getNotificationsCount());
                        homeActivityTvNotificationCount.setText(count);
                        //Log.v("token",token);

                    }

                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<NotificationCount> call, Throwable t) {

            }
        });


    }


    private void setNavigationView() {

        createDrawer();

    }


    private void createDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            //baseFragment.onBack();
            super.onBackPressed();

        //exitApp();

    }

    private void exitApp() {
        final Context mContext = this;

        showAlertDialog(mContext
                , getString(R.string.exit_app)
                , null
                , false
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideAlertDialog(mContext);

                    }
                }, getString(R.string.yes), getString(R.string.no));
    }

    @OnClick({R.id.home_activity_iv_open_drawer, R.id.home_activity_iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.home_activity_iv_open_drawer:

                openDrawer();
                break;

            case R.id.home_activity_iv_back:

                //onBackPressed();
                super.onBackPressed();

                break;

        }


    }

    public void homeFragment() {
        //active = new HomeFragment() ;
        ReplaceFragment(getSupportFragmentManager()
                , new HomeFragment()
                , R.id.home_activity_fl_home
                , true);
    }

    private void openDrawer() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {


        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void setFragmentConfig(int notificationIcon
            , int drawerIcon
            , int backIcon
            , int notificationCircle
            , String title) {

        homeActivityIvNotification.setVisibility(notificationIcon);
        homeActivityIvOpenDrawer.setVisibility(drawerIcon);
        homeActivityIvBack.setVisibility(backIcon);
        homeActivityTvNotificationCount.setVisibility(notificationCircle);
        homeActivityTvTitle.setText(title);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_account:
                myInformationFragment();

                break;

            case R.id.nav_notification:
                notificationSettings();

                break;
            case R.id.nav_heart:

                break;
            case R.id.nav_home:
                //homeFragment();

                break;
            case R.id.nav_instructions:

                break;
            case R.id.nav_contact_us:

                contactUsFragment();
                break;
            case R.id.nav_about_app:
                aboutAppFragment();

                break;
            case R.id.nav_rate_app:


                break;
            case R.id.nav_sign_out:
                signOut();

                break;
        }
        //active = null ;
        drawer.closeDrawers();
        return true;
    }

    private void notificationSettings() {
        ReplaceFragment(getSupportFragmentManager()
                , new NotificationSettingsFragment()
                , R.id.home_activity_fl_home
                , false);
    }

    private void myInformationFragment() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        ReplaceFragment(getSupportFragmentManager()
                , editProfileFragment
                , R.id.home_activity_fl_home
                , false);
    }

    private void signOut() {
        final Context mContext = this;

        showAlertDialog(mContext
                , getString(R.string.are_you_want_sign_out)
                , null
                , false
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HelperMethod.startActivity(mContext, UserCycleActivity.class, "");
                        SharedPreferencesManger.clean(getParent());
                        finish();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideAlertDialog(mContext);
                        //active = new  HomeFragment();
                    }
                }, getString(R.string.yes), getString(R.string.no));

    }

    private void contactUsFragment() {
        ReplaceFragment(getSupportFragmentManager()
                , new ContactUsFragment()
                , R.id.home_activity_fl_home
                , false);
    }

    private void aboutAppFragment() {
        ReplaceFragment(getSupportFragmentManager()
                , new AboutAppFragment()
                , R.id.home_activity_fl_home
                , false);
    }
}
