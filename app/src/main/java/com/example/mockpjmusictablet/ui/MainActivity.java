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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
    private static final String TAG = "nhangb";
    private ActivityMainBinding binding;
    private SongViewModel viewModel;

    private MediaManager mediaManager;
    private final IntentFilter intentFilter = new IntentFilter();
    private UpdatePlayNewSong receiver;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected:");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SongViewModel.class);
        grantedPermission();
        mediaManager = MediaManager.getInstance(this);

        intentFilter.addAction(Const.ACTION_SEND_DATA);
        receiver = new UpdatePlayNewSong(viewModel);
        registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        runOnUiThread(runnable);
    }

    private void grantedPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            List<Song> songs = Utils.getListSongOffline(this);
            viewModel.setListSongs(songs);
            viewModel.selectSong(songs.get(0));
            iniViews();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 105);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void iniViews() {
        viewModel.getListSongs().observe(this, songs -> {
            mediaManager.setListSongs(songs);
        });
        viewModel.getSelectedSong().observe(this, song -> {
            mediaManager.setCurrentIndex(mediaManager.getListSongs().indexOf(song));
            binding.setSong(song);
        });

        viewModel.getSongName().observe(this, songName -> {
            binding.tvSongName.setText(songName);
        });

        binding.btnPlayAndPause.setOnClickListener(view -> {
            Intent it = new Intent(Const.ACTION_PAUSE_SONG);
            sendBroadcast(it);
        });

        binding.btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(Const.ACTION_NEXT);
            sendBroadcast(intent);
        });

        binding.btnPrevious.setOnClickListener(view -> {
            Intent intent = new Intent(Const.ACTION_PREVIOUS);
            sendBroadcast(intent);
        });

        setHomeFragment();

    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean isPlaying = mediaManager.getPlayer().isPlaying();
            binding.btnPlayAndPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
            handler.postDelayed(this, 200);
        }
    };

    private void setHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, HomeFragment.class, null)
                .commit();
    }

    @Override
    protected void onDestroy() {
        try {
            unbindService(serviceConnection);
            unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.d("Error", "Error unbind service connect");
        }
        super.onDestroy();
    }
}
