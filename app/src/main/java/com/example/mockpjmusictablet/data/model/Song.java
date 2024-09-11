package com.example.mockpjmusictablet.data.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song implements Parcelable {
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

    protected Song(Parcel in) {
        id = in.readString();
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        durationConverted = in.readString();
        path = in.readString();
        duration = in.readLong();
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(durationConverted);
        dest.writeString(path);
        dest.writeLong(duration);
        dest.writeParcelable(icon, flags);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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
