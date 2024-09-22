package com.example.mockpjmusictablet.data.model;

import android.graphics.Bitmap;

public class Song {
    private final String id, title, album, artist, durationConverted, path;
    private long duration;
    private final Bitmap icon;
    private boolean isSelected;

    public Song(String id, String title, String album, String artist, String durationConverted, long duration, String path, Bitmap icon) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.durationConverted = durationConverted;
        this.duration = duration;
        this.path = path;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDurationConverted() {
        return durationConverted;
    }
}
