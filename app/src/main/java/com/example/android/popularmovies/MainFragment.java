package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.android.popularmovies.data.MovieContract.MovieEntry;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private MoviesAdapter mMoviesAdapter;
    private TmdbAccess tmdbAccess;
    private BroadcastReceiver receiver;

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
        Log.e("PopularMovies", "onCreate " + getClass().getSimpleName());
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // The CursorAdapter will take data from our cursor and populate the GridView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        tmdbAccess = new TmdbAccess(getContext());
        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0, tmdbAccess);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("PopularMovies", "onCreateView " + getClass().getSimpleName());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.setData(MovieEntry.buildMovieDetailUri(cursor.getLong(COL_MOVIE_ID)));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("PopularMovies", "onActivityCreated " + getClass().getSimpleName());
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void onStart() {
        String sortOrder=Utility.getSortOrderPreferences(getContext());
        updateUI(sortOrder);
        super.onStart();
    }

    public void updateUI(final String sortOrder){
        receiver = new InternetReceiver(getActivity()) {
            @Override
            protected void refresh() {
                syncDB(sortOrder);
            }

        };
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }


    public void syncDB(String sortOrder) {
            FetchMoviesTask movieTask = new FetchMoviesTask();
            movieTask.execute(sortOrder);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("PopularMovies", "onCreateLoader " + getClass().getSimpleName());
        return new CursorLoader(getActivity(),
                MovieEntry.buildMovieSortByUri(Utility.getSortOrderPreferences(getContext())),
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("PopularMovies", "onLoadFinished " + getClass().getSimpleName());
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("PopularMovies", "onLoaderReset " + getClass().getSimpleName());
        mMoviesAdapter.swapCursor(null);
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Cursor> {

        private static final String LOG_TAG = "PopularMovies";

        @Override
        protected Cursor doInBackground(String... params) {
            tmdbAccess.syncMovies(params[0]);
//            Cursor cur = getActivity().getContentResolver().query(
//                    MovieEntry.buildMovieSortByUri(Utility.getSortOrderPreferences(getContext())),
//                    MOVIE_COLUMNS,
//                    null,
//                    null,
//                    null);
            return null;
        }

//        @Override
//        protected void onPostExecute(Cursor result) {
//            if (result != null) {
//                //mDiscoveryAdapter.refresh(result);
//                mMoviesAdapter.changeCursor(result);
//            }
//        }

    }

}
