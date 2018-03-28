package com.github.n1try.popularmovies.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieItemAdapter extends ArrayAdapter<Movie> {

    @BindView(R.id.main_movie_cover_iv)
    ImageView movieCoverIv;
    @BindView(R.id.main_movie_title_tv)
    TextView movieTitleIv;
    @BindView(R.id.main_movie_rating_tv)
    TextView movieRatingTv;

    public MovieItemAdapter(@NonNull Context context, @NonNull List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }

        ButterKnife.bind(this, convertView);
        Picasso.with(getContext()).load(movie.getPosterPath()).into(movieCoverIv);
        movieTitleIv.setText(movie.getTitle());
        movieRatingTv.setText(String.valueOf(movie.getVoteAverage()));

        return convertView;
    }
}
