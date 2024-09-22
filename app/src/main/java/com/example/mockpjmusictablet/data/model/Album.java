package com.example.mockpjmusictablet.data.model;

import android.graphics.Bitmap;

import java.util.List;

public class Album {
    Bitmap icon;
    String name;
    int quantity;

    public List<Song> getSongs() {
        return songs;
    }

    List<Song> songs;

    public Bitmap getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public Album(List<Song> songs) {
        this.icon = songs.get(0).getIcon();
        this.name = songs.get(0).getAlbum();
        this.quantity = songs.size();
        this.songs = songs;
    }

}
