package com.edwin.otpapp;
        import com.edwin.otpapp.Sms;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.GET;
        import retrofit2.http.POST;
        import retrofit2.http.Query;

interface APIInterface {

    @POST("/sendSMS.php")
    Call<Sms> createSMS(@Body Sms sms);

    @GET("/posts/5")
    Call<Sms> doGetListAlbums();

//    @POST("/api/users")
//    Call<Sms> createUser(@Body Sms sms);
}

