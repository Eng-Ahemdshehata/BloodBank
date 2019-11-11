package com.ashehata.bloodbank.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashehata.bloodbank.R;
import com.ashehata.bloodbank.adapter.PostsAdapter;
import com.ashehata.bloodbank.data.api.GetDataService;
import com.ashehata.bloodbank.data.api.RetrofitClient;
import com.ashehata.bloodbank.data.local.sharedPreferenceManager.SharedPreferencesManger;
import com.ashehata.bloodbank.data.model.ListModel;
import com.ashehata.bloodbank.data.model.categey.CategeyData;
import com.ashehata.bloodbank.data.model.posts.Posts;
import com.ashehata.bloodbank.data.model.posts.PostsData;
import com.ashehata.bloodbank.data.model.postsFilter.PostsFilter;
import com.ashehata.bloodbank.helper.HelperMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ashehata.bloodbank.helper.HelperMethod.createToast;
import static com.ashehata.bloodbank.helper.HelperMethod.dismissProgressDialog;
import static com.ashehata.bloodbank.helper.HelperMethod.showProgressDialog;

public class PostsFragment extends BaseFragment {

    @BindView(R.id.posts_fragment_rv_posts)
    RecyclerView postsFragmentRvPosts;
    @BindView(R.id.posts_fragment_pb_indicator)
    ProgressBar postsFragmentPbIndicator;
    @BindView(R.id.posts_fragment_spin_categy)
    Spinner postsFragmentSpinFilter;
    @BindView(R.id.posts_fragment_et_filter_name)
    EditText postsFragmentEtFilterName;
    @BindView(R.id.posts_fragment_iv_make_filter)
    ImageView postsFragmentIvMakeFilter;
    private GetDataService getDataService;
    LinearLayoutManager linearLayoutManager;
    // DividerItemDecoration dividerItemDecoration;
    PostsAdapter postsAdapter;
    List<PostsData> datumList = new ArrayList<>();
    int pastItems, visibleItems, totalItems;
    boolean loading = true;
    private int nextPage;
    private String mToken;
    private int id;
    private String keyWord;

    /*
    // Post Details
    public String postTitle ;
    public String postDescription ;
    public String postTitleImage ;

     */

    public PostsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, view);

        getDataService = RetrofitClient.getRetrofitInstance().create(GetDataService.class);

        setRecyclerConfig();

//        if (datumList.size() == 0) {

        if (HelperMethod.InternetState.isConnected(getContext())) {

            getPosts(1);
            loadNextPage();
        } else {
            getDataFromPreference();
            // show message for user
            //createSnackBar(getView(),getString(R.string.connect_internet),getContext(),null,null);
        }
