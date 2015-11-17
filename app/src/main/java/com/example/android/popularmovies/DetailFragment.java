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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";
    private static Uri mUri;

    private static final int MOVIE_LOADER = 0;

    private DetailAdapter mDetailAdapter;
    private ListView mDetailListView;
    private ShareActionProvider mShareActionProvider;
    private Uri mFirstVideoUri;



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mFirstVideoUri != null){
            setShareActionProvider(mFirstVideoUri);
        }
    }

    private void setShareActionProvider(Uri uri) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent(uri));
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }

    }

    private Intent createShareIntent(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, uri.toString());
        return shareIntent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        mDetailListView = (ListView) rootView.findViewById(R.id.detail_list);
        mDetailAdapter = DetailAdapter.getInstance(getActivity(), null, 0, this);
        mDetailListView.setAdapter(mDetailAdapter);

//        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
//                " : DetailFragment.mDetailAdapter :  object created");
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
//                " : DetailFragment Loader :  object created");
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
//            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
//                    " : DetailFragment.mDetailListView :  change state");
            if (mUri != null) {
                cursorLoader = new CursorLoader(getActivity(),
                        mUri,
                        null,
                        null,
                        null,
                        null);
            }
        }
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mDetailAdapter.swapCursor(data);
//            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
//                    " : Cursor : " + data.getCount());
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDetailAdapter.swapCursor(null);
//        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
//                " : Cursor : ");
    }

    public void onMainUriChange() {
        if (mUri != null) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }
    }



    public void onFirstTrailerUriKnown(Uri uri) {
        this.mFirstVideoUri = uri;
        if (mFirstVideoUri != null)
            setShareActionProvider(mFirstVideoUri);
    }


}