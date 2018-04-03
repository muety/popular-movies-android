package com.github.n1try.popularmovies.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.MovieTrailer;

import java.util.List;

public class MovieTrailerItemAdapter extends ArrayAdapter<MovieTrailer> {
    public MovieTrailerItemAdapter(@NonNull Context context, @NonNull List<MovieTrailer> trailers) {
        super(context, 0, trailers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, parent, false);
        }

        final MovieTrailer trailer = getItem(position);

        final TextView trailerTitleIv = convertView.findViewById(R.id.details_trailer_title_tv);
        trailerTitleIv.setText(trailer.getName());

        return convertView;
    }
}
