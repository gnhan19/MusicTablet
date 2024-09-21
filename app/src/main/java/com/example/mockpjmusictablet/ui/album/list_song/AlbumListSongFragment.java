package com.example.mockpjmusictablet.ui.album.list_song;

import static com.example.mockpjmusictablet.utils.Const.ALBUM_FRAGMENT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.model.Album;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.data.view_model.SongViewModel;
import com.example.mockpjmusictablet.databinding.FragmentListSongAlbumBinding;
import com.example.mockpjmusictablet.media.MediaManager;
import com.example.mockpjmusictablet.service.MusicService;
import com.example.mockpjmusictablet.ui.MainActivity;
import com.example.mockpjmusictablet.ui.album.AlbumFragment;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AlbumListSongFragment extends Fragment implements IItemClick {
    private MediaManager mediaManager;
    private FragmentListSongAlbumBinding binding;
    private SongViewModel viewModel;
    private final List<Song> songList = new ArrayList<>();
    private MainActivity mainActivity;
    private AlbumSongAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mediaManager = MediaManager.getInstance(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListSongAlbumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        initViews();
    }

    private void initViews() {
        List<Album> albums = Utils.getAlbums();
        viewModel.getPositionAlbum().observe(requireActivity(), position -> {
            List<Song> songs = albums.get(position).getSongs();
            songList.addAll(songs);
            binding.setSong(songs.get(0));
            binding.tvNumberOfAlbum.setText(songs.size() + "");
            adapter = new AlbumSongAdapter(songs, requireActivity(), this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false);
            binding.rvListSong.setLayoutManager(layoutManager);
            binding.rvListSong.setAdapter(adapter);

            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlbumFragment fragment = new AlbumFragment();
                    fragment.setAlbumPos(songs);
                    replaceLeftFragment(fragment, ALBUM_FRAGMENT);
                }
            });
        });
    }

    private void replaceLeftFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        if (existingFragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view_left, existingFragment, tag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view_left, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
        }
    }

    @Override
    public void onItemClick(int pos) {
        viewModel.getListSongs().observe(getViewLifecycleOwner(), songs -> {
            if (!songList.containsAll(songs)) {
                viewModel.setListSongs(songList);
                viewModel.selectSong(songList.get(pos));
                if (!mainActivity.isBound()) {
                    Intent intent = new Intent(mainActivity, MusicService.class);
                    mainActivity.startService(intent);
                    mainActivity.bindService(intent, mainActivity.serviceConnection, Context.BIND_AUTO_CREATE);
                }
                mediaManager.setListSongs(songList);
                mediaManager.setCurrentIndex(pos);
                mediaManager.play(true);
                adapter.selectSong(songList.get(pos));
                adapter.notifyDataSetChanged();
            }
        });
    }
}
