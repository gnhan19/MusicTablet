package com.example.mockpjmusictablet.media;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import com.example.mockpjmusictablet.ui.MainActivity;
import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.Utils.Const;

import java.util.Objects;

public class MediaService extends Service {

    private MediaManager mediaManager;
    private final BroadCastMusic broadCastMusic = new BroadCastMusic();
    private final IntentFilter intentFilter = new IntentFilter();
    private RemoteViews remoteViews;
    private Notification.Builder mBuilder;

    private Context context;

    private final MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        runForeground();
        return myBinder;
    }

    public static class MyBinder extends Binder {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mediaManager = MediaManager.getInstance(context);
        intentFilter.addAction(Const.ACTION_PREVIOUS);
        intentFilter.addAction(Const.ACTION_NEXT);
        intentFilter.addAction(Const.ACTION_PAUSE_SONG);
        intentFilter.addAction(Const.ACTION_START_FOREGROUND);
        intentFilter.addAction(Const.ACTION_SEND_DATA);
        intentFilter.addAction(Const.ACTION_STOP);
        registerReceiver(broadCastMusic, intentFilter, Context.RECEIVER_EXPORTED);
    }

    private class BroadCastMusic extends BroadcastReceiver {
        @SuppressLint("ForegroundServiceType")
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Const.ACTION_SEND_DATA:
                    String title = intent.getStringExtra(Const.KEY_TITLE_SONG);
                    remoteViews.setTextViewText(R.id.tvTitleNoti, title);
                    startForeground(Const.NOTIFICATION_ID, mBuilder.build());
                    break;

                case Const.ACTION_PREVIOUS:
                    mediaManager.previous();
                    break;

                case Const.ACTION_NEXT:
                    mediaManager.next();
                    break;

                case Const.ACTION_PAUSE_SONG:
                    if (mediaManager.getPlayer().isPlaying()) {
                        remoteViews.setImageViewResource(R.id.ivPause, R.drawable.play);
                    } else {
                        remoteViews.setImageViewResource(R.id.ivPause, R.drawable.pause);
                    }
                    mediaManager.play(false);
                    startForeground(Const.NOTIFICATION_ID, mBuilder.build());
                    break;

                case Const.ACTION_STOP:
                    mediaManager.stop();
                    stopSelf();
                    Intent intentStop = new Intent(Const.ACTION_STOP_ALL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    sendBroadcast(intentStop);
                    break;
            }
        }
    }

    private void runForeground() {
        remoteViews = new RemoteViews(
                getPackageName(),
                R.layout.layout_notification_music_service
        );
        mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setCustomBigContentView(remoteViews)
                .setSound(null)
                .setOnlyAlertOnce(true);

        Intent intentPress = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        Const.REQUEST_CODE_NOTIFICATION,
                        intentPress,
                        PendingIntent.FLAG_IMMUTABLE
                );
        mBuilder.setContentIntent(pendingIntent);
        PendingIntent pre = PendingIntent.getBroadcast(
                context, Const.REQUEST_CODE_NOTIFICATION,
                new Intent(Const.ACTION_PREVIOUS), PendingIntent.FLAG_IMMUTABLE
        );
        remoteViews.setOnClickPendingIntent(R.id.ibPre, pre);
        PendingIntent next = PendingIntent.getBroadcast(
                context, Const.REQUEST_CODE_NOTIFICATION,
                new Intent(Const.ACTION_NEXT), PendingIntent.FLAG_IMMUTABLE
        );
        remoteViews.setOnClickPendingIntent(R.id.ivNext, next);
        Intent intentP = new Intent(Const.ACTION_PAUSE_SONG);
        intentP.putExtra(Const.KEY_SEND_PAUSE, mediaManager.getPlayer().isPlaying());
        PendingIntent pause = PendingIntent.getBroadcast(
                context,
                Const.REQUEST_CODE_NOTIFICATION,
                intentP,
                PendingIntent.FLAG_IMMUTABLE
        );
        remoteViews.setOnClickPendingIntent(R.id.ivPause, pause);
    }

}


