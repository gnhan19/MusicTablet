package com.example.mockpjmusictablet.ui.playlist;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.interfaces.IItemClick;
import com.example.mockpjmusictablet.data.interfaces.IItemLongClick;
import com.example.mockpjmusictablet.data.model.Playlist;
import com.example.mockpjmusictablet.databinding.FragmentPlaylistBinding;
import com.example.mockpjmusictablet.ui.playlist.songs.SongsInPlaylistFragment;
import com.example.mockpjmusictablet.utils.Const;
import com.example.mockpjmusictablet.utils.PlaylistManager;
import com.example.mockpjmusictablet.view_model.SongViewModel;

import java.util.List;

public class PlaylistFragment extends Fragment implements IItemClick, IItemLongClick {
    private FragmentPlaylistBinding binding;
    private SongViewModel viewModel;
    private PlaylistManager playlistManager;
    private List<Playlist> playlists;
    private PlaylistAdapter adapter;
    private final int selectedPlaylist = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        playlistManager = new PlaylistManager(requireContext());
        initViews();
    }

    private void initViews() {
        playlists = playlistManager.loadPlaylists();
        viewModel.setPlaylists(playlists);

        viewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            adapter = new PlaylistAdapter(playlists, requireContext(), this, this);
            if (selectedPlaylist >= 0) {
                adapter.selectAlbum(playlists.get(selectedPlaylist));
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false);
            binding.rvListSong.setLayoutManager(layoutManager);
            binding.rvListSong.setAdapter(adapter);

            binding.tvNumberOfAlbum.setText(playlists.size() + "");
        });

        binding.btnCreateNew.setOnClickListener(v -> {
            showCreatePlaylistDialog();
        });

    }

    private void showCreatePlaylistDialog() {
        final EditText input = new EditText(requireContext());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setTitle("Create new playlist")
                .setView(input)
                .setPositiveButton("OK", (dialog1, which) -> {
                    String playlistName = input.getText().toString();
                    if (playlistName.isEmpty()) {
                        return;
                    }
                    playlistManager.addPlaylist(new Playlist(playlistName));
                    viewModel.setPlaylists(playlistManager.loadPlaylists());
                })
                .setNegativeButton("Cancel", (dialog2, which) -> {
                    dialog2.cancel();
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.WHITE);  // Set to desired color

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.WHITE);  // Set to desired color
    }

    @Override
    public void onItemClick(int pos) {
        playlists = playlistManager.loadPlaylists();
        viewModel.setCurrentPlaylist(playlists.get(pos));
        SongsInPlaylistFragment fragment = new SongsInPlaylistFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view_left, fragment);
        transaction.addToBackStack(Const.PLAYLIST_SONGS_FRAGMENT);
        transaction.commit();
    }

    @Override
    public void onItemLongClick(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Do you want to delete this playlist?");

        builder.setPositiveButton("OK", (dialog, which) -> {
            playlistManager.removePlaylist(pos);
            viewModel.setPlaylists(playlistManager.loadPlaylists());
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.WHITE);  // Set to desired color

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.WHITE);  // Set to desired color
    }
}