//        }

        //showPostDetails();

        // Fill spinner with items
        fillSpinner(getActivity());


        return view;
    }

    private void fillSpinner(Activity activity) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                ListModel listModel = SharedPreferencesManger.loadUserListModel(activity);
                if (listModel != null) {
                    List<String> stringList = new ArrayList<>();
                    for (int i = 0; i < listModel.getCategories().size(); i++) {

                        CategeyData data = listModel.getCategories().get(i);
                        stringList.add(data.getName());
                        HelperMethod.setSpinner(activity, postsFragmentSpinFilter, stringList);

                    }
                }

            }

        });


    }


    private void setRecyclerConfig() {
        // Set items on linear manager
        linearLayoutManager = new LinearLayoutManager(getContext());
        postsFragmentRvPosts.setLayoutManager(linearLayoutManager);

        // Set divider for recycler view

        /*
        dividerItemDecoration = new DividerItemDecoration(postsFragmentRvPosts.getContext(),
                linearLayoutManager.getOrientation());

        postsFragmentRvPosts.addItemDecoration(dividerItemDecoration);

         */

        // Fixed size
        postsFragmentRvPosts.setHasFixedSize(true);

    }

    private void getDataFromPreference() {
        // write here ..
        Posts posts = SharedPreferencesManger.loadUserPosts(getActivity());
        //createToast(getContext(),posts.getData().getData().get(0).getTitle()+"",Toast.LENGTH_SHORT);
        postsAdapter = new PostsAdapter(getActivity(), getContext(), posts.getData().getData());

        // Hide progress bar indicator
        postsFragmentPbIndicator.setVisibility(View.GONE);

        // Set adapter for recycler
        postsFragmentRvPosts.setAdapter(postsAdapter);

    }

    private void getPosts(int pageNumber) {
        mToken = SharedPreferencesManger.LoadData(getActivity(), SharedPreferencesManger.USER_API_TOKEN);
        Call<Posts> postsCall = getDataService.getPosts(mToken, pageNumber);
        Log.v("myToken", mToken + "");

        postsCall.enqueue(new Callback<Posts>() {
            @Override
            public void onResponse(Call<Posts> call, Response<Posts> response) {
                Posts posts = response.body();
                if (posts.getStatus() == 1) {

                    // Hide progress bar indicator
                    postsFragmentPbIndicator.setVisibility(View.GONE);
                    //HelperMethod.createToast(getContext(),posts.getMsg(), Toast.LENGTH_SHORT);

                    // Increase next page number
                    nextPage++;

                    // Get the list from api
                    datumList = posts.getData().getData();

                    //save the first page of posts from api
                    savePostsInPreference(posts);

                    //set adapter
                    postsAdapter = new PostsAdapter(getActivity(), getContext(), datumList);

                    // Set adapter for recycler
                    postsFragmentRvPosts.setAdapter(postsAdapter);

                } else {
                    // Hide progress bar indicator
                    postsFragmentPbIndicator.setVisibility(View.GONE);
                    createToast(getContext(), posts.getMsg(), Toast.LENGTH_SHORT);

                }

            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t) {
                getDataFromPreference();

                // Hide progress bar indicator
                postsFragmentPbIndicator.setVisibility(View.GONE);

                createToast(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);

            }
        });

    }

    private void savePostsInPreference(Posts posts) {

        SharedPreferencesManger.SaveData(getActivity(), SharedPreferencesManger.USER_POSTS, posts);
        //SharedPreferencesManger.saveUserPosts(getActivity(), posts);


    }

    private void loadNextPage() {
        postsFragmentRvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItems = linearLayoutManager.getChildCount();
                    totalItems = linearLayoutManager.getItemCount();
                    pastItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if (visibleItems + pastItems >= totalItems) {

                            loading = false;
                            loadMore();


                        }
                    }
                } else
                    loading = true;
            }
        });
    }

    private void loadMore() {

        Call<Posts> postsCall = getDataService.getPosts(mToken, 2);

        postsCall.enqueue(new Callback<Posts>() {
            @Override
            public void onResponse(Call<Posts> call, Response<Posts> response) {
                Posts posts = response.body();
                if (posts.getStatus() == 1) {

                    if (posts.getData().getData().size() != 0) {

                        Toast.makeText(getContext(), nextPage + "", Toast.LENGTH_SHORT).show();
                        nextPage++;
                        for (int i = 0; i < posts.getData().getData().size(); i++) {
                            postsAdapter.addData(posts.getData().getData().get(i));
                        }
                        postsAdapter.notifyDataSetChanged();
                    } else
                        createToast(getContext(),
                                getString(R.string.no_more), Toast.LENGTH_SHORT);

                } else {

                    createToast(getContext(), posts.getMsg(), Toast.LENGTH_SHORT);

                }

            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t) {

                createToast(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);

            }
        });
    }



    @OnClick(R.id.posts_fragment_iv_make_filter)
    public void onViewClicked() {
        HelperMethod.disappearKeypad(getActivity(),getView());
        id =  postsFragmentSpinFilter.getSelectedItemPosition()+1;
        keyWord = postsFragmentEtFilterName.getText().toString().trim();
        if (id == 0){
            createToast(getContext(),getString(R.string.insert_categery),Toast.LENGTH_SHORT);
        }else {
            if (HelperMethod.InternetState.isConnected(getContext())) {
                filterPosts(1);

            }else
                createToast(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT);
        }
    }

    private void filterPosts(int pageNum) {
        showProgressDialog(getActivity(),getString(R.string.wait_moment));
        String apiToken ="";
        if(mToken != null && ! mToken.isEmpty()){
            apiToken = mToken ;
        }else {
            apiToken = SharedPreferencesManger.LoadData(getActivity(),SharedPreferencesManger.USER_API_TOKEN);
        }

        getDataService.getPostsFilter(apiToken,pageNum,"",id).enqueue(new Callback<PostsFilter>() {
            @Override
            public void onResponse(Call<PostsFilter> call, Response<PostsFilter> response) {

                dismissProgressDialog();
                PostsFilter postsFilter = response.body();

                if (postsFilter.getStatus() == 1) {

                    PostsAdapter postsAdapter = new PostsAdapter(getActivity(),getContext(),postsFilter.getData().getData());
                    if( postsAdapter.getItemCount() != 0){
                        postsFragmentRvPosts.setAdapter(postsAdapter);

                    }else {
                        createToast(getContext(),getString(R.string.no_result),Toast.LENGTH_SHORT);

                    }
                    createToast(getContext(),postsFilter.getMsg(),Toast.LENGTH_SHORT);


                }else {
                    createToast(getContext(),postsFilter.getMsg(),Toast.LENGTH_SHORT);

                }
            }

            @Override
            public void onFailure(Call<PostsFilter> call, Throwable t) {

                dismissProgressDialog();
                createToast(getContext(),getString(R.string.error),Toast.LENGTH_SHORT);

            }
        });
    }
    /*
    private void showPostDetails() {
        PostDetailsFragment postDetailsFragment = new PostDetailsFragment();
        postDetailsFragment.postTitle = "";
        postDetailsFragment.postDescription = "";
        postDetailsFragment.postImage = "";
        HelperMethod.ReplaceFragment(getFragmentManager(), postDetailsFragment, R.id.home_activity_fl_home, true);
    }
     */
}
