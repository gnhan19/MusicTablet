package com.example.mockpjmusictablet.ui.playlist.add_song;

import static com.example.mockpjmusictablet.utils.Const.PLAYLIST_NAME;
import static com.example.mockpjmusictablet.utils.Const.PLAYLIST_SONGS_FRAGMENT;

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
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.databinding.FragmentAddSongsBinding;
import com.example.mockpjmusictablet.ui.playlist.songs.SongsInPlaylistFragment;
import com.example.mockpjmusictablet.utils.PlaylistManager;
import com.example.mockpjmusictablet.utils.Utils;
import com.example.mockpjmusictablet.view_model.SongViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddSongsFragment extends Fragment implements AddSongAdapter.IItemClick {
    private FragmentAddSongsBinding binding;
    private PlaylistManager playlistManager;
    private SongViewModel viewModel;
    private List<Song> allSongs;
    private List<Song> songsNeedToAdd = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddSongsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        allSongs = Utils.getListSongOffline(requireContext());
        playlistManager = new PlaylistManager(requireContext());
        initViews();
    }

    private void initViews() {
        String playlistName;
        Bundle bundle = getArguments();
        if (bundle != null) {
            playlistName = bundle.getString(PLAYLIST_NAME);
        } else {
            return;
        }
        List<Song> duplicateSongs = new ArrayList<>();
        viewModel.getCurrentPlaylist().observe(getViewLifecycleOwner(), playlist -> {
            List<Song> plSongs = playlist.getSongs();

            Set<String> playlistSongTitles = new HashSet<>();
            for (Song song : plSongs) {
                playlistSongTitles.add(song.getTitle());
            }

            for (Song song : allSongs) {
                if (playlistSongTitles.contains(song.getTitle())) {
                    duplicateSongs.add(song);
                }
            }
            allSongs.removeAll(duplicateSongs);
        });
        AddSongAdapter adapter = new AddSongAdapter(allSongs, requireActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false);
        binding.rvListSong.setLayoutManager(layoutManager);
        binding.rvListSong.setAdapter(adapter);

        binding.btnAdd.setOnClickListener(v -> {
            for (Song song : songsNeedToAdd) {
                if (song.isSelected()) {
                    playlistManager.addSongToPlaylist(playlistName, song);
                }
            }
            viewModel.setPlaylists(playlistManager.loadPlaylists());
            viewModel.setCurrentPlaylist(playlistManager.findPlaylistByName(playlistName));
            backToPreviousFragment();
        });

        binding.btnClose.setOnClickListener(v -> {
            backToPreviousFragment();
        });
    }

    private void backToPreviousFragment() {
        SongsInPlaylistFragment fragment = new SongsInPlaylistFragment();
        replaceLeftFragment(fragment);
    }

    private void replaceLeftFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentByTag(PLAYLIST_SONGS_FRAGMENT);

        if (existingFragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view_left, existingFragment, PLAYLIST_SONGS_FRAGMENT)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view_left, fragment, PLAYLIST_SONGS_FRAGMENT)
                    .addToBackStack(PLAYLIST_SONGS_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onItemClick(Song song) {
        songsNeedToAdd.add(song);
    }
}
