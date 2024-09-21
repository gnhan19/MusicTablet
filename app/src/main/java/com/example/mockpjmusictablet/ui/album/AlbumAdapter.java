package com.example.mockpjmusictablet.ui.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.model.Album;
import com.example.mockpjmusictablet.databinding.ItemAlbumsBinding;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {
    private final List<Album> albums;
    private final Context mContext;
    private final IItemClick callback;
    private int selectedAlbumPos = -1;

    public AlbumAdapter(List<Album> albums, Context mContext, IItemClick callback) {
        this.albums = albums;
        this.mContext = mContext;
        this.callback = callback;
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumHolder(ItemAlbumsBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {
        Album album = albums.get(position);
        holder.bind(album, position, selectedAlbumPos);
        holder.itemView.setOnClickListener(view -> {
            callback.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void selectAlbum(Album album) {
        this.selectedAlbumPos = albums.indexOf(album);
    }

    public static class AlbumHolder extends RecyclerView.ViewHolder {
        private final ItemAlbumsBinding binding;

        public AlbumHolder(ItemAlbumsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Album album, int position, int selectedAlbumPos) {
            binding.tvNo.setText(position + 1 +"");
            Utils.loadImage(binding.ivAlbum, album.getIcon());
            binding.tvSongName.setText(album.getName());
            binding.tvNumberOfSong.setText(album.getQuantity() + " songs");
            if (position == selectedAlbumPos) {
                binding.holderView.setBackgroundResource(R.color.blue_light);
            } else {
                binding.holderView.setBackgroundResource(R.color.background);
            }
        }
    }
}
