package com.example.mockpjmusictablet.ui.songs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.databinding.ItemSongInPlaylistBinding;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    private final List<Song> songs;
    private final Context mContext;
    private int selectedSongPos;

    public SongAdapter(Context context, List<Song> songs) {
        this.mContext = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongHolder(ItemSongInPlaylistBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position, selectedSongPos);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void selectSong(Song song) {
        this.selectedSongPos = songs.indexOf(song);
    }

    public static class SongHolder extends RecyclerView.ViewHolder {
        private final ItemSongInPlaylistBinding binding;

        public SongHolder(ItemSongInPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song, int pos, int selectedSongPos) {
            binding.tvNumber.setText(pos + "");
            binding.tvSongName.setText(song.getTitle());
            binding.tvDuration.setText(song.getDurationConverted());
            if (pos == selectedSongPos) {
                binding.holderView.setBackgroundResource(R.color.blue_light);
            } else {
                binding.holderView.setBackgroundResource(R.color.background);
            }
        }
    }
}
