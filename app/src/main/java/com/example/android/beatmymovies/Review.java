package com.example.android.beatmymovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 05-01-2016.
 */
public class Review implements Parcelable {
    String author;
    String content;

    public Review(String s1, String s2){
        this.author = s1;
        this.content = s2;
    }
    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }
}
