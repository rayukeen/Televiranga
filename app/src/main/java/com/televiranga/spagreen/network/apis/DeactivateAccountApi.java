package com.televiranga.spagreen.network.apis;

import com.televiranga.spagreen.network.model.ResponseStatus;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DeactivateAccountApi  {

    @POST("deactivate_account")
    @FormUrlEncoded
    Call<ResponseStatus> deactivateAccount(@Field("id") String id, @Field("password") String password,
                                           @Field("reason") String reason,
                                           @Field("api_secret_key") String key);


}
