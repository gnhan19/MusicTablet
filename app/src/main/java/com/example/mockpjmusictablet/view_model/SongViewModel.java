package com.example.mockpjmusictablet.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mockpjmusictablet.data.model.Playlist;
import com.example.mockpjmusictablet.data.model.Song;

import java.util.List;

public class SongViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> songs = new MutableLiveData<>();
    private final MutableLiveData<Song> song = new MutableLiveData<>();
    private final MutableLiveData<String> songName = new MutableLiveData<>();
    private final MutableLiveData<String> path = new MutableLiveData<>();
    private final MutableLiveData<Integer> positionAlbum = new MutableLiveData<>();
    private final MutableLiveData<List<Playlist>> playlists = new MutableLiveData<>();
    private final MutableLiveData<Playlist> playlist = new MutableLiveData<>();

    public LiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists.setValue(playlists);
    }

    public LiveData<Playlist> getCurrentPlaylist() {
        return playlist;
    }

    public void setCurrentPlaylist(Playlist playlist) {
        this.playlist.setValue(playlist);
    }

    public void selectSong(Song song) {
        this.song.setValue(song);
    }

    public LiveData<Song> getSelectedSong() {
        return song;
    }

    public LiveData<List<Song>> getListSongs() {
        return songs;
    }

    public void setListSongs(List<Song> songs) {
        this.songs.setValue(songs);
    }

    public LiveData<String> getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName.setValue(songName);
    }

    public LiveData<String> getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path.setValue(path);
    }

    public LiveData<Integer> getPositionAlbum() {
        return positionAlbum;
    }

    public void setPositionAlbum(int pos) {
        this.positionAlbum.setValue(pos);
    }
}
