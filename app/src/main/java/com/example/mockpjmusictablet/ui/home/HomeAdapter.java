package com.example.mockpjmusictablet.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.databinding.ItemSongInPlaylistBinding;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    private final List<Song> songs;
    private final Context mContext;
    private int selectedSongPos;

    public HomeAdapter(Context context, List<Song> songs) {
        this.mContext = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeHolder(ItemSongInPlaylistBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position, selectedSongPos);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void selectSong(int pos) {
        this.selectedSongPos = pos;
    }

    public static class HomeHolder extends RecyclerView.ViewHolder {
        private final ItemSongInPlaylistBinding binding;

        public HomeHolder(ItemSongInPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song, int pos, int selectedSongPos) {
            binding.tvNumber.setText(pos + "");
            binding.tvSongName.setText(song.getTitle());
            binding.tvDuration.setText(song.getDurationConverted());
            if (pos == selectedSongPos) {
                binding.holderView.setBackgroundResource(R.color.blue_light);
                binding.sbDuration.setVisibility(ViewGroup.VISIBLE);
            } else {
                binding.holderView.setBackgroundResource(R.color.background);
                binding.sbDuration.setVisibility(ViewGroup.GONE);
            }
        }
    }
}
