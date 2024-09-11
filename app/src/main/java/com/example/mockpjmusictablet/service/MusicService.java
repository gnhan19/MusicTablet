package com.example.mockpjmusictablet.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaSession2;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;


import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.Utils.Const;
import com.example.mockpjmusictablet.data.model.Song;
import com.example.mockpjmusictablet.ui.MainActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MusicService extends Service {
    public static int IDLE = 0;
    public static int PLAY = 1;
    public static int PAUSE = 2;
    private IBinder iBinder = new MyBinder();
    public MediaPlayer player = new MediaPlayer();
    private List<Song> songList;
    private Song currentSong;
    private int index, state;
    private Bitmap mIcon;
    private String mTitle, mSinger;
    private BroadCastMusic receiver;

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void playMusic() {
        try {
            if (state == IDLE) {
                player.reset();
                currentSong = songList.get(index);
                player.setDataSource(currentSong.getPath());
                player.prepare();
                player.start();
                state = PLAY;
                mTitle = currentSong.getTitle();
                mSinger = currentSong.getArtist();
                mIcon = currentSong.getIcon();
            } else if (state == PLAY) {
                player.pause();
                state = PAUSE;
            } else if (state == PAUSE) {
                player.start();
                state = PLAY;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSong() {
        if (index >= (songList.size() - 1)) {
            index = -1;
        }
        index++;
        state = IDLE;
        playMusic();
    }

    public void backSong() {
        if (index == 0) {
            index = songList.size();
        }
        index--;
        state = IDLE;
        playMusic();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new BroadCastMusic();
        IntentFilter filter = new IntentFilter();
        filter.addAction("MediaPlayer");
        registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        player.start();
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        return START_STICKY;
    }

    public void showNotification() {
        int playPauseBtn = 1;
        if (state == PLAY) {
            playPauseBtn = R.drawable.pause;
        } else if (state == PAUSE) {
            playPauseBtn = R.drawable.play;
        } else if (state == IDLE) {
            playPauseBtn = R.drawable.play;
        }

        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.putExtra("TITLE", mTitle);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, BroadCastMusic.class).setAction(Const.ACTION_PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,
                0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playIntent = new Intent(this, BroadCastMusic.class).setAction(Const.ACTION_PAUSE_SONG);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this,
                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, BroadCastMusic.class).setAction(Const.ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,
                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(mTitle)
                .setContentText(mSinger)
                .setChannelId("Music")
                .setLargeIcon(mIcon)
                .setSmallIcon(R.drawable.ic_music)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_previous, "previous", prevPendingIntent)
                .addAction(playPauseBtn, "play", playPendingIntent)
                .addAction(R.drawable.ic_next, "next", nextPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .build();

        startForeground(Const.NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    public void updateThumbMusic(Bitmap thumb, String title, String singer) {
        mIcon = thumb;
        mTitle = title;
        mSinger = singer;
    }

    public int getCurrentDuration() {
        int time = 100;
        if (isSongPlaying()) {
            time = player.getCurrentPosition();
        }
        return time;
    }

    public int getTotalDuration() {
        int time = 0;
        if (isSongPlaying()) {
            time = player.getDuration();
        }
        return time;
    }

    public void seekTo(int progress) {
        try {
            player.seekTo(progress);
        } catch (Exception e) {
        }
    }

    public String getStartTime() {
        String time = "--:--";
        try {
            int duration = player.getCurrentPosition();
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            time = df.format(new Date(duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isSongPlaying() {
        return player != null && player.isPlaying() && currentSong != null;
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    private class BroadCastMusic extends BroadcastReceiver {
        @SuppressLint("ForegroundServiceType")
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Const.ACTION_PAUSE_SONG:
                    playMusic();
                    break;
                case Const.ACTION_NEXT:
                    nextSong();
                    break;
                case Const.ACTION_PREVIOUS:
                    backSong();
                    break;

            }
        }
    }
}


