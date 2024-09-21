package com.example.mockpjmusictablet.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mockpjmusictablet.R;
import com.example.mockpjmusictablet.data.model.Album;
import com.example.mockpjmusictablet.data.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private static Context mContext;

    // Method to convert milliseconds to mm:ss format
    public static String msToMmSs(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static List<Song> getListSongOffline(Context context) {
        mContext = context;
        List<Song> songList = new ArrayList<>();
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor c = mContext.getContentResolver()
                    .query(uri, null,
                            null,
                            null,
                            null);
            if (c != null) {
                int idIndex = c.getColumnIndex(MediaStore.Audio.Media._ID);
                int titleIndex = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistIndex = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumIndex = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int dataIndex = c.getColumnIndex(MediaStore.Audio.Media.DATA);
                int durationIndex = c.getColumnIndex(MediaStore.Audio.Media.DURATION);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    int id = c.getInt(idIndex);
                    String title = c.getString(titleIndex);
                    String artist = c.getString(artistIndex);
                    String album = c.getString(albumIndex);
                    String path = c.getString(dataIndex);
                    long duration = c.getLong(durationIndex);
                    String durationConverted = Utils.msToMmSs(duration);
                    if (path.contains("mp3")) {
                        Song songModel = new Song(id + "", title, album,
                                artist, durationConverted, duration, path,
                                getIconSong(Uri.parse(path)));
                        songList.add(songModel);
                    }
                    c.moveToNext();
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songList;
    }

    public static Bitmap getIconSong(Uri uri) {
        Bitmap art = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_home);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        mmr.setDataSource(mContext, uri);
        rawArt = mmr.getEmbeddedPicture();

        if (null != rawArt) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        }
        return art;
    }

    public static void loadImage(ImageView view, Bitmap url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

    public static List<Album> getAlbums() {
        List<Album> albums = new ArrayList<>();
        List<Song> songs = getListSongOffline(mContext);
        Map<String, List<Song>> groupedSongs = new HashMap<>();
        for (Song song : songs) {
            groupedSongs.computeIfAbsent(song.getAlbum(), k -> new ArrayList<>()).add(song);
        }
        for (List<Song> songList : groupedSongs.values()) {
            if (!songList.isEmpty()) {
                albums.add(new Album(songList));
            }
        }
        return albums;
    }
}
