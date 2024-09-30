package com.example.mockpjmusictablet.data.model;


import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name;
    private int numberOfSongs;
    private List<Song> songs;

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
        this.numberOfSongs = 0;
    }

    public void addSong(Song song) {
        this.songs.add(song);
        this.numberOfSongs++;
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
        this.numberOfSongs--;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
