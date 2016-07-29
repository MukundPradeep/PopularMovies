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
public class TrailorListAdapter extends ArrayAdapter<Trailor> {
    public TrailorListAdapter(Context context, int resource, ArrayList<Trailor> objects) {
        super(context, resource,objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailor trailorItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailor_item, parent, false);
        }

        TextView trailorName = (TextView) convertView.findViewById(R.id.list_trailor_text);
        trailorName.setText(trailorItem.name);

        return convertView;
    }
}
