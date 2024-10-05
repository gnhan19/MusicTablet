package com.example.mockpjmusictablet.ui.songs;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mockpjmusictablet.databinding.FragmentSongsBinding;
import com.example.mockpjmusictablet.utils.Utils;
import com.example.mockpjmusictablet.view_model.SongViewModel;

public class SongsFragment extends Fragment {
    private FragmentSongsBinding binding;
    private SongViewModel viewModel;
    private SongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        initViews();
    }

    private void initViews() {
        viewModel.getListSongs().observe(requireActivity(), songs -> {
            adapter = new SongAdapter(requireActivity(), songs);
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false);
            binding.rvListSong.setLayoutManager(layoutManager);
            binding.rvListSong.setAdapter(adapter);
            viewModel.getSelectedSong().observe(requireActivity(), song -> {
                binding.setSong(song);
                Utils.loadImage(binding.ivMusic, Utils.getIconSong(Uri.parse(song.getPath())));
                adapter.selectSong(song);
                layoutManager.scrollToPosition(songs.indexOf(song));
                adapter.notifyDataSetChanged();
            });
        });
        viewModel.getPath().observe(requireActivity(), path -> {
            Utils.loadImage(binding.ivMusic, Utils.getIconSong(Uri.parse(path)));
        });
    }
}
