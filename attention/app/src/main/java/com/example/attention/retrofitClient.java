package com.example.attention;


import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class retrofitClient {

    private static final String  BASE_URL ="http://172.22.3.100:3000";
    private static retrofitClient mInstance;
    private Retrofit retrofit;

    private retrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized  retrofitClient getInstance(){

        if(mInstance ==null){
            mInstance = new retrofitClient();

        }
        return mInstance;
    }

    public RetrofitInterface getRetrofitInterface(){
        return retrofit.create(RetrofitInterface.class);
    }

}
