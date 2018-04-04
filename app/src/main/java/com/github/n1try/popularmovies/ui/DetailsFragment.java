/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.ui;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.model.MovieReview;
import com.github.n1try.popularmovies.model.MovieTrailer;
import com.github.n1try.popularmovies.persistence.FavoriteMoviesContract;
import com.github.n1try.popularmovies.utils.AndroidUtils;
import com.github.n1try.popularmovies.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.n1try.popularmovies.ui.MainActivity.KEY_MOVIE_ID;

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
    @BindView(R.id.details_title_tv)
    TextView movieTitleTv;
    @BindView(R.id.details_trailers_label_tv)
    TextView trailersLabelTv;
    @BindView(R.id.details_trailers_lv)
    ListView trailersLv;
    @BindView(R.id.details_reviews_label_tv)
    TextView reviewsLabelTv;
    @BindView(R.id.details_reviews_lv)
    ListView reviewsLv;
    @BindView(R.id.details_star_iv)
    ImageView starIv;

    private static final int TRAILER_LIST_LOADER_ID = 1;
    private static final int REVIEWS_LIST_LOADER_ID = 2;

    private Movie movie;
    private MovieTrailerItemAdapter trailerAdapter;
    private MovieReviewItemAdapter reviewAdapter;

    public static DetailsFragment newInstance(Movie movie) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.KEY_MOVIE_ID, movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movie = getArguments().getParcelable(MainActivity.KEY_MOVIE_ID);
        getLoaderManager().initLoader(TRAILER_LIST_LOADER_ID, createLoaderBundle(), trailerLoaderCallbacks);
        getLoaderManager().initLoader(REVIEWS_LIST_LOADER_ID, createLoaderBundle(), reviewsLoaderCallbacks);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        String dateString = Utils.formatDate(movie.getReleaseDate(), getResources().getString(R.string.display_date_format));
        movieGenresTv.setText(TextUtils.join(", ", movie.getGenres()));
        movieReleaseDateTv.setText(dateString);
        movieSummaryTv.setText(movie.getOverview());
        movieSummaryTv.append(movie.getOverview());
        movieRatingTv.setText(String.valueOf(movie.getVoteAverage()));
        moviePopularityTv.setText(String.valueOf((int) Math.round(movie.getPopularity())));
        movieTitleTv.setText(movie.getTitle());
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

        trailersLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MovieTrailer trailer = (MovieTrailer) adapterView.getAdapter().getItem(position);
                AndroidUtils.watchYoutubeVideo(getContext(), trailer.getKey());
            }
        });

        if (isMovieStarred()) setIconStarred();
        else setIconUnstarred();

        starIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMovieStarred()) {
                    Uri resultUri = getActivity().getContentResolver().insert(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI, FavoriteMoviesContract.FavoriteMovieEntry.movieToContentValues(movie));
                    if (resultUri != null) {
                        setIconStarred();
                        Toast.makeText(getContext(), getString(R.string.alert_starred), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.alert_starred_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    int numDeleted = getActivity().getContentResolver().delete(
                            ContentUris.withAppendedId(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI, Double.valueOf(movie.getId()).longValue()),
                            null,
                            null);
                    if (numDeleted > 0) {
                        setIconUnstarred();
                        Toast.makeText(getContext(), getString(R.string.alert_unstarred), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.alert_unstarred_error), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private void setIconStarred() {
        Drawable mIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_black_48dp);
        mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        starIv.setImageDrawable(mIcon);
    }

    private void setIconUnstarred() {
        Drawable mIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_border_black_48dp);
        mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        starIv.setImageDrawable(mIcon);
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

    private boolean isMovieStarred() {
        Cursor result = getActivity().getContentResolver().query(
                ContentUris.withAppendedId(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI, Double.valueOf(movie.getId()).longValue()),
                new String[]{FavoriteMoviesContract.FavoriteMovieEntry._ID},
                null,
                null,
                null);
        return result != null && result.getCount() > 0;
    }

    private Bundle createLoaderBundle() {
        Bundle bundle = new Bundle();
        bundle.putDouble(KEY_MOVIE_ID, movie.getId());
        return bundle;
    }

    private LoaderCallbacks trailerLoaderCallbacks = new LoaderCallbacks<List<MovieTrailer>>() {
        @NonNull
        @Override
        public Loader<List<MovieTrailer>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<MovieTrailer>>(getContext()) {
                @Nullable
                @Override
                public List<MovieTrailer> loadInBackground() {
                    if (movie.getTrailers() == null) {
                        return TmdbApiService.getInstance(getContext()).getVideosByMovie(args.getDouble(KEY_MOVIE_ID));
                    } else {
                        return movie.getTrailers();
                    }
                }

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<MovieTrailer>> loader, List<MovieTrailer> trailers) {
            if (trailers != null && !trailers.isEmpty()) {
                trailersLv.setFocusable(false);
                movie.setTrailers(trailers);
                trailerAdapter = new MovieTrailerItemAdapter(getContext(), trailers);
                trailersLv.setAdapter(trailerAdapter);
                trailersLv.setVisibility(View.VISIBLE);
                trailersLabelTv.setVisibility(View.VISIBLE);
            } else {
                trailersLv.setVisibility(View.GONE);
                trailersLabelTv.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<MovieTrailer>> loader) {
        }
    };

    private LoaderCallbacks reviewsLoaderCallbacks = new LoaderCallbacks<List<MovieReview>>() {
        @NonNull
        @Override
        public Loader<List<MovieReview>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<MovieReview>>(getContext()) {
                @Nullable
                @Override
                public List<MovieReview> loadInBackground() {
                    if (movie.getTrailers() == null) {
                        return TmdbApiService.getInstance(getContext()).getReviewsByMovie(args.getDouble(KEY_MOVIE_ID));
                    } else {
                        return movie.getReviews();
                    }
                }

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> reviews) {
            if (reviews != null && !reviews.isEmpty()) {
                reviewsLv.setFocusable(false);
                movie.setReviews(reviews);
                reviewAdapter = new MovieReviewItemAdapter(getContext(), reviews);
                reviewsLv.setAdapter(reviewAdapter);
                reviewsLv.setVisibility(View.VISIBLE);
                reviewsLabelTv.setVisibility(View.VISIBLE);
            } else {
                reviewsLv.setVisibility(View.GONE);
                reviewsLabelTv.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {
        }
    };
}
