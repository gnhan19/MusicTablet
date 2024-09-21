package com.example.mockpjmusictablet.media;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.example.mockpjmusictablet.utils.Const;
import com.example.mockpjmusictablet.data.model.Song;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MediaManager {
    private final MediaPlayer mPlayer = new MediaPlayer();
    private List<Song> listSongs = new ArrayList<>();
    private int currentIndex = 0;
    private int mediaState = Const.MEDIA_IDLE;
    private int loop = Const.MEDIA_STATE_NO_LOOP;
    private final Random random = new Random();
    private boolean shuffle = Const.MEDIA_SHUFFLE_FALSE;
    private final Context mContext;

    private static WeakReference<MediaManager> mediaManagerRef = null;

    private MediaManager(Context context) {
        mContext = context;
    }

    public static MediaManager getInstance(Context context) {
        MediaManager mediaManager = mediaManagerRef != null ? mediaManagerRef.get() : null;
        if (mediaManager == null) {
            mediaManager = new MediaManager(context);
            mediaManagerRef = new WeakReference<>(mediaManager);
        }
        return mediaManager;
    }

    public void play(boolean isPlayAgain) {
        if (mediaState == Const.MEDIA_IDLE || mediaState == Const.MEDIA_STOP || isPlayAgain) {
            try {
                mPlayer.reset();
                Song song = listSongs.get(currentIndex);
                mPlayer.setDataSource(song.getPath());
                mPlayer.prepare();
                mPlayer.start();
                Intent intent = new Intent(Const.ACTION_SEND_DATA);
                intent.putExtra(Const.KEY_TITLE_SONG, song.getTitle());
                intent.putExtra(Const.KEY_PATH, song.getPath());
                mContext.sendBroadcast(intent);
                mediaState = Const.MEDIA_PLAYING;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mediaState == Const.MEDIA_PAUSE) {
            mPlayer.start();
            mediaState = Const.MEDIA_PLAYING;
        } else if (mediaState == Const.MEDIA_PLAYING) {
            mPlayer.pause();
            mediaState = Const.MEDIA_PAUSE;
        }
    }

    public void next() {
        if (loop == Const.MEDIA_STATE_NO_LOOP) {
            if (shuffle) {
                currentIndex = random.nextInt(listSongs.size());
            } else {
                if (currentIndex > listSongs.size() - 2) {
                    currentIndex = 0;
                } else {
                    currentIndex++;
                }
            }
        } else if (loop == Const.MEDIA_STATE_LOOP_ONE) {
            play(true);
            setLoop(Const.MEDIA_STATE_NO_LOOP);
        }
        play(true);
    }

    public void previous() {
        if (loop == Const.MEDIA_STATE_NO_LOOP) {
            if (shuffle) {
                currentIndex = random.nextInt(listSongs.size());
            } else {
                if (currentIndex <= 0) {
                    currentIndex = listSongs.size() - 1;
                } else {
                    currentIndex--;
                }
            }
        } else if (loop == Const.MEDIA_STATE_LOOP_ONE) {
            play(true);
            setLoop(Const.MEDIA_STATE_NO_LOOP);
        }
        play(true);
    }

    public void stop() {
        if (mediaState == Const.MEDIA_IDLE) {
            return;
        }
        mPlayer.stop();
        mediaState = Const.MEDIA_STOP;
    }

    public List<Song> getListSongs() {
        return listSongs;
    }

    public void setListSongs(List<Song> listSongs) {
        this.listSongs = listSongs;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int value) {
        loop = value;
    }

    public void seek(int pos){
        mPlayer.seekTo(pos);
    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }
}
