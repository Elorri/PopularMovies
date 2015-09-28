package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        super.onStart();
        FetchOneMovieTask movieTask = new FetchOneMovieTask();
        movieTask.execute(movieId);
    }

    public class FetchOneMovieTask extends AsyncTask<String, Void, Movie> {

        private final String LOG_TAG = FetchOneMovieTask.class.getSimpleName();

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







        private String getJsonString(URL url) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Create the request http, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return jsonStr;
        }

    }
}