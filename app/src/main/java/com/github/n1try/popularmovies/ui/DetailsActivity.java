package com.github.n1try.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.details_movie_cover_iv)
    ImageView movieCoverIv;
    @BindView(R.id.details_genres_tv)
    TextView movieGenresTv;
    @BindView(R.id.details_date_tv)
    TextView movieReleaseDateTv;
    @BindView(R.id.details_summary_tv)
    TextView movieSummaryTv;
    @BindView(R.id.details_rating_tv)
    TextView movieRatingTv;
    @BindView(R.id.details_popularity_tv)
    TextView moviePopularityTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Movie movie = getIntent().getParcelableExtra(MainActivity.KEY_MOVIE_ID);
        String dateString = new SimpleDateFormat(getResources().getString(R.string.display_date_format)).format(movie.getReleaseDate());

        setTitle(movie.getTitle());

        movieGenresTv.setText(TextUtils.join(", ", movie.getGenres()));
        movieReleaseDateTv.setText(dateString);
        movieSummaryTv.setText(movie.getOverview());
        movieRatingTv.setText(String.valueOf(movie.getVoteAverage()));
        moviePopularityTv.setText(String.valueOf((int) Math.round(movie.getPopularity())));
        Picasso.with(this).load(movie.getPosterPath()).into(movieCoverIv);

    }

}
