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
    private Movie movie;

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

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movie = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
            title = (TextView) rootView.findViewById(R.id.title);
            posterImage = (ImageView) rootView.findViewById(R.id.posterImage);
            overview = (TextView) rootView.findViewById(R.id.overview);
            voteAverage = (TextView) rootView.findViewById(R.id.voteAverage);
            releaseDate = (TextView) rootView.findViewById(R.id.releaseYear);
            duration = (TextView) rootView.findViewById(R.id.duration);
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

    @Override
    public void onStart() {
        super.onStart();
        FetchOneMovieTask movieTask = new FetchOneMovieTask();
        movieTask.execute(movie);
    }

    public class FetchOneMovieTask extends AsyncTask<Movie, Void, Movie> {

        private final String LOG_TAG = FetchOneMovieTask.class.getSimpleName();
        private final String API_KEY = "4691965cfc3e6f0591bc595986e92e84";

        @Override
        protected Movie doInBackground(Movie... params) {
            URL url = constructMovieDetailQuery(params[0]);
            String popularMoviesJsonStr = getJsonString(url);

            try {
                return getMovieDataFromJson(params[0], popularMoviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {
                title.setText(result.getTitle());
                overview.setText(result.getOverview());
                voteAverage.setText(result.getVoteAverage()+getString(R.string.rateMax));
                releaseDate.setText(result.getReleaseDate().split("-")[0]); //To extract the year from the date
                duration.setText(result.getDuration()+" "+getString(R.string.min));
            }
        }

        private Movie getMovieDataFromJson(Movie theMovie, String theMovieJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String DURATION = "runtime";

            JSONObject movieJson = new JSONObject(theMovieJsonStr);
            theMovie.setTitle(movieJson.getString(TITLE));
            theMovie.setOverview(movieJson.getString(OVERVIEW));
            theMovie.setVoteAverage(movieJson.getString(VOTE_AVERAGE));
            theMovie.setReleaseDate(movieJson.getString(RELEASE_DATE));
            theMovie.setDuration(movieJson.getString(DURATION));
            return theMovie;

        }


        private URL constructMovieDetailQuery(Movie aMovie) {
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String SORTBY_PARAM = "sort_by";
                final String KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(aMovie.getId())
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        .build();
                return new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error " + e);
                return null;
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