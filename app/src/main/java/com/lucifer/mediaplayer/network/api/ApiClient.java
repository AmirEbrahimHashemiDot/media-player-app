package com.lucifer.mediaplayer.network.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    //private static final String BASE_URL = "http://192.168.1.10/mh/expert_apis/";
    //private static final String BASE_URL = "http://192.168.220.2/mh/expert_apis/";
    private static final String BASE_URL = "http://192.168.1.13/mh/expert_apis/";
    //private static final String BASE_URL = "http://192.168.1.105/mh/expert_apis/";
    //private static final String BASE_URL = "http://192.168.209.2/mh/expert_apis/";
    //private static final String BASE_URL = "http://192.168.81.2/mh/expert_apis/";
    //private static final String BASE_URL = "http://192.168.80.2/mh/expert_apis/";
    //private static final String BASE_URL = "http://192.168.255.2/mh/expert_apis/";

    public static Retrofit getClient () {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}