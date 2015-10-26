package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private Movie movie;
    private TmdbAccess tmdbAccess;

    private ImageView posterImage;
    private TextView title;
    private TextView overview;
    private TextView voteAverage;
    private TextView releaseDate;
    private TextView duration;

    private BroadcastReceiver receiver;

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
        tmdbAccess = new TmdbAccess();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            title = (TextView) rootView.findViewById(R.id.title);
            posterImage = (ImageView) rootView.findViewById(R.id.posterImage);
            overview = (TextView) rootView.findViewById(R.id.overview);
            voteAverage = (TextView) rootView.findViewById(R.id.voteAverage);
            releaseDate = (TextView) rootView.findViewById(R.id.releaseYear);
            duration = (TextView) rootView.findViewById(R.id.duration);

            title.setText(movie.getTitle());
            overview.setText(movie.getOverview());
            voteAverage.setText(movie.getVoteAverage() + getString(R.string.rateMax));
            releaseDate.setText(movie.getReleaseDate().split("-")[0]); //To extract the year from the date
            duration.setText(movie.getDuration() + " " + getString(R.string.min));
        }
        return rootView;
    }


    @Override
    public void onStart() {
        receiver = new InternetReceiver(getActivity()) {
            @Override
            protected void refresh() {
                Picasso.with(getActivity()).load(tmdbAccess.constructPosterImageURL(movie.getPosterName()).toString()).into(posterImage);
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(receiver, filter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(receiver);
        super.onStop();
    }

}