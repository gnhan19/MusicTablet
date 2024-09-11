package com.example.mockpjmusictablet.ui.home;

import android.graphics.Bitmap;
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

import com.example.mockpjmusictablet.Utils.Utils;
import com.example.mockpjmusictablet.data.view_model.SongViewModel;
import com.example.mockpjmusictablet.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private SongViewModel viewModel;
    private HomeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        initViews();
    }

    private void initViews() {
        viewModel.getSelectedSong().observe(requireActivity(), song -> {
            binding.setSong(song);
            Utils.loadImage(binding.ivMusic, song.getIcon());

        });
        viewModel.getListSongs().observe(requireActivity(), songs -> {
            adapter = new HomeAdapter(requireActivity(), songs);
            binding.rvListSong.setLayoutManager(new LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false));
            binding.rvListSong.setAdapter(adapter);
        });
        viewModel.getPath().observe(requireActivity(), path -> {
            Utils.loadImage(binding.ivMusic, Utils.getIconSong(Uri.parse(path)));
        });
    }
}
