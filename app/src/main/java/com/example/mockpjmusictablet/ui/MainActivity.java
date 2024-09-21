package com.example.mockpjmusictablet.ui;

import static com.example.mockpjmusictablet.utils.Const.ACTION_SEND_DATA;
import static com.example.mockpjmusictablet.utils.Const.MEDIA_STATE_LOOP_ONE;
import static com.example.mockpjmusictablet.utils.Const.MEDIA_STATE_NO_LOOP;

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
import com.example.mockpjmusictablet.broadcast.UpdatePlayNewSong;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.data.view_model.SongViewModel;
import com.example.mockpjmusictablet.databinding.ActivityMainBinding;
import com.example.mockpjmusictablet.media.MediaManager;
import com.example.mockpjmusictablet.service.MusicService;
import com.example.mockpjmusictablet.ui.home.HomeFragment;
import com.example.mockpjmusictablet.ui.songs.SongsFragment;
import com.example.mockpjmusictablet.utils.Const;
import com.example.mockpjmusictablet.utils.Utils;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SongViewModel viewModel;

    private MediaManager mediaManager;
    private final IntentFilter intentFilter = new IntentFilter();
    private UpdatePlayNewSong receiver;
    private boolean isBound = false;

    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isBound = true;
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
            isBound = false;
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
            setDestinationFragment();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 105);
        }
    }

    private void setDestinationFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view_left, SongsFragment.class, null)
                .addToBackStack(Const.SONGS_FRAGMENT)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view_right, HomeFragment.class, null)
                .addToBackStack(Const.HOME_FRAGMENT)
                .commit();
    }

    public boolean isBound() {
        return isBound;
    }

    @Override
    protected void onDestroy() {
        try {
            if (isBound) {
                unbindService(serviceConnection);
                unregisterReceiver(receiver);
                isBound = false;
            }
        } catch (Exception e) {
            Log.d("Error", "Error unbind service connect");
        }
        super.onDestroy();
    }
}
