package com.ashehata.bloodbank.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.postFavourite.PostFavourite;
import com.ashehata.bloodbank.data.model.posts.PostsData;
import com.ashehata.bloodbank.helper.HelperMethod;
import com.ashehata.bloodbank.ui.activity.BaseActivity;
import com.ashehata.bloodbank.ui.fragment.PostDetailsFragment;

import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ashehata.bloodbank.helper.HelperMethod.createToast;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private View.OnClickListener onClickListener;
    private Context context;
    private BaseActivity activity;
    private List<PostsData> datumList;
    private FragmentManager fragmentManager;
    private String mtoken;
    private GetDataService getDataService;

//    private List<RestaurantClientData> restaurantDataList = new ArrayList<>();


    public PostsAdapter(Activity activity, Context context, List<PostsData> datumList) {
        this.activity = (BaseActivity) activity;
        this.context = context;
        this.datumList = datumList;
        //this.fragmentManager = fragmentManager;
        mtoken = SharedPreferencesManger.LoadData(activity, SharedPreferencesManger.USER_API_TOKEN);
        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_post,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setData(holder, position);
        setAction(holder, position);
    }

    private void setData(ViewHolder holder, int position) {
        holder.postName.setText(datumList.get(position).getTitle());
        String imageUrl = datumList.get(position).getThumbnailFullPath();
        HelperMethod.onLoadImageFromUrl(holder.postImage, imageUrl, context, 0);
        if (datumList.get(position).getIsFavourite()) {
            holder.postfavou.setImageResource(R.drawable.ic_heart_bold);
        } else {
            holder.postfavou.setImageResource(R.drawable.ic_heart_light);
        }
    }

    private void setAction(ViewHolder holder, int position) {

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostsData data = datumList.get(position);
                showPostDetails(data.getTitle(), data.getContent(), data.getThumbnailFullPath());
            }
        });


        holder.postfavou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (HelperMethod.InternetState.isConnected(context)) {
                    if (!mtoken.isEmpty()) {

                        addOrRemovePostFromFavourite(holder, position);

                    }
                } else {
                    createToast(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void addOrRemovePostFromFavourite(ViewHolder holder, int position) {

        getDataService.addRemovePostFavourite(mtoken, datumList.get(position).getId()).enqueue(new Callback<PostFavourite>() {
            @Override
            public void onResponse(Call<PostFavourite> call, Response<PostFavourite> response) {

                PostFavourite postFavourite = response.body();
                if (postFavourite.getStatus() == 1) {

                    datumList.get(position).setIsFavourite(!datumList.get(position).getIsFavourite());
                    //createToast(context, context.getString(R.string.post_deleted), Toast.LENGTH_SHORT);

                    if (datumList.get(position).getIsFavourite()) {
                        holder.postfavou.setImageResource(R.drawable.ic_heart_bold);
                    } else {
                        holder.postfavou.setImageResource(R.drawable.ic_heart_light);
                    }

                } else {
                    //createToast(context, context.getString(R.string.post_no_removed), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<PostFavourite> call, Throwable t) {

                createToast(context, context.getString(R.string.error), Toast.LENGTH_SHORT);
            }
        });
    }


    public void addData(PostsData data) {
        datumList.add(data);

    }



    private void showPostDetails(String title, String description, String imageUrl) {
        PostDetailsFragment postDetailsFragment = new PostDetailsFragment();
        postDetailsFragment.postTitle = title;
        postDetailsFragment.postDescription = description;
        postDetailsFragment.postImage = imageUrl;
        HelperMethod.ReplaceFragment(activity.getSupportFragmentManager(), postDetailsFragment, R.id.home_activity_fl_home, true);
    }

    @Override
    public int getItemCount() {
        return datumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView postName;
        public ImageView postImage;
        public ImageView postfavou;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
            postName = itemView.findViewById(R.id.post_title);
            postImage = itemView.findViewById(R.id.post_image);
            postfavou = itemView.findViewById(R.id.post_favourate);
        }
    }
}
