package com.ashehata.bloodbank.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.adapter.DonationAdapter;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.ListModel;
import com.ashehata.bloodbank.data.model.bloodType.BloodTypeData;
import com.ashehata.bloodbank.data.model.donation.Donation;
import com.ashehata.bloodbank.data.model.donation.DonationData;
import com.ashehata.bloodbank.data.model.governorates.GonernoratesData;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.helper.OnEndLess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ashehata.bloodbank.helper.HelperMethod.createToast;

public class DonationFragment extends BaseFragment  {

    @BindView(R.id.donation_fragment_rv_donation)
    RecyclerView donationFragmentRvDonation;
    @BindView(R.id.donation_fragment_pb_indicator)
    ProgressBar donationFragmentPbIndicator;
    @BindView(R.id.donation_fragment_pb_indicator_more)
    ProgressBar donationFragmentPbIndicatorMore;
    @BindView(R.id.donation_fragment_srl_refresh)
    SwipeRefreshLayout donationFragmentSrlRefresh;
    @BindView(R.id.donation_fragment_spin_blood_type)
    Spinner donationFragmentSpinBloodType;
    @BindView(R.id.donation_fragment_spin_city)
    Spinner donationFragmentSpinCity;

    private GetDataService getDataService;
    LinearLayoutManager linearLayoutManager;
    // DividerItemDecoration dividerItemDecoration;
    List<DonationData> datumList;
    String mToken;
    private DonationAdapter donationAdapter;
    int pastItems, visibleItems, totalItems, nextPage;
    boolean loading = false;
    private OnEndLess onEndLess;


