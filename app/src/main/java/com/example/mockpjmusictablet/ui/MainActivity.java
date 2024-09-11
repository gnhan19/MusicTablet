package com.example.mockpjmusictablet.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.Utils.Const;
import com.example.mockpjmusictablet.Utils.Utils;
import com.example.mockpjmusictablet.broadcast.UpdatePlayNewSong;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.data.view_model.SongViewModel;
import com.example.mockpjmusictablet.databinding.ActivityMainBinding;
import com.example.mockpjmusictablet.media.MediaManager;
import com.example.mockpjmusictablet.service.MusicService;
import com.example.mockpjmusictablet.ui.home.HomeFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SongViewModel viewModel;

    private final IntentFilter intentFilter = new IntentFilter();
    private UpdatePlayNewSong receiver;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;

    private List<Song> songList;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SongViewModel.class);
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
        }
        grantedPermission();
    }

    private void grantedPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            List<Song> songs = Utils.getListSongOffline(this);
            viewModel.setListSongs(songs);
            viewModel.selectSong(songs.get(0));
            setHomeFragment();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 105);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(playIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        iniViews();
    }

    private void iniViews() {
        viewModel.getListSongs().observe(this, songs -> {
            songList = songs;
        });
        viewModel.getSelectedSong().observe(this, song -> {
            musicService.setIndex(songList.indexOf(song));
            binding.setSong(song);
        });

        viewModel.getSongName().observe(this, songName -> {
            binding.tvSongName.setText(songName);
        });

        binding.btnPlayAndPause.setOnClickListener(view -> {
            Intent intent = new Intent(Const.ACTION_PAUSE_SONG);
            sendBroadcast(intent);
            musicService.playMusic();
        });

        binding.btnNext.setOnClickListener(view -> {
            musicService.nextSong();
        });

        binding.btnPrevious.setOnClickListener(view -> {
            musicService.backSong();
        });

    }

    private void setHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, HomeFragment.class, null)
                .commit();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }
}
