package com.example.mockpjmusictablet.ui.album.list_song;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.databinding.ItemSongBinding;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.List;

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.AlbumSongHolder> {
    private final List<Song> songs;
    private final Context mContext;
    private int selectedSongPos = -1;
    private final IItemClick callback;

    public AlbumSongAdapter(List<Song> songs, Context mContext, IItemClick callback) {
        this.songs = songs;
        this.mContext = mContext;
        this.callback = callback;
    }

    @NonNull
    @Override
    public AlbumSongAdapter.AlbumSongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumSongHolder(ItemSongBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumSongAdapter.AlbumSongHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position, selectedSongPos);
        holder.itemView.setOnClickListener(v -> {
            callback.onItemClick(position);
        });
    }

    public void selectSong(Song song) {
        this.selectedSongPos = songs.indexOf(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class AlbumSongHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        public AlbumSongHolder(@NonNull ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song, int position, int selectedSongPos) {
            binding.tvNo.setText(position + 1+ "");
            binding.tvSongName.setText(song.getTitle());
            binding.tvDuration.setText(song.getDurationConverted());
            Utils.loadImage(binding.ivAlbum, song.getIcon());
            if (position == selectedSongPos) {
                binding.holderView.setBackgroundResource(R.color.blue_light);
            } else {
                binding.holderView.setBackgroundResource(R.color.background);
            }
        }
    }
}
