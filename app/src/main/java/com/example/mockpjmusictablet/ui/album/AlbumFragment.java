package com.example.mockpjmusictablet.ui.album;


import static com.example.mockpjmusictablet.utils.Const.ALBUM_SONGS_FRAGMENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.model.Album;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.view_model.SongViewModel;
import com.example.mockpjmusictablet.databinding.FragmentAlbumBinding;
import com.example.mockpjmusictablet.ui.album.list_song.AlbumListSongFragment;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.HashSet;
import java.util.List;

public class AlbumFragment extends Fragment implements IItemClick {
    private FragmentAlbumBinding binding;
    private List<Album> albums = Utils.getAlbums();
    private SongViewModel viewModel;
    private AlbumAdapter adapter;
    private int selectedAlbum = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlbumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        initViews();
    }

    private void initViews() {
        binding.tvNumberOfAlbum.setText(albums.size() + "");
        adapter = new AlbumAdapter(albums, requireActivity(), this);
        if (selectedAlbum >= 0) {
            adapter.selectAlbum(albums.get(selectedAlbum));

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false);
        binding.rvListSong.setLayoutManager(layoutManager);
        binding.rvListSong.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int pos) {
        viewModel.setPositionAlbum(pos);
        adapter.selectAlbum(albums.get(pos));
        adapter.notifyDataSetChanged();
        replaceLeftFragment(new AlbumListSongFragment(), ALBUM_SONGS_FRAGMENT);
    }

    private void replaceLeftFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view_left, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void setAlbumPos(List<Song> songs) {
        for (int i = 0; i < albums.size(); i++) {
            if (new HashSet<>(albums.get(i).getSongs()).containsAll(songs)) {
                selectedAlbum = i;
            } else {
                return;
            }
        }
    }
}
