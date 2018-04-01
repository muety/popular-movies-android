/*
    TODO: JavaDocs
    TODO: Tablet layout
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OverviewFragment.OnMovieSelectedListener {
    @Nullable
    @BindView(R.id.main_details_container)
    ViewGroup detailsContainer;

    private FragmentManager fragmentManager;

    public static final String KEY_SORT_ORDER = "sort_order";
    public static final String KEY_PAGE = "page";
    public static final String KEY_HIDE_LOADING_DIALOG = "hide_loading_dialog";
    public static final String KEY_MOVIE_ID = "movie_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_overview_container, new OverviewFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if (detailsContainer != null) {
            Fragment fragment = DetailsFragment.newInstance(movie);
            fragmentManager.beginTransaction().add(R.id.main_details_container, fragment).commit();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(KEY_MOVIE_ID, movie);
            startActivity(intent);
        }
    }
}
