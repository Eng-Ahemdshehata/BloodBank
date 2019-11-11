package com.ashehata.bloodbank.data.api;

import com.ashehata.bloodbank.data.model.GeneralResponse;
import com.ashehata.bloodbank.data.model.bloodType.BloodType;
import com.ashehata.bloodbank.data.model.categey.Categey;
import com.ashehata.bloodbank.data.model.cities.Cities;
import com.ashehata.bloodbank.data.model.contactUs.ContactUs;
import com.ashehata.bloodbank.data.model.createAccount.CreateAccount;
import com.ashehata.bloodbank.data.model.donation.Donation;
import com.ashehata.bloodbank.data.model.donationRequest.DonationRequest;
import com.ashehata.bloodbank.data.model.governorates.Governorates;
import com.ashehata.bloodbank.data.model.login.Login;
import com.ashehata.bloodbank.data.model.notificationCount.NotificationCount;
import com.ashehata.bloodbank.data.model.postFavourite.PostFavourite;
import com.ashehata.bloodbank.data.model.posts.Posts;
import com.ashehata.bloodbank.data.model.postsFilter.PostsFilter;
import com.ashehata.bloodbank.data.model.resetPassword.ResetPassword;
import com.ashehata.bloodbank.data.model.updateProfile.UpdateProfile;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetDataService {


    @POST("login")
    @FormUrlEncoded
    Call<Login> getUser(@Field("phone") String phone,
                        @Field("password") String password);

    @POST("reset-password")
    @FormUrlEncoded
    Call<ResetPassword> resetPassword(@Field("phone") String phone);


    @POST("new-password")
    @FormUrlEncoded
    Call<GeneralResponse> setNewPassword(@Field("password") String password
            , @Field("password_confirmation") String passwordConfirm
            , @Field("pin_code") String code
            , @Field("phone") String phone);

    @POST("signup")
    @FormUrlEncoded
    Call<CreateAccount> createAccount(
            @Field("name") String name
            , @Field("email") String mail
            , @Field("birth_date") String birth
            , @Field("city_id") String city
            , @Field("phone") String phone
            , @Field("donation_last_date") String donationLastDate
            , @Field("password") String password
            , @Field("password_confirmation") String passwordConfirm
            , @Field("blood_type_id") String bloodType);

    /*
     * if the method take a parameter
     * @Query("api_token") String api_token
     */


    @GET("blood-types")
    Call<BloodType> getBloodType();


    @GET("governorates")
    Call<Governorates> getGovernorates();

    @GET("categories")
    Call<Categey> getCategories();


    @GET("cities")
    Call<Cities> getCities(@Query("governorate_id") String governorate_id);


    @GET("posts")
    Call<Posts> getPosts(@Query("api_token") String apiToken
            , @Query("page") int pageNum);


    @GET("donation-requests")
    Call<Donation> getDonation(@Query("api_token") String apiToken
            , @Query("page") int pageNum);

    @GET("notifications-count")
    Call<NotificationCount> getNotificationCount(@Query("api_token") String apiToken);

    @POST("contact")
    @FormUrlEncoded
    Call<ContactUs> contactUs(@Field("api_token") String apiToken
            , @Field("title") String title
            , @Field("message") String message);


    @POST("profile")
    @FormUrlEncoded
    Call<UpdateProfile> updateProfile(
            @Field("name") String name
            , @Field("email") String mail
            , @Field("birth_date") String birth
            , @Field("city_id") String city
            , @Field("phone") String phone
            , @Field("donation_last_date") String donationLastDate
            , @Field("password") String password
            , @Field("password_confirmation") String passwordConfirm
            , @Field("blood_type_id") String bloodType
            , @Field("api_token") String apiToken);


    @POST("donation-request/create")
    @FormUrlEncoded
    Call<DonationRequest> makeDonationRequest(@Field("api_token") String apiToken,
                                              @Field("patient_name") String patientName,
                                              @Field("patient_age") String patientAge,
                                              @Field("blood_type_id") String bloodTypeId,
                                              @Field("bags_num") String bagsNum,
                                              @Field("hospital_name") String hospitalName,
                                              @Field("hospital_address") String hospitalAddress,
                                              @Field("city_id") String cityId,
                                              @Field("phone") String phone,
                                              @Field("notes") String notes,
                                              @Field("latitude") String longitude,
                                              @Field("longitude") String latitude);


    @GET("posts")
    Call<PostsFilter> getPostsFilter(@Query("api_token") String apiToken
            , @Query("page") int pageNum ,@Query("keyword") String keyWord ,@Query("category_id") int catId );


    @POST("post-toggle-favourite")
    @FormUrlEncoded
    Call<PostFavourite> addRemovePostFavourite(@Field("api_token") String apiToken,
                                               @Field("post_id") int postId);



}
