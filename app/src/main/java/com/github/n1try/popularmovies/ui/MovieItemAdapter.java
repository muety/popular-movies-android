package com.github.n1try.popularmovies.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.utils.ImageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieItemAdapter extends ArrayAdapter<Movie> {
    private List<Movie> movies;
    private ArrayList<Integer> colorCache;

    public MovieItemAdapter(@NonNull Context context, @NonNull List<Movie> movies) {
        super(context, 0, movies);
        this.movies = movies;
        colorCache = new ArrayList<>(movies.size());
        colorCache.addAll(Collections.<Integer>nCopies(movies.size(), null));
    }

    @Override
    public void notifyDataSetChanged() {
        int delta = movies.size() - colorCache.size();
        if (delta > 0) {
            colorCache.addAll(Collections.<Integer>nCopies(delta, null));
            super.notifyDataSetChanged();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }

        final Movie movie = getItem(position);

        final View movieTextContainer = convertView.findViewById(R.id.main_movie_text_container);
        final View itemContainer = convertView.findViewById(R.id.item_container);
        final ImageView movieCoverIv = convertView.findViewById(R.id.details_movie_cover_iv);
        final TextView movieTitleTv = convertView.findViewById(R.id.main_movie_title_tv);
        final TextView movieRatingTv = convertView.findViewById(R.id.main_movie_rating_tv);
        final TextView movieGenresTv = convertView.findViewById(R.id.main_movie_genre_tv);

        itemContainer.setVisibility(View.GONE);
        movieTitleTv.setText(movie.getTitle());
        movieRatingTv.setText(String.valueOf(movie.getVoteAverage()));
        movieGenresTv.setText(TextUtils.join(", ", movie.getGenres()));
        Picasso.with(getContext()).load(movie.getPosterPath()).into(movieCoverIv, new Callback() {
            @Override
            public void onSuccess() {
                if (colorCache.get(position) == null) {
                    colorCache.set(position, ImageUtils.getAverageColor(((BitmapDrawable) movieCoverIv.getDrawable()).getBitmap()));
                }

                int color = colorCache.get(position);
                movieTextContainer.setBackgroundColor(color);
                if (ImageUtils.getBrightness(color) <= 110) {
                    movieTitleTv.setTextColor(getContext().getResources().getColor(R.color.colorTextLight));
                    movieRatingTv.setTextColor(getContext().getResources().getColor(R.color.colorTextLight));
                    movieGenresTv.setTextColor(getContext().getResources().getColor(R.color.colorTextLight));
                } else {
                    movieTitleTv.setTextColor(getContext().getResources().getColor(R.color.colorTextDark));
                    movieRatingTv.setTextColor(getContext().getResources().getColor(R.color.colorTextDark));
                    movieGenresTv.setTextColor(getContext().getResources().getColor(R.color.colorTextDark));
                }

                itemContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                Log.w(getContext().getPackageName(), "Couldn't fetch image for position " + position);
            }
        });

        return convertView;
    }
}
