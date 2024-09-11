package com.example.mockpjmusictablet.data.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mockpjmusictablet.data.model.Song;

import java.util.List;

public class SongViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> songs = new MutableLiveData<>();
    private final MutableLiveData<Song> song = new MutableLiveData<>();
    private final MutableLiveData<String> songName = new MutableLiveData<>();
    private final MutableLiveData<String> path = new MutableLiveData<>();

    public void selectSong(Song song) {
        this.song.setValue(song);
    }

    public LiveData<Song> getSelectedSong() {
        return song;
    }

    public void setListSongs(List<Song> songs) {
        this.songs.setValue(songs);
    }

    public LiveData<List<Song>> getListSongs() {
        return songs;
    }

    public void setSongName(String songName) {
        this.songName.setValue(songName);
    }

    public LiveData<String> getSongName() {
        return songName;
    }

    public void setPath(String path) {
        this.path.setValue(path);
    }

    public LiveData<String> getPath() {
        return path;
    }
}
