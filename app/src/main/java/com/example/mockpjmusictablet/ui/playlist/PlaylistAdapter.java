package com.example.mockpjmusictablet.ui.playlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.interfaces.IItemLongClick;
import com.example.mockpjmusictablet.data.model.Playlist;
import com.example.mockpjmusictablet.databinding.ItemPlaylistBinding;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {
    private final List<Playlist> playlists;
    private final Context mContext;
    private final IItemClick callback;
    private final IItemLongClick callback2;
    private int selectedPlaylistPos = -1;

    public PlaylistAdapter(List<Playlist> playlists, Context mContext, IItemClick callback, IItemLongClick callback2) {
        this.playlists = playlists;
        this.mContext = mContext;
        this.callback = callback;
        this.callback2 = callback2;
    }

    @NonNull
    @Override
    public PlaylistAdapter.PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistAdapter.PlaylistHolder(ItemPlaylistBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.PlaylistHolder holder, @SuppressLint("RecyclerView") int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist, position, selectedPlaylistPos);
        holder.itemView.setOnClickListener(view -> {
            callback.onItemClick(position);
        });
        holder.itemView.setOnLongClickListener(view -> {
            callback2.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void selectAlbum(Playlist playlist) {
        this.selectedPlaylistPos = playlists.indexOf(playlist);
    }

    public static class PlaylistHolder extends RecyclerView.ViewHolder {
        private final ItemPlaylistBinding binding;

        public PlaylistHolder(ItemPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Playlist playlist, int position, int selectedPlaylistPos) {
            binding.tvNo.setText(position + 1 + "");
            if (playlist.getNumberOfSongs() > 0) {
                Utils.loadImage(binding.ivAlbum, Utils.getIconSong(Uri.parse(playlist.getSongs().get(0).getPath())));
            }
            binding.tvSongName.setText(playlist.getName());
            binding.tvNumberOfSong.setText(playlist.getNumberOfSongs() + " songs");
            if (position == selectedPlaylistPos) {
                binding.holderView.setBackgroundResource(R.color.blue_light);
            } else {
                binding.holderView.setBackgroundResource(R.color.background);
            }
        }
    }
}
