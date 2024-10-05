package com.example.mockpjmusictablet.ui.playlist.songs;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.interfaces.IItemLongClick;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.databinding.ItemSongBinding;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.PlaylistSongHolder> {
    private final List<Song> songs;
    private final Context mContext;
    private final IItemClick callback;
    private final IItemLongClick callback2;
    private int selectedSongPos = -1;

    public PlaylistSongAdapter(List<Song> songs, Context mContext, IItemClick callback, IItemLongClick callback2) {
        this.songs = songs;
        this.mContext = mContext;
        this.callback = callback;
        this.callback2 = callback2;
    }

    @NonNull
    @Override
    public PlaylistSongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistSongHolder(ItemSongBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position, selectedSongPos);
        holder.itemView.setOnClickListener(v -> {
            callback.onItemClick(position);
        });
        holder.itemView.setOnLongClickListener(view -> {
            callback2.onItemLongClick(position);
            return true;
        });
    }

    public void selectSong(Song song) {
        this.selectedSongPos = songs.indexOf(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class PlaylistSongHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        public PlaylistSongHolder(@NonNull ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song, int position, int selectedSongPos) {
            binding.tvNo.setText(position + 1 + "");
            binding.tvSongName.setText(song.getTitle());
            binding.tvDuration.setText(song.getDurationConverted());
            Utils.loadImage(binding.ivAlbum, Utils.getIconSong(Uri.parse(song.getPath())));
            if (position == selectedSongPos) {
                binding.holderView.setBackgroundResource(R.color.blue_light);
            } else {
                binding.holderView.setBackgroundResource(R.color.background);
            }
        }
    }
}
