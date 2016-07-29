package com.example.android.beatmymovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 05-01-2016.
 */
public class Trailor implements Parcelable{
    public String name;
    public String id;
    public String key;

    public Trailor(String s1, String s2,String s3){
        this.name = s1;
        this.id = s2;
        this.key = s3;
    }

    protected Trailor(Parcel in) {
        name = in.readString();
        id = in.readString();
        key = in.readString();
    }

    public static final Parcelable.Creator<Trailor> CREATOR = new Parcelable.Creator<Trailor>() {
        @Override
        public Trailor createFromParcel(Parcel in) {
            return new Trailor(in);
        }

        @Override
        public Trailor[] newArray(int size) {
            return new Trailor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(key);
    }
}
