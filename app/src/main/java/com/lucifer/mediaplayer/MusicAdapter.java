package com.lucifer.mediaplayer;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.lucifer.mediaplayer.network.api.MusicModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    List<MusicModel> musicList;
    OnMusicItemClickListener onMusicItemClickListener;


    public MusicAdapter(List<MusicModel> musicList, OnMusicItemClickListener onMusicItemClickListener) {
        this.musicList = musicList;
        this.onMusicItemClickListener = onMusicItemClickListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {

        Picasso.get().load(musicList.get(position).getPic()).into(holder.imgVSingerPic);
        holder.tvTitle.setText(musicList.get(position).getTitle());
        holder.tvSingerName.setText(musicList.get(position).getSinger());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMusicItemClickListener.onMusicItemClick(musicList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVSingerPic;
        TextView tvTitle, tvSingerName;
        AppCompatButton btnDownload;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVSingerPic = itemView.findViewById(R.id.imgv_singer_pic);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSingerName = itemView.findViewById(R.id.tv_singer_name);
            btnDownload = itemView.findViewById(R.id.btn_download_music);
        }
    }

    public interface OnMusicItemClickListener {
        public void onMusicItemClick(MusicModel music);
    }
}
