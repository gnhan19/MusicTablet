package com.example.mockpjmusictablet.ui.home;

import static com.example.mockpjmusictablet.utils.Const.ACTION_PAUSE_SONG;
import static com.example.mockpjmusictablet.utils.Const.ALBUM_FRAGMENT;
import static com.example.mockpjmusictablet.utils.Const.MEDIA_STATE_LOOP_ALL;
import static com.example.mockpjmusictablet.utils.Const.MEDIA_STATE_LOOP_ONE;
import static com.example.mockpjmusictablet.utils.Const.MEDIA_STATE_NO_LOOP;
import static com.example.mockpjmusictablet.utils.Const.SONGS_FRAGMENT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.data.view_model.SongViewModel;
import com.example.mockpjmusictablet.databinding.FragmentHomeBinding;
import com.example.mockpjmusictablet.media.MediaManager;
import com.example.mockpjmusictablet.ui.album.AlbumFragment;
import com.example.mockpjmusictablet.ui.songs.SongsFragment;
import com.example.mockpjmusictablet.utils.Utils;

public class HomeFragment extends Fragment {
    private static final String TAG = "nhangb";
    private FragmentHomeBinding binding;
    private SongViewModel viewModel;
    private MediaManager mediaManager;
    private final Handler handler = new Handler(Looper.getMainLooper());

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
        mediaManager = MediaManager.getInstance(requireActivity());
        initViews();
    }

    private void initViews() {
        viewModel.getListSongs().observe(getViewLifecycleOwner(), songs -> {
            mediaManager.setListSongs(songs);
        });
        mediaManager.setCurrentIndex(0);
        viewModel.getSelectedSong().observe(getViewLifecycleOwner(), song -> {
            binding.setSong(song);
        });

        viewModel.getSongName().observe(getViewLifecycleOwner(), songName -> {
            binding.tvSongName.setText(songName);
        });

        binding.btnPlayAndPause.setOnClickListener(view -> {
            Intent it = new Intent(ACTION_PAUSE_SONG);
            requireActivity().sendBroadcast(it);
        });

        binding.btnNext.setOnClickListener(view -> {
            mediaManager.next();
            Log.d(TAG, "current index = " + mediaManager.getCurrentIndex());
            viewModel.selectSong(mediaManager.getListSongs().get(mediaManager.getCurrentIndex()));
        });

        binding.btnPrevious.setOnClickListener(view -> {
            mediaManager.previous();
        });

        binding.btnShuffle.setOnClickListener(view -> {
            boolean shuffle = mediaManager.isShuffle();
            mediaManager.setShuffle(!shuffle);
        });

        binding.btnRepeat.setOnClickListener(view -> {
            int loop = mediaManager.getLoop();
            switch (loop) {
                case MEDIA_STATE_NO_LOOP:
                    mediaManager.setLoop(MEDIA_STATE_LOOP_ONE);
                    break;
                case MEDIA_STATE_LOOP_ONE:
                    mediaManager.setLoop(MEDIA_STATE_LOOP_ALL);
                    break;
                case MEDIA_STATE_LOOP_ALL:
                    mediaManager.setLoop(MEDIA_STATE_NO_LOOP);
            }
        });
        checkLeftFragment();
        updateBtnPlayPause();
        updateSeekBarProgress();
        updateBtnShuffleAndLoop();
        clickToPage();
    }

    private void updateBtnShuffleAndLoop() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.btnShuffle.setColorFilter(ContextCompat.getColor(
                        requireActivity(),
                        mediaManager.isShuffle() ? R.color.blue_light : R.color.white));
                switch (mediaManager.getLoop()) {
                    case MEDIA_STATE_NO_LOOP:
                        binding.btnRepeat.setImageResource(R.drawable.ic_repeat);
                        binding.btnRepeat.setColorFilter(ContextCompat.getColor(
                                requireActivity(),
                                R.color.white));
                        break;
                    case MEDIA_STATE_LOOP_ONE:
                        binding.btnRepeat.setImageResource(R.drawable.ic_repeat_one);
                        binding.btnRepeat.setColorFilter(ContextCompat.getColor(
                                requireActivity(),
                                R.color.blue_light));
                        break;
                    case MEDIA_STATE_LOOP_ALL:
                        binding.btnRepeat.setImageResource(R.drawable.ic_repeat);
                        binding.btnRepeat.setColorFilter(ContextCompat.getColor(
                                requireActivity(),
                                R.color.blue_light));
                        break;
                }
                handler.postDelayed(this, 200);
            }
        });
    }

    private void updateBtnPlayPause() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isPlaying = mediaManager.getPlayer().isPlaying();
                binding.btnPlayAndPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
                handler.postDelayed(this, 200);
            }
        });
    }

    private void updateSeekBarProgress() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaManager.getPlayer().isPlaying()) {
                    Song song = mediaManager.getListSongs().get(mediaManager.getCurrentIndex());
                    binding.tvStartTime.setText(Utils.msToMmSs(mediaManager.getPlayer().getCurrentPosition()));
                    binding.sbDuration.setMax((int) song.getDuration());
                    binding.sbDuration.setProgress(mediaManager.getPlayer().getCurrentPosition());
                    binding.sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            mediaManager.seek(binding.sbDuration.getProgress());
                        }
                    });
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void clickToPage() {
        binding.lnPlaylist.setOnClickListener(view -> setFragment(1));
        binding.lnAlbum.setOnClickListener(view -> setFragment(2));
        binding.lnHome.setOnClickListener(view -> setFragment(3));
        binding.lnFolder.setOnClickListener(view -> setFragment(4));
    }

    private void setFragment(int page) {
        switch (page) {
            case 1:
                Toast.makeText(requireActivity(), "This feature is implementing", Toast.LENGTH_SHORT).show();
                binding.lnPlaylist.setBackgroundResource(R.color.blue_light);
                binding.lnAlbum.setBackgroundResource(R.color.white);
                binding.lnHome.setBackgroundResource(R.color.white);
                binding.lnFolder.setBackgroundResource(R.color.white);
                break;
            case 2:
                replaceLeftFragment(new AlbumFragment(), ALBUM_FRAGMENT);
                binding.lnPlaylist.setBackgroundResource(R.color.white);
                binding.lnAlbum.setBackgroundResource(R.color.blue_light);
                binding.lnHome.setBackgroundResource(R.color.white);
                binding.lnFolder.setBackgroundResource(R.color.white);
                break;
            case 3:
                replaceLeftFragment(new SongsFragment(), SONGS_FRAGMENT);
                binding.lnPlaylist.setBackgroundResource(R.color.white);
                binding.lnAlbum.setBackgroundResource(R.color.white);
                binding.lnHome.setBackgroundResource(R.color.blue_light);
                binding.lnFolder.setBackgroundResource(R.color.white);
                break;
            case 4:
                Toast.makeText(requireActivity(), "This feature is unavailable", Toast.LENGTH_SHORT).show();
                binding.lnPlaylist.setBackgroundResource(R.color.white);
                binding.lnAlbum.setBackgroundResource(R.color.white);
                binding.lnHome.setBackgroundResource(R.color.white);
                binding.lnFolder.setBackgroundResource(R.color.blue_light);
                break;
        }
    }

    private void checkLeftFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container_view_left);
        binding.lnHome.setBackgroundResource(currentFragment instanceof SongsFragment ? R.color.blue_light : R.color.white);
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

}
