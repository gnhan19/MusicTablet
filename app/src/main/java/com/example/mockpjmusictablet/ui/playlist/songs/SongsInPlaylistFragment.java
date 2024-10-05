package com.example.mockpjmusictablet.ui.playlist.songs;

import static com.example.mockpjmusictablet.utils.Const.PLAYLIST_ADD_SONGS_FRAGMENT;
import static com.example.mockpjmusictablet.utils.Const.PLAYLIST_FRAGMENT;
import static com.example.mockpjmusictablet.utils.Const.PLAYLIST_NAME;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.activity.MainActivity;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.interfaces.IItemLongClick;
import com.example.mockpjmusictablet.data.model.Playlist;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.databinding.FragmentListSongPlaylistBinding;
import com.example.mockpjmusictablet.media.MediaManager;
import com.example.mockpjmusictablet.service.MusicService;
import com.example.mockpjmusictablet.ui.playlist.PlaylistFragment;
import com.example.mockpjmusictablet.ui.playlist.add_song.AddSongsFragment;
import com.example.mockpjmusictablet.utils.PlaylistManager;
import com.example.mockpjmusictablet.view_model.SongViewModel;

import java.util.List;

public class SongsInPlaylistFragment extends Fragment implements IItemClick, IItemLongClick {
    private FragmentListSongPlaylistBinding binding;
    private SongViewModel viewModel;
    private MediaManager mediaManager;
    private PlaylistManager playlistManager;
    private MainActivity mainActivity;
    private PlaylistSongAdapter adapter;
    private List<Song> songList;
    private List<Song> previousSongs;
    private Playlist mPlaylist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListSongPlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        mainActivity = (MainActivity) getActivity();
        mediaManager = MediaManager.getInstance(requireActivity());
        playlistManager = new PlaylistManager(requireContext());
        viewModel.getCurrentPlaylist().observe(getViewLifecycleOwner(), this::initViews);
    }

    private void initViews(Playlist playlist) {
        mPlaylist = playlist;
        viewModel.getListSongs().observe(getViewLifecycleOwner(), songs -> {
            previousSongs = songs;
        });
        binding.tvNameOfPlaylist.setText(playlist.getName());
        binding.tvNumberOfPlaylist.setText(playlist.getNumberOfSongs() + "");

        songList = playlist.getSongs();

        adapter = new PlaylistSongAdapter(songList, requireActivity(), this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false);
        binding.rvListSong.setLayoutManager(layoutManager);
        binding.rvListSong.setAdapter(adapter);

        binding.btnClose.setOnClickListener(v -> {
            PlaylistFragment fragment = new PlaylistFragment();
            replaceLeftFragment(fragment, PLAYLIST_FRAGMENT);
        });

        binding.btnAdd.setOnClickListener(v -> {
            AddSongsFragment fragment = new AddSongsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PLAYLIST_NAME, playlist.getName());
            fragment.setArguments(bundle);
            replaceLeftFragment(fragment, PLAYLIST_ADD_SONGS_FRAGMENT);
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
        if (!previousSongs.containsAll(songList)) {
            viewModel.setListSongs(songList);
        }
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

    @Override
    public void onItemLongClick(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Do you want to delete this playlist?");

        builder.setPositiveButton("OK", (dialog, which) -> {
            playlistManager.removeSongFromPlaylist(mPlaylist.getName(), pos);
            viewModel.setPlaylists(playlistManager.loadPlaylists());
            viewModel.setCurrentPlaylist(playlistManager.findPlaylistByName(mPlaylist.getName()));
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.WHITE);

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.WHITE);
    }
}
