package com.example.android.beatmymovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Admin on 04-01-2016.
 */
public class MovieGridAdapter extends ArrayAdapter<MovieForGrid> {
    public MovieGridAdapter(Context context, int resource, ArrayList<MovieForGrid> movieList) {
        super(context, resource, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieForGrid movieItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_grid_item, parent, false);
        }

        ImageView poster = (ImageView)convertView.findViewById(R.id.movie_grid_item_image);
        //poster.setImageResource(R.mipmap.interstellar);
        //Log.v(LOG_TAG, "Poster path: " +  movieItem.poster_path);
        String realPath = "http://image.tmdb.org/t/p/w185" + movieItem.poster_path;
        Picasso.with(getContext()).load(realPath).into(poster);
        return convertView;
    }
}
