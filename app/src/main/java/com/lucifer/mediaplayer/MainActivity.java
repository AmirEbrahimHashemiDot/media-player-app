package com.lucifer.mediaplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.lucifer.mediaplayer.network.api.ApiClient;
import com.lucifer.mediaplayer.network.api.ApiService;
import com.lucifer.mediaplayer.network.api.MusicModel;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvMusic;
    ApiService apiService;
    MusicAdapter musicAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    public MediaPlayer mediaPlayer;
    ConstraintLayout musicControllerBarLayout;
    TextView tvCurrentTime, tvDurationTime, tvCurrentMusicTitle;
    ImageView playPauseImgBtn, backwardImgBtn, forwardImgBtn;
    public Slider musicSlider;
    public Timer timer;
    private String PPBtnStatus = "pause";
    public Thread musicThread;
    public String currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        getAllMusic();
    }

    public String convertTimeToString(long duration) {
        long sec = (duration / 1000) % 60;
        long min = (duration / (1000 * 60)) % 60;
        return String.format(Locale.US, "%02d:%02d", min, sec);
    }

    private void getAllMusic() {
        apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMusic().enqueue(new Callback<List<MusicModel>>() {
            @Override
            public void onResponse(Call<List<MusicModel>> call, Response<List<MusicModel>> response) {
                musicAdapter = new MusicAdapter(response.body(), new MusicAdapter.OnMusicItemClickListener() {
                    @Override
                    public void onMusicItemClick(MusicModel music) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            //mediaPlayer.release();
                            mediaPlayer = null;
                            setUpMediaPlayer(music, response.body());
                        } else {
                            setUpMediaPlayer(music, response.body());
                        }
                    }
                });
                musicAdapter.notifyDataSetChanged();
                rvMusic.setAdapter(musicAdapter);
            }

            @Override
            public void onFailure(Call<List<MusicModel>> call, Throwable t) {
                Log.i("E_M_LOG", "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error in get music list from server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpMediaPlayer(MusicModel music, List<MusicModel> musicList) {
        musicControllerBarLayout.setVisibility(View.VISIBLE);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(music.getMusic()));

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                timer = new Timer();
                mediaPlayer.start();

                tvDurationTime.setText(convertTimeToString(mediaPlayer.getDuration()));
                tvCurrentTime.setText(convertTimeToString(mediaPlayer.getCurrentPosition()));
                PPBtnStatus = "play";
                playPauseImgBtn.setImageResource(R.drawable.baseline_pause_24);

                musicSlider.setValueTo(mediaPlayer.getDuration());
                tvCurrentMusicTitle.setText(music.getTitle() + ", " + music.getSinger());
                currentPosition = convertTimeToString(mediaPlayer.getCurrentPosition());
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //tvCurrentTime.setText(currentPosition);
                                tvCurrentTime.setText(convertTimeToString(mediaPlayer.getCurrentPosition()));
                                try {
                                    if (mediaPlayer.getCurrentPosition() <= mediaPlayer.getDuration()) {
                                        musicSlider.setValue(mediaPlayer.getCurrentPosition());
                                    } else {
                                        timer.cancel();
                                        musicSlider.setValue(0);
                                        //mediaPlayer = null;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "error2", Toast.LENGTH_SHORT).show();
                                    Log.i("TAG_E", "e: " + e.getMessage());
                                }
                            }
                        });
                    }
                }, 1000, 1000);
            }
        });
    }

    private void setUpViews() {
        tvCurrentTime = findViewById(R.id.tv_show_current_time);
        tvDurationTime = findViewById(R.id.tv_duration_time);
        playPauseImgBtn = findViewById(R.id.play_pause_btn_controller);
        backwardImgBtn = findViewById(R.id.btn_backward_music_controller);
        forwardImgBtn = findViewById(R.id.btn_forward_music_controller);
        musicSlider = findViewById(R.id.music_controller_slider);
        rvMusic = findViewById(R.id.rv_main_music_list);
        rvMusic.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.swp_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllMusic();
                musicAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        musicControllerBarLayout = findViewById(R.id.music_controller_bar_layout);
        playPauseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PPBtnStatus == "play") {
                    PPBtnStatus = "pause";
                    mediaPlayer.pause();
                    playPauseImgBtn.setImageResource(R.drawable.baseline_play_arrow_24);
                } else {
                    PPBtnStatus = "play";
                    mediaPlayer.start();
                    playPauseImgBtn.setImageResource(R.drawable.baseline_pause_24);
                }
            }
        });
        musicSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                mediaPlayer.seekTo((int) slider.getValue());
            }
        });

        tvCurrentMusicTitle = findViewById(R.id.tv_current_music_title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}