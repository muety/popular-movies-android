/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.model.MovieSortOrder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OverviewFragment.OnMovieSelectedListener, OverviewFragment.OnDataLoadedListener {
    public static final String KEY_SORT_ORDER = "sort_order";
    public static final String KEY_PAGE = "page";
    public static final String KEY_HIDE_LOADING_DIALOG = "hide_loading_dialog";
    public static final String KEY_MOVIE_ID = "movie_id";

    private static final String KEY_ACTIVE_MOVIE = "active_movie";
    private static final String TAG_OVERVIEW_FRAGMENT = "overview_fragment";

    @Nullable
    @BindView(R.id.main_details_container)
    ViewGroup detailsContainer;

    private FragmentManager fragmentManager;
    private OverviewFragment overviewFragment;
    private Movie activeMovie;
    private Movie movieToSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();
        OverviewFragment foundFragment = (OverviewFragment) fragmentManager.findFragmentByTag(TAG_OVERVIEW_FRAGMENT);
        if (foundFragment == null) {
            overviewFragment = new OverviewFragment();
            fragmentManager.beginTransaction().replace(R.id.main_overview_container, overviewFragment, TAG_OVERVIEW_FRAGMENT).commit();
        } else {
            overviewFragment = foundFragment;
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_ACTIVE_MOVIE)) {
                movieToSelect = savedInstanceState.getParcelable(KEY_ACTIVE_MOVIE);
            }
        }

        updateTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeMovie != null) {
            outState.putParcelable(KEY_ACTIVE_MOVIE, activeMovie);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onMovieSelected(Movie movie) {
        activeMovie = movie;
        if (isTabletLayout()) {
            Fragment fragment = DetailsFragment.newInstance(movie);
            fragmentManager.beginTransaction().replace(R.id.main_details_container, fragment).commit();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(KEY_MOVIE_ID, movie);
            startActivity(intent);
        }
    }

    @Override
    public void onDataLoaded() {
        if (isTabletLayout() && activeMovie == null) {
            if (movieToSelect != null) {
                overviewFragment.selectMovie(movieToSelect);
                movieToSelect = null;
            }
            else {
                overviewFragment.selectMovieByIndex(0);
            }
        }

        updateTitle();
    }

    private void updateTitle() {
        if (overviewFragment == null) return;
        if (overviewFragment.getSortOrder() == MovieSortOrder.TOP_RATED)
            setTitle(R.string.title_top_rated_movies);
        else if (overviewFragment.getSortOrder() == MovieSortOrder.FAVORITE)
            setTitle(getString(R.string.favorite));
        else setTitle(R.string.title_popular_movies);
    }

    private boolean isTabletLayout() {
        return detailsContainer != null;
    }
}
