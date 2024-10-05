package com.example.mockpjmusictablet.data.model;

import android.graphics.Bitmap;

public class Song {
    private final String id;
    private final String title;
    private final String album;
    private final String durationConverted;
    private final String path;
    private final Bitmap icon;
    private long duration;
    private boolean isSelected = false;

    public Song(String id, String title, String album, String durationConverted, long duration, String path, Bitmap icon) {
        this.id = id;
        this.title = title;
        this.album = album;
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

    public String getDurationConverted() {
        return durationConverted;
    }
}
