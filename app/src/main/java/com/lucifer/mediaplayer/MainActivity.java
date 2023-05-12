package com.lucifer.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lucifer.mediaplayer.network.api.ApiClient;
import com.lucifer.mediaplayer.network.api.ApiService;
import com.lucifer.mediaplayer.network.api.MusicModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvMusic;
    ApiService apiService;
    MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        getAllMusic();
    }

    private void getAllMusic() {
        apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMusic().enqueue(new Callback<List<MusicModel>>() {
            @Override
            public void onResponse(Call<List<MusicModel>> call, Response<List<MusicModel>> response) {

                musicAdapter = new MusicAdapter(response.body());
                rvMusic.setAdapter(musicAdapter);
            }

            @Override
            public void onFailure(Call<List<MusicModel>> call, Throwable t) {
                Log.i("E_M_LOG", "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error in get music list from server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpViews() {
        rvMusic = findViewById(R.id.rv_main_music_list);
        rvMusic.setLayoutManager(new LinearLayoutManager(this));
    }
}