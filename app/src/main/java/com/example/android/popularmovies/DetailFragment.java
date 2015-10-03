package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
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

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String movieId;
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
            movieId =  intent.getStringExtra(Intent.EXTRA_TEXT);
            title = (TextView) rootView.findViewById(R.id.title);
            posterImage = (ImageView) rootView.findViewById(R.id.posterImage);
            overview = (TextView) rootView.findViewById(R.id.overview);
            voteAverage = (TextView) rootView.findViewById(R.id.voteAverage);
            releaseDate = (TextView) rootView.findViewById(R.id.releaseYear);
            duration = (TextView) rootView.findViewById(R.id.duration);
        }
        return rootView;
    }


    @Override
    public void onStart() {
        receiver = new InternetReceiver(getActivity()) {
            @Override
            protected void refresh() {
                FetchOneMovieTask movieTask = new FetchOneMovieTask();
                movieTask.execute(movieId);
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

    public class FetchOneMovieTask extends AsyncTask<String, Void, Movie> {


        @Override
        protected Movie doInBackground(String... params) {
            return tmdbAccess.getMovieById(params[0]);
        }


        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {
                title.setText(result.getTitle());
                overview.setText(result.getOverview());
                voteAverage.setText(result.getVoteAverage()+getString(R.string.rateMax));
                releaseDate.setText(result.getReleaseDate().split("-")[0]); //To extract the year from the date
                duration.setText(result.getDuration()+" "+getString(R.string.min));
                Picasso.with(getActivity()).load(tmdbAccess.constructPosterImageURL(result.getPosterName()).toString()).into(posterImage);
            }
        }

    }
}