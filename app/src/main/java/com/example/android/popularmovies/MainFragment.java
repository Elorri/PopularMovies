package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.sync.MoviesSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public interface Callback {
        void onItemSelected(Uri uri, boolean firstDisplay);
    }

    private MainAdapter mMainAdapter;
    private TmdbAccess tmdbAccess;

    private static final int MOVIES_LOADER = 0;


    private static final String[] MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_TITLE
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_PATH = 1;
    static final int COL_TITLE = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // The CursorAdapter will take data from our cursor and populate the GridView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        tmdbAccess = new TmdbAccess(getContext());
        mMainAdapter = new MainAdapter(getActivity(), null, 0, tmdbAccess);

        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : TmdbAccess :  object created");
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : MainAdapter :  object created");
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : MainFragment :  change state");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("PopularMovies", "onCreateView " + Thread.currentThread().getStackTrace()[2]);
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        gridView.setAdapter(mMainAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMovieClicked(parent, position);
            }
        });
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : GridView :  object created");
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : MainFragment :  change state");
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : MainFragmentView :  object created");
        return rootView;
    }

    private void onMovieClicked(AdapterView<?> parent, int position) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            String movieId = (Long.valueOf(cursor.getLong(COL_MOVIE_ID))).toString();
            Uri uri = MovieEntry.buildMovieTrailersReviewsUri(movieId);
            Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                    " : Uri :  object created");
            ((Callback) getActivity()).onItemSelected(uri, false);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : MainFragment Loader :  object created");
        super.onActivityCreated(savedInstanceState);
    }


    public void onSettingsChange() {
        //Offline 'popularity.desc' and 'vote_average.desc' sort order will display
        // the favorites in the order chosen.
        deleteUnfavorites(getContext());
        if (isConnected())
            syncDB();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : MainFragment Loader :  change state");
    }


    public void syncDB() {
        MoviesSyncAdapter.syncImmediately(getActivity());
    }


    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : isConnected boolean :  object created");
        return isConnected;
    }

    private static void deleteUnfavorites(Context context) {
        //Need to delete Trailer and Reviews entry first to avoid foreign key conflict
        //Need to delete Trailer and Reviews, because 'on delete cascade does not seems to work'
        context.getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
    }


    private Uri buildMoviesUri() {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : movies uri :  object created");
        String sortBy = Utility.getSortOrderPreferences(getContext());
        if (sortBy.equals(getString(R.string.pref_sort_order_favorite)))
            return MovieEntry.buildMoviesFavoriteUri();
        return MovieEntry.buildMoviesSortByUri(sortBy);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : CursorLoader :  object created");
        return new CursorLoader(getActivity(),
                buildMoviesUri(),
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMainAdapter.swapCursor(data);
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : mMainAdapter :  change state");
        //To avoid 'java.lang.IllegalStateException: Can not perform this action inside of onLoadFinished'
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((Callback) getActivity()).onItemSelected(mMainAdapter.getmUriFirstItem(), true);
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMainAdapter.swapCursor(null);
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : onLoaderReset" +
                " : mMainAdapter :  change state");
    }


}
