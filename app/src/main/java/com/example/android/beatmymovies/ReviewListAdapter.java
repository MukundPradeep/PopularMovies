package com.example.android.beatmymovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin on 05-01-2016.
 */
public class ReviewListAdapter extends ArrayAdapter<Review>{

    public ReviewListAdapter(Context context, int resource, ArrayList<Review> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review reviewItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_item, parent, false);
        }

        TextView reviewText = (TextView) convertView.findViewById(R.id.review_text);
        reviewText.setText(reviewItem.content);

        return convertView;
    }
}
