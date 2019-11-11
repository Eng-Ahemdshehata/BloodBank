package com.ashehata.bloodbank.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.model.donation.DonationData;
import com.ashehata.bloodbank.data.model.login.LoginData;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.BaseActivity;
import com.ashehata.bloodbank.ui.fragment.DonationDetailsFragment;

import java.util.List;

import butterknife.ButterKnife;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

    private Context context;
    private List<DonationData> datumList;
    private BaseActivity activity;
    private FragmentManager fragmentManager;
//    private List<RestaurantClientData> restaurantDataList = new ArrayList<>();


    public DonationAdapter(Activity activity, Context context, List<DonationData> datumList, FragmentManager fragmentManager) {
        this.activity = (BaseActivity) activity;
        //this.context = activity.getBaseContext();
        this.context = context;
        this.datumList = datumList;
        this.fragmentManager = fragmentManager;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_donation,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setData(holder, position);
        setAction(holder, position);
    }

    private void setData(ViewHolder holder, int position) {

        holder.donationBloodType.setText(datumList.get(position).getBloodType().getName());

        holder.donationName.setText(datumList.get(position).getPatientName());

        holder.donationHospital.setText(datumList.get(position).getHospitalName());

        holder.donationCity.setText(datumList.get(position).getCity().getName());

    }

    private void setAction(ViewHolder holder, int position) {

        holder.donationCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMethod(datumList.get(position).getPhone(), datumList.get(position).getPatientName());

                //HelperMethod.createToast(context,datumList.get(position).getPhone()+"",Toast.LENGTH_SHORT);


            }
        });

        holder.donationDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HelperMethod.createToast(context,""+datumList.get(position).getId(),Toast.LENGTH_SHORT);
                DonationData data = datumList.get(position);
                showDonationDetails(data);

            }
        });
    }

    private void showDonationDetails(DonationData data) {
        DonationDetailsFragment donationDetailsFragment = new DonationDetailsFragment();
        donationDetailsFragment.data = data;
        HelperMethod.ReplaceFragment(activity.getSupportFragmentManager(), donationDetailsFragment, R.id.home_activity_fl_home, true);

    }

    private void callMethod(String phoneNumber, String patientName) {

        HelperMethod.showAlertDialog(context
                , context.getString(R.string.call_him)
                , context.getString(R.string.do_you_want_call) + " " + patientName
                , true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callIntent(phoneNumber);

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HelperMethod.hideAlertDialog(context);

                    }
                }, context.getString(R.string.yes), context.getString(R.string.no));
    }

    private void callIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        String permission = android.Manifest.permission.CALL_PHONE;

        //ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)


        if (HelperMethod.checkGrantedPermission(context, permission)) {

            activity.startActivity(intent);

        } else {
            HelperMethod.onPermission(activity);

        }


        // Permission has already been granted
//        activity.startActivity(intent);
    }

    public void addData(DonationData data) {
        datumList.add(data);
//        notifyDataSetChanged();

    }

    public void clear() {
        datumList.clear();
        notifyDataSetChanged();
    }

    public List<DonationData> getList() {

        return datumList;
    }

    @Override
    public int getItemCount() {
        return datumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView donationBloodType;
        public TextView donationName;
        public TextView donationHospital;
        public TextView donationCity;
        public RelativeLayout donationCall;
        public Button donationDetails;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
            donationBloodType = itemView.findViewById(R.id.donation_blood_type);
            donationName = itemView.findViewById(R.id.donation_name);
            donationHospital = itemView.findViewById(R.id.donation_hospital);
            donationCity = itemView.findViewById(R.id.donation_city);
            donationCall = itemView.findViewById(R.id.donation_call);
            donationDetails = itemView.findViewById(R.id.donation_details);

        }
    }
}
