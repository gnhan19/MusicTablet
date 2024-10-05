package com.example.mockpjmusictablet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mockpjmusictablet.data.model.Playlist;
import com.example.mockpjmusictablet.data.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static final String SHARED_PREFS_NAME = "playlists_prefs";
    private static final String PLAYLISTS_KEY = "playlists_key";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public PlaylistManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void savePlaylists(List<Playlist> playlists) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(playlists);
        editor.putString(PLAYLISTS_KEY, json);
        editor.apply();
    }

    public List<Playlist> loadPlaylists() {
        String json = sharedPreferences.getString(PLAYLISTS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<List<Playlist>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
    }

    public void addPlaylist(Playlist playlist) {
        List<Playlist> playlists = loadPlaylists();
        playlists.add(playlist);
        savePlaylists(playlists);
    }

    public void removePlaylist(int position) {
        List<Playlist> playlists = loadPlaylists();
        playlists.remove(position);
        savePlaylists(playlists);
    }

    public void addSongToPlaylist(String playlistName, Song song) {
        List<Playlist> playlists = loadPlaylists();
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                playlist.addSong(song);
                break;
            }
        }
        savePlaylists(playlists);
    }

    public void removeSongFromPlaylist(String playlistName, int pos) {
        List<Playlist> playlists = loadPlaylists();
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                playlist.removeSong(pos);
                break;
            }
        }
        savePlaylists(playlists);
    }

    public Playlist findPlaylistByName(String name) {
        List<Playlist> playlists = loadPlaylists();
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }
}
