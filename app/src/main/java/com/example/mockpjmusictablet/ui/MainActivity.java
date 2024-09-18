package com.example.mockpjmusictablet.ui;

import static com.example.mockpjmusictablet.Utils.Const.ACTION_NEXT;
import static com.example.mockpjmusictablet.Utils.Const.ACTION_PAUSE_SONG;
import static com.example.mockpjmusictablet.Utils.Const.ACTION_PREVIOUS;
import static com.example.mockpjmusictablet.Utils.Const.ACTION_SEND_DATA;
import static com.example.mockpjmusictablet.Utils.Const.MEDIA_STATE_LOOP_ALL;
import static com.example.mockpjmusictablet.Utils.Const.MEDIA_STATE_LOOP_ONE;
import static com.example.mockpjmusictablet.Utils.Const.MEDIA_STATE_NO_LOOP;

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
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mockpjmusictablet.R;
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
            mediaManager.getPlayer().setOnCompletionListener(mediaPlayer -> {
                if (mediaManager.getPlayer().getCurrentPosition() > 0) {
                    mediaPlayer.reset();
                    mediaManager.next();
                    if (mediaManager.getLoop() == MEDIA_STATE_LOOP_ONE) {
                        mediaManager.setLoop(MEDIA_STATE_NO_LOOP);
                    }
                }
            });
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
        mediaManager = MediaManager.getInstance(this);

        intentFilter.addAction(ACTION_SEND_DATA);
        receiver = new UpdatePlayNewSong(viewModel);
        registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        grantedPermission();
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
            Intent it = new Intent(ACTION_PAUSE_SONG);
            sendBroadcast(it);
        });

        binding.btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(ACTION_NEXT);
            sendBroadcast(intent);
        });

        binding.btnPrevious.setOnClickListener(view -> {
            Intent intent = new Intent(ACTION_PREVIOUS);
            sendBroadcast(intent);
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

        updateBtnPlayPause();
        updateSeekBarProgress();
        updateBtnShuffleAndLoop(this);
        setHomeFragment();
    }

    private void updateBtnShuffleAndLoop(MainActivity mainActivity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.btnShuffle.setColorFilter(ContextCompat.getColor(
                        mainActivity,
                        mediaManager.isShuffle() ? R.color.blue_light : R.color.white));
                switch (mediaManager.getLoop()) {
                    case MEDIA_STATE_NO_LOOP:
                        binding.btnRepeat.setImageResource(R.drawable.ic_repeat);
                        binding.btnRepeat.setColorFilter(ContextCompat.getColor(
                                mainActivity,
                                R.color.white));
                        break;
                    case MEDIA_STATE_LOOP_ONE:
                        binding.btnRepeat.setImageResource(R.drawable.ic_repeat_one);
                        binding.btnRepeat.setColorFilter(ContextCompat.getColor(
                                mainActivity,
                                R.color.blue_light));
                        break;
                    case MEDIA_STATE_LOOP_ALL:
                        binding.btnRepeat.setImageResource(R.drawable.ic_repeat);
                        binding.btnRepeat.setColorFilter(ContextCompat.getColor(
                                mainActivity,
                                R.color.blue_light));
                        break;
                }
                handler.postDelayed(this, 200);
            }
        });
    }

    private void updateBtnPlayPause() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isPlaying = mediaManager.getPlayer().isPlaying();
                binding.btnPlayAndPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
                handler.postDelayed(this, 200);
            }
        });
    }

    private void updateSeekBarProgress() {
        runOnUiThread(new Runnable() {
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
