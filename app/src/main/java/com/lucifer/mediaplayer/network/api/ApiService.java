package com.lucifer.mediaplayer.network.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("getmusic.php")
    Call<List<MusicModel>> getMusic();

}
