package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CompoundButton.OnCheckedChangeListener {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";
    private static Uri mUri;
    private TmdbAccess tmdbAccess;

    private ImageView mPosterImage;
    private TextView mTitle;
    private TextView mPlotSynopsis;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private TextView mDuration;
    private SwitchCompat mFavorite;


    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_DURATION,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RATE,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    static final int COL_TITLE = 0;
    static final int COL_DURATION = 1;
    static final int COL_RELEASE_DATE = 2;
    static final int COL_POSTER_PATH = 3;
    static final int COL_PLOT_SYNOPSIS = 4;
    static final int COL_RATE = 5;
    static final int COL_FAVORITE = 6;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "will be the first video URL here");
        return shareIntent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        tmdbAccess = new TmdbAccess(getContext());
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mPosterImage = (ImageView) rootView.findViewById(R.id.posterImage);
        mPlotSynopsis = (TextView) rootView.findViewById(R.id.overview);
        mVoteAverage = (TextView) rootView.findViewById(R.id.voteAverage);
        mReleaseDate = (TextView) rootView.findViewById(R.id.releaseYear);
        mDuration = (TextView) rootView.findViewById(R.id.duration);
        mFavorite = (SwitchCompat) rootView.findViewById(R.id.favorite);
        mFavorite.setOnCheckedChangeListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("PopularMovies", "onActivityCreated " + getClass().getSimpleName());
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("PopularMovies", "onCreateLoader " + getClass().getSimpleName());
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            if (mUri != null)
                return new CursorLoader(getActivity(),
                        mUri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.e("PopularMovies", "mTitle : " + mTitle);
            mTitle.setText(data.getString(COL_TITLE));
            mDuration.setText(data.getString(COL_DURATION) + " " + getString(R.string.min));
            mReleaseDate.setText(data.getString(COL_RELEASE_DATE).split("-")[0]); //To extract the year from the date
            Picasso.with(getActivity()).load(tmdbAccess.constructPosterImageURL(data.getString(COL_POSTER_PATH)).toString()).into(mPosterImage);
            mPlotSynopsis.setText(data.getString(COL_PLOT_SYNOPSIS));
            mVoteAverage.setText(data.getString(COL_RATE) + getString(R.string.rateMax));
            mFavorite.setChecked(Utility.isFavorite(data.getString(COL_FAVORITE)));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onSettingsChange() {
        if (mUri != null) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(getContext(), "Switch checked ? "+isChecked, Toast.LENGTH_SHORT).show();
    }
}