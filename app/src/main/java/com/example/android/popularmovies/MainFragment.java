package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.sync.MoviesSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String SELECTED_KEY = "selected_position";
    private static final String MAIN_URI = "main_uri";
    private int mPosition = GridView.INVALID_POSITION;
    private GridView mGridView;


    public interface Callback {
        void onItemSelected(Uri uri, boolean firstDisplay);
    }

    private MainAdapter mMainAdapter;
    private Uri mMainUri;

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
        mMainAdapter = new MainAdapter(getActivity(), null, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        mGridView.setAdapter(mMainAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMovieClicked(parent, position);
                setPosition(position);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    private void onMovieClicked(AdapterView<?> parent, int position) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            String movieId = (Long.valueOf(cursor.getLong(COL_MOVIE_ID))).toString();
            Uri uri = MovieEntry.buildMovieTrailersReviewsUri(movieId);
            ((Callback) getActivity()).onItemSelected(uri, false);
        }
    }


//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
//        super.onActivityCreated(savedInstanceState);
//    }

    // We call the initLoader in the onResume instead of the onActivityCreated, because
    // fragment onResume method is called just after activity onResume (when onActivityCreated is
    // called before the activity onResume) and the activity onResume can possibly change the mUri
    @Override
    public void onResume() {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onResume();
    }

    public void onMainUriChange() {
        //Offline 'popularity.desc' and 'vote_average.desc' sort order will display
        // the favorites in the order chosen.
        deleteUnfavorites(getContext());
        if (Utility.isConnected(getContext()))
            syncDB();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);

    }


    public void syncDB() {
        MoviesSyncAdapter.syncImmediately(getActivity());
    }




    private static void deleteUnfavorites(Context context) {
        context.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                mMainUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMainAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    public void setPosition(int position) {
        mPosition = position;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMainAdapter.swapCursor(null);
    }

    public void setMainUri(Uri mMainUri) {
        this.mMainUri = mMainUri;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MAIN_URI, mMainUri);
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to GridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
