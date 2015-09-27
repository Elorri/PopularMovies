package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movie=(Movie)intent.getSerializableExtra(Intent.EXTRA_TEXT);
            String id = movie.getId();
            TextView detail_text = ((TextView) rootView.findViewById(R.id.title));
            detail_text.setText(id);
            ImageView posterImage=(ImageView)rootView.findViewById(R.id.posterImage);
            Picasso.with(getActivity()).load(constructPosterImageURL(movie.getPosterName()).toString()).into(posterImage);
        }
        return rootView;
    }

    private URL constructPosterImageURL(String posterName) {
        try {
            final String BASE_URL = "http://image.tmdb.org/t/p/";
            final String SIZE = "w185";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SIZE)
                    .appendPath(posterName)
                    .build();

            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error " + e);
            return null;
        }
    }
}