    public DonationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donation, container, false);
        ButterKnife.bind(this, view);

        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);

        setRecyclerConfig();

        if (HelperMethod.InternetState.isConnected(getContext())) {

            getDonation();

        } else {
            createToast(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);
            hideProgressIndicator();
            getDataFromPreference();

        }
        fillSpinners(getActivity());

        // set refresh indicator
        refreshDonation();
        return view;
    }

    private void fillSpinners(FragmentActivity activity) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                ListModel listModel = SharedPreferencesManger.loadUserListModel(activity);
                if(listModel != null){

                    // Fill blood type spinner
                    List<String> stringList = new ArrayList<>();
                    stringList.add(getString(R.string.choose_blood_type));
                    for (int i=0 ;i<listModel.getBloodType().size();i++){

                        BloodTypeData data = listModel.getBloodType().get(i);
                        stringList.add(data.getName());
                        HelperMethod.setSpinner(activity,donationFragmentSpinBloodType,stringList);

                    }

                    // Fill City spinner
                    List<String> stringList2 = new ArrayList<>();
                    stringList2.add(getString(R.string.choose_city));
                    for (int i=0 ;i<listModel.getGovernorates().size();i++){
                        GonernoratesData data = listModel.getGovernorates().get(i);
                        stringList2.add(data.getName());
                        HelperMethod.setSpinner(activity,donationFragmentSpinCity,stringList2);

                    }

                }
            }
        });
    }
    private void refreshDonation() {
        donationFragmentSrlRefresh.setOnRefreshListener(() -> {
            // Get donation again
            getDonation();

        });
    }

    private void getDonation() {

        mToken = SharedPreferencesManger.LoadData(getActivity(),SharedPreferencesManger.USER_API_TOKEN);

        Call<Donation> postsCall = getDataService.getDonation(mToken, 1);

        postsCall.enqueue(new Callback<Donation>() {
            @Override
            public void onResponse(Call<Donation> call, Response<Donation> response) {

                Donation donation = response.body();
                if (donation.getStatus() == 1) {

                    // hide indicators
                    hideIndicators();
                    //HelperMethod.createToast(getContext(),posts.getMsg(), Toast.LENGTH_SHORT);

                    //nextPage = 2;

                    datumList = donation.getData().getData();

                    //save the first page of donation from api
                    saveDonationInPreference(donation);
                    donationAdapter = new DonationAdapter(getActivity(), getContext(), datumList, getFragmentManager());

                    donationFragmentRvDonation.setAdapter(null);
                    // Set adapter for recycler
                    donationFragmentRvDonation.setAdapter(donationAdapter);

                    // loading next page
                    //loadNextPage();
                    onEndLess = new OnEndLess(linearLayoutManager,1) {
                        @Override
                        public void onLoadMore(int current_page) {
                            loadMore(current_page);

                        }
                    };
                    donationFragmentRvDonation.addOnScrollListener(onEndLess);

                } else {

                    // hide indicators
                    hideIndicators();
                    createToast(getContext(), donation.getMsg(), Toast.LENGTH_SHORT);

                }
            }

            @Override
            public void onFailure(Call<Donation> call, Throwable t) {
                // hide indicators
                hideIndicators();
                createToast(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);
                getDataFromPreference();


            }
        });


    }



    private void saveDonationInPreference(Donation donation) {

        SharedPreferencesManger.SaveData(getActivity(), SharedPreferencesManger.USER_DONATION, donation);

    }



    private void setRecyclerConfig() {
        linearLayoutManager= new LinearLayoutManager(getContext());
        // Set items on linear manager
        donationFragmentRvDonation.setLayoutManager(linearLayoutManager);

        // Set divider for recycler view
        /*
        dividerItemDecoration = new DividerItemDecoration(donationFragmentRvDonation.getContext(),
                linearLayoutManager.getOrientation());
        donationFragmentRvDonation.addItemDecoration(dividerItemDecoration);
         */

        // Fixed size
        donationFragmentRvDonation.setHasFixedSize(true);
    }


    /*
    private void loadNextPage() {

        donationFragmentRvDonation.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItems = linearLayoutManager.getChildCount();
                    totalItems = linearLayoutManager.getItemCount();
                    pastItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {

                        if (visibleItems + pastItems >= totalItems) {

                            donationFragmentPbIndicatorMore.setVisibility(View.VISIBLE);
                            loading = false;
                            loadMore();


                        }
                    } else
                        loading = true;
                }
            }
        });
    }

*/
    private void loadMore(int page) {

        donationFragmentPbIndicatorMore.setVisibility(View.VISIBLE);
        Call<Donation> postsCall = getDataService.getDonation(mToken, page);

        postsCall.enqueue(new Callback<Donation>() {
            @Override
            public void onResponse(Call<Donation> call, Response<Donation> response) {
                Donation donation = response.body();
                if (donation.getStatus() == 1) {

                    //HelperMethod.createToast(getContext(),nextPage+"",Toast.LENGTH_SHORT);

                    donationFragmentPbIndicatorMore.setVisibility(View.GONE);
                    //Toast.makeText(getContext(), nextPage + "", Toast.LENGTH_SHORT).show();
                    //nextPage++;
                    for (int i = 0; i < donation.getData().getData().size(); i++) {
                        donationAdapter.addData(donation.getData().getData().get(i));
                    }

                    donationAdapter.notifyDataSetChanged();

                } else {

                    donationFragmentPbIndicatorMore.setVisibility(View.GONE);
                    createToast(getContext(), donation.getMsg(), Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onFailure(Call<Donation> call, Throwable t) {

                donationFragmentPbIndicatorMore.setVisibility(View.GONE);
                //HelperMethod.createToast(getContext(), getString(R.string.connect_internet), Toast.LENGTH_SHORT);
            }
        });
    }
    /*
    @Override
    public void onBack() {
        //HelperMethod.createToast(getContext(),"dgdf",Toast.LENGTH_SHORT);

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

        //super.onBack();
    }

     */
    private void getDataFromPreference() {
        // write here ..
        Donation donation = SharedPreferencesManger.loadUserDonation(getActivity());
        //createToast(getContext(),posts.getData().getData().get(0).getTitle()+"",Toast.LENGTH_SHORT);
        donationAdapter = new DonationAdapter(getActivity(), getContext(), donation.getData().getData(), getFragmentManager());

        /*
        // Hide progress bar indicator
        postsFragmentPbIndicator.setVisibility(View.GONE);

         */
        // Set adapter for recycler
        donationFragmentRvDonation.setAdapter(donationAdapter);
    }

    private void hideIndicators() {
        //hide refresh indicator
        hideRefreshIndicator();
        //hide progress bar indicator
        hideProgressIndicator();
    }


    private void hideProgressIndicator() {
        donationFragmentPbIndicator.setVisibility(View.GONE);

    }

    private void hideRefreshIndicator() {

        if (donationFragmentSrlRefresh.isRefreshing()) {

            donationFragmentSrlRefresh.setRefreshing(false);
            createToast(getContext(), getString(R.string.refresh_done), Toast.LENGTH_SHORT);
        }
    }
}
