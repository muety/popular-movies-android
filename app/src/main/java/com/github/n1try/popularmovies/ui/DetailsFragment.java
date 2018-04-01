package com.github.n1try.popularmovies.ui;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment {
    @BindView(R.id.details_movie_cover_placeholder_container)
    ViewGroup movieCoverPlaceholderContainer;
    @BindView(R.id.details_movie_cover_placeholder_iv)
    ImageView movieCoverPlaceholderIv;
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

    public static DetailsFragment newInstance(Movie movie) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.KEY_MOVIE_ID, movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        Movie movie = getArguments().getParcelable(MainActivity.KEY_MOVIE_ID);
        String dateString = new SimpleDateFormat(getResources().getString(R.string.display_date_format)).format(movie.getReleaseDate());
        movieGenresTv.setText(TextUtils.join(", ", movie.getGenres()));
        movieReleaseDateTv.setText(dateString);
        movieSummaryTv.setText(movie.getOverview());
        movieRatingTv.setText(String.valueOf(movie.getVoteAverage()));
        moviePopularityTv.setText(String.valueOf((int) Math.round(movie.getPopularity())));
        getActivity().setTitle(movie.getTitle());
        Picasso.with(getContext()).load(movie.getBackdropPath()).into(movieCoverIv, new Callback() {
            @Override
            public void onSuccess() {
                hidePlaceholderImage();
            }

            @Override
            public void onError() {
                showPlaceholderImage();
            }
        });

        return view;
    }

    private void showPlaceholderImage() {
        movieCoverIv.setVisibility(View.GONE);
        movieCoverPlaceholderContainer.setVisibility(View.VISIBLE);
        Drawable mIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_photo_camera_black_48dp);
        mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextMedium), PorterDuff.Mode.SRC_IN);
        movieCoverPlaceholderIv.setImageDrawable(mIcon);
    }

    private void hidePlaceholderImage() {
        movieCoverPlaceholderContainer.setVisibility(View.GONE);
        movieCoverIv.setVisibility(View.VISIBLE);
    }
}
