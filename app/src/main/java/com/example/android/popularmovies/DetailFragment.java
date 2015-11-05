package com.example.android.popularmovies;

import android.content.ContentValues;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;
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
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_DURATION,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieEntry.COLUMN_RATE,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_FAVORITE
    };

    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_DURATION = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_PLOT_SYNOPSIS = 5;
    static final int COL_RATE = 6;
    static final int COL_POPULARITY = 7;
    static final int COL_FAVORITE = 8;

    private static final String[] TRAILER_COLUMNS = {
            TrailerEntry._ID,
            TrailerEntry.COLUMN_KEY,
            TrailerEntry.COLUMN_NAME,
            TrailerEntry.COLUMN_TYPE,
            TrailerEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to TRAILER_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    static final int TRAILER_ID = 0;
    static final int COL_KEY = 1;
    static final int COL_NAME = 2;
    static final int COL_TYPE = 3;
    static final int COL_MOVIE_ID_T = 4;


    private static final String[] REVIEWS_COLUMNS = {
            ReviewEntry._ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT,
            ReviewEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to REVIEWS_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    static final int REVIEWS_ID = 0;
    static final int COL_AUTHOR = 1;
    static final int COL_CONTENT = 2;
    static final int COL_MOVIE_ID_R = 3;


    private long mId;
    private String mTitleValue;
    private int mDurationValue;
    private String mReleaseDateValue;
    private String mPosterPathValue;
    private String mPlotSynopsisValue;
    private Double mRateValue;
    private String mPopularityValue;
    private boolean mFavoriteValue;
    private ListView mTrailerListView;
    private ListView mReviewListView;
    private TrailersAdapter mTrailerAdapter;


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
        mTrailerListView = (ListView) rootView.findViewById(R.id.trailer_list);
        mReviewListView = (ListView) rootView.findViewById(R.id.review_list);

        mTrailerAdapter = new TrailersAdapter(getActivity(), null, 0);
        mTrailerListView.setAdapter(mTrailerAdapter);


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("PopularMovies", "onActivityCreated " + getClass().getSimpleName());
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        Log.e("PopularMovies", "onCreateLoader " + getClass().getSimpleName());

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            if (mUri != null) {
                switch (id) {
                    case MOVIE_LOADER:
                        Log.e("PopularMovies", "MOVIE_LOADER " + mUri.toString());
                        cursorLoader = new CursorLoader(getActivity(),
                                mUri,
                                MOVIE_COLUMNS,
                                null,
                                null,
                                null);
                        break;
                    case TRAILER_LOADER:
                        Log.e("PopularMovies", "TRAILER_LOADER " + TrailerEntry
                                .buildMovieTrailerUri(Long.parseLong(MovieEntry
                                        .getMovieIdFromMovieDetailUri(mUri))));
                        cursorLoader = new CursorLoader(getActivity(),
                                TrailerEntry.buildMovieTrailerUri(Long.parseLong(MovieEntry
                                        .getMovieIdFromMovieDetailUri(mUri))),
                                TRAILER_COLUMNS,
                                null,
                                null,
                                null);
                        break;
                    case REVIEW_LOADER:
                        Log.e("PopularMovies", "REVIEW_LOADER " + ReviewEntry
                                .buildMovieReviewUri(Long.parseLong(MovieEntry
                                        .getMovieIdFromMovieDetailUri(mUri))));
                        cursorLoader = new CursorLoader(getActivity(),
                                ReviewEntry.buildMovieReviewUri(Long.parseLong(MovieEntry
                                        .getMovieIdFromMovieDetailUri(mUri))),
                                REVIEWS_COLUMNS,
                                null,
                                null,
                                null);
                        break;
                    default:
                        break;
                }
            }
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("PopularMovies", "onLoadFinished "+this.getClass().getSimpleName());
        if (data != null && data.moveToFirst()) {
            switch (loader.getId()) {
                case MOVIE_LOADER:
                    Log.e("PopularMovies", "MOVIE_LOADER "+" "+this.getClass().getSimpleName());
                    mId = data.getLong(COL_ID);
                    mTitleValue = data.getString(COL_TITLE);
                    mDurationValue = data.getInt(COL_DURATION);
                    mReleaseDateValue = data.getString(COL_RELEASE_DATE).split("-")[0];//To extract the
                    // year  from the date
                    mPosterPathValue = data.getString(COL_POSTER_PATH);
                    mPlotSynopsisValue = data.getString(COL_PLOT_SYNOPSIS);
                    mRateValue = data.getDouble(COL_RATE);
                    mPopularityValue = data.getString(COL_POPULARITY);
                    mFavoriteValue = Utility.isFavorite(data.getInt(COL_FAVORITE));


                    mTitle.setText(mTitleValue);
                    mDuration.setText(mDurationValue + " " + getString(R.string.min));
                    mReleaseDate.setText(mReleaseDateValue);
                    Picasso.with(getActivity()).load(tmdbAccess.constructPosterImageURL(mPosterPathValue)
                            .toString()).into(mPosterImage);
                    mPlotSynopsis.setText(mPlotSynopsisValue);
                    mVoteAverage.setText(mRateValue + getString(R.string.rateMax));
                    mFavorite.setChecked(mFavoriteValue);
                    break;
                case TRAILER_LOADER:
                    Log.e("PopularMovies", "TRAILER_LOADER "+" "+this.getClass().getSimpleName());
                    mTrailerAdapter.swapCursor(data);
                    break;
                case REVIEW_LOADER:
                    Log.e("PopularMovies", "REVIEW_LOADER"+this.getClass().getSimpleName());
                    // do some more stuff here
                    break;
                default:
                    break;
            }

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MOVIE_LOADER:
                break;
            case TRAILER_LOADER:
                mTrailerAdapter.swapCursor(null);
                break;
            case REVIEW_LOADER:
                // do some more stuff here
                break;
            default:
                break;
        }
    }

    public void onSettingsChange() {
        if (mUri != null) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry._ID, mId);
        movieValues.put(MovieEntry.COLUMN_TITLE, mTitleValue);
        movieValues.put(MovieEntry.COLUMN_DURATION, mDurationValue);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, mReleaseDateValue);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, mPosterPathValue);
        movieValues.put(MovieEntry.COLUMN_PLOT_SYNOPSIS, mPlotSynopsisValue);
        movieValues.put(MovieEntry.COLUMN_RATE, mRateValue);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, mPopularityValue);
        movieValues.put(MovieEntry.COLUMN_FAVORITE, Utility.getDbFavoriteValue(isChecked));

        getContext().getContentResolver().update(MovieEntry.CONTENT_URI, movieValues, MovieEntry
                ._ID + "=?", new String[]{Long.toString(mId)});
    }
}