package com.github.n1try.popularmovies.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.model.MovieSortOrder;
import com.github.n1try.popularmovies.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.n1try.popularmovies.ui.MainActivity.KEY_HIDE_LOADING_DIALOG;
import static com.github.n1try.popularmovies.ui.MainActivity.KEY_PAGE;
import static com.github.n1try.popularmovies.ui.MainActivity.KEY_SORT_ORDER;

public class OverviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>> {
    public interface OnMovieSelectedListener {
        public void onMovieSelected(Movie movie);
    }

    private static final int LOADER_ID = 0;

    @BindView(R.id.main_movies_gv)
    GridView moviesContainer;
    @BindView(R.id.main_offline_indicator_container)
    ViewGroup offlineContainer;
    @BindView(R.id.main_offline_indicator_iv)
    ImageView offlineIndicatorIv;

    private SharedPreferences prefs;
    private MovieItemAdapter movieAdapter;
    private ProgressDialog loadingDialog;
    private MovieSortOrder currentOrder;
    private MovieSortOrder currentLoaderState;

    private OnMovieSelectedListener movieSelectedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        movieSelectedListener = (OnMovieSelectedListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, view);

        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        currentOrder = MovieSortOrder.valueOf(prefs.getString(KEY_SORT_ORDER, MovieSortOrder.POPULAR.name()));
        updateTitle(currentOrder);

        getLoaderManager().initLoader(LOADER_ID, createLoaderBundle(), this);

        moviesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movieItem = (Movie) adapterView.getAdapter().getItem(i);
                movieSelectedListener.onMovieSelected(movieItem);
            }
        });

        resetScrollListener();

        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setTitle(getResources().getString(R.string.main_loading_title));
        loadingDialog.setMessage(getResources().getString(R.string.main_loading_text));
        loadingDialog.setCancelable(false);

        Drawable mIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_cloud_off_black_48dp);
        mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextMedium), PorterDuff.Mode.SRC_IN);
        offlineIndicatorIv.setImageDrawable(mIcon);

        return view;
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle bundle) {
        final MovieSortOrder order = MovieSortOrder.valueOf(bundle.getString(KEY_SORT_ORDER));
        final int page = bundle.getInt(KEY_PAGE, 1);
        final boolean hideLoadingDialog = bundle.getBoolean(KEY_HIDE_LOADING_DIALOG, false);

        return new AsyncTaskLoader<List<Movie>>(getContext()) {
            @Override
            public List<Movie> loadInBackground() {
                switch (order) {
                    case POPULAR:
                        return TmdbApiService.getInstance(getContext()).getPopularMovies(page);
                    case TOP_RATED:
                        return TmdbApiService.getInstance(getContext()).getTopRatedMovies(page);
                    default:
                        return new ArrayList<>();
                }
            }

            @Override
            protected void onStartLoading() {
                if (!hideLoadingDialog) loadingDialog.show();
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies) {
        loadingDialog.dismiss();

        if (currentOrder != currentLoaderState) {
            // Initial load
            if (movies.isEmpty() && !AndroidUtils.isNetworkAvailable(getContext())) {
                moviesContainer.setVisibility(View.GONE);
                offlineContainer.setVisibility(View.VISIBLE);
            } else {
                moviesContainer.setVisibility(View.VISIBLE);
                offlineContainer.setVisibility(View.GONE);
                movieAdapter = new MovieItemAdapter(getActivity().getApplicationContext(), movies);
                moviesContainer.setAdapter(movieAdapter);
            }
        } else {
            // Load caused by infinite scrolling
            movieAdapter.addAll(movies);
            movieAdapter.notifyDataSetChanged();
        }

        currentLoaderState = currentOrder;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_popular:
                currentOrder = MovieSortOrder.POPULAR;
                prefs.edit().putString(KEY_SORT_ORDER, currentOrder.name()).apply();
                getLoaderManager().restartLoader(LOADER_ID, createLoaderBundle(), this);
                resetScrollListener();
                return true;
            case R.id.action_sort_rating:
                currentOrder = MovieSortOrder.TOP_RATED;
                prefs.edit().putString(KEY_SORT_ORDER, currentOrder.name()).apply();
                getLoaderManager().restartLoader(LOADER_ID, createLoaderBundle(), this);
                resetScrollListener();
                return true;
        }

        if (currentOrder != null) updateTitle(currentOrder);

        return super.onOptionsItemSelected(item);
    }

    private void resetScrollListener() {
        moviesContainer.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                getLoaderManager().restartLoader(LOADER_ID, createLoaderBundle(page, true), OverviewFragment.this);
                return true;
            }
        });
    }

    private void updateTitle(MovieSortOrder currentSortOrder) {
        if (currentSortOrder == MovieSortOrder.TOP_RATED) getActivity().setTitle(R.string.title_top_rated_movies);
        else getActivity().setTitle(R.string.title_popular_movies);
    }

    private Bundle createLoaderBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SORT_ORDER, currentOrder.name());
        return bundle;
    }

    private Bundle createLoaderBundle(int pageToLoad, boolean hideLoadingDialog) {
        Bundle bundle = createLoaderBundle();
        bundle.putInt(KEY_PAGE, pageToLoad);
        bundle.putBoolean(KEY_HIDE_LOADING_DIALOG, hideLoadingDialog);
        return bundle;
    }
}
