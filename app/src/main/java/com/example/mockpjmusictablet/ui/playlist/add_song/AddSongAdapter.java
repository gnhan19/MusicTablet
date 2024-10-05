package com.example.mockpjmusictablet.ui.playlist.add_song;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.databinding.ItemSongBinding;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.List;

public class AddSongAdapter extends RecyclerView.Adapter<AddSongAdapter.SongHolder> {
    private final List<Song> songs;
    private final Context mContext;
    private final IItemClick callback;

    public AddSongAdapter(List<Song> songs, Context mContext, IItemClick callback) {
        this.songs = songs;
        this.mContext = mContext;
        this.callback = callback;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongHolder(ItemSongBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position);
        holder.itemView.setOnClickListener(v -> {
            song.setSelected(!song.isSelected());
            callback.onItemClick(song);
            holder.setSelectedSong(song);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        public SongHolder(@NonNull ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song, int position) {
            binding.tvNo.setText(position + 1 + "");
            binding.tvSongName.setText(song.getTitle());
            binding.tvDuration.setText(song.getDurationConverted());
            Utils.loadImage(binding.ivAlbum, Utils.getIconSong(Uri.parse(song.getPath())));
        }

        public void setSelectedSong(Song song) {
            binding.holderView.setBackgroundResource(song.isSelected() ? R.color.blue_light : R.color.background);
        }
    }

    public interface IItemClick {
        void onItemClick(Song song);
    }
}
