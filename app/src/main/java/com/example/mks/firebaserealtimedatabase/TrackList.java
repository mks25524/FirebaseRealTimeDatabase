package com.example.mks.firebaserealtimedatabase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mks on 10/22/2017.
 */

public class TrackList extends ArrayAdapter<Track> {
    private Activity context;
    List<Track> tracks;

    public TrackList(Activity context, List<Track> tracks) {
        super(context, R.layout.tracklist_layout, tracks);
        this.context = context;
        this.tracks = tracks;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.tracklist_layout, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.trackName);
        TextView textViewGenre = (TextView) listViewItem.findViewById(R.id.trackRating);

        Track track = tracks.get(position);
        textViewName.setText(track.getTrackName());
        textViewGenre.setText(String.valueOf(track.getTrackRating()));

        return listViewItem;
    }
}
