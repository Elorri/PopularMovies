package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        tmdbAccess=new TmdbAccess();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movie =  intent.getParcelableExtra(Intent.EXTRA_TEXT);
            title = (TextView) rootView.findViewById(R.id.title);
            posterImage = (ImageView) rootView.findViewById(R.id.posterImage);
            overview = (TextView) rootView.findViewById(R.id.overview);
            voteAverage = (TextView) rootView.findViewById(R.id.voteAverage);
            releaseDate = (TextView) rootView.findViewById(R.id.releaseYear);
            duration = (TextView) rootView.findViewById(R.id.duration);

            title.setText(movie.getTitle());
            overview.setText(movie.getOverview());
            voteAverage.setText(movie.getVoteAverage()+getString(R.string.rateMax));
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