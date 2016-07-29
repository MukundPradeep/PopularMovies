package com.example.android.beatmymovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 04-01-2016.
 */
public class MovieForGrid implements Parcelable{

    String poster_path;
    String original_title;
    String overview;
    String vote_count;
    String release_date;
    int id;

    public MovieForGrid(String s, String s1, String s2, String s3, String s4, int s5) {
        this.poster_path = s;
        this.original_title = s1;
        this.overview = s2;
        this.vote_count = s3;
        this.release_date = s4;
        this.id = s5;
    }

    protected MovieForGrid(Parcel in) {
        poster_path = in.readString();
        original_title = in.readString();
        overview = in.readString();
        vote_count = in.readString();
        release_date = in.readString();
        id = in.readInt();
    }

    public static final Creator<MovieForGrid> CREATOR = new Creator<MovieForGrid>() {
        @Override
        public MovieForGrid createFromParcel(Parcel in) {
            return new MovieForGrid(in);
        }

        @Override
        public MovieForGrid[] newArray(int size) {
            return new MovieForGrid[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(vote_count);
        dest.writeString(release_date);
        dest.writeInt(id);
    }
}
