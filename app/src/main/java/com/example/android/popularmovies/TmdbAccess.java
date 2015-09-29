package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
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
 * Created by Elorri-user on 28/09/2015.
 */
public class TmdbAccess {


    private static final String LOG_TAG = TmdbAccess.class.getSimpleName();
    private final String API_KEY = "real_api_key_here";

    public Movie[] getMoviesSortBy(String sortBy) {
        URL url = constructMovieListQuery(sortBy);
        String popularMoviesJsonStr = getJsonString(url);

        try {
            return getMoviesFromJson(popularMoviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    public Movie getMovieById(String id){
        URL url = constructMovieDetailQuery(id);
        String popularMoviesJsonStr = getJsonString(url);

        try {
            return getOneMovieFromJson(popularMoviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    public URL constructPosterImageURL(String posterName) {
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

    private URL constructMovieListQuery(String sortByValue) {
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORTBY_PARAM = "sort_by";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORTBY_PARAM, sortByValue)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .build();

            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error " + e);
            return null;
        }
    }

    private URL constructMovieDetailQuery(String id) {
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(id)
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

    private Movie[] getMoviesFromJson(String moviesJsonStr) throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String ID = "id";
        final String TITLE="title";
        final String POSTER_PATH = "poster_path";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);


        Movie[] moviesList = new Movie[moviesArray.length()];
        for (int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject aMovie = moviesArray.getJSONObject(i);

            // Create Movie Object
            String id = aMovie.getString(ID);
            String title =aMovie.getString(TITLE);
            String posterPath = aMovie.getString(POSTER_PATH);
            String posterName = null;
            if (!posterPath.equals("null"))
                posterName = posterPath.split("/")[1]; //To remove the unwanted '/' given by the api
            moviesList[i] = new Movie(id, title, posterName);
        }
        return moviesList;

    }

    private Movie getOneMovieFromJson(String theMovieJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String ID = "id";
        final String TITLE="title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String DURATION = "runtime";

        JSONObject movieJson = new JSONObject(theMovieJsonStr);

        String id = movieJson.getString(ID);
        String title =movieJson.getString(TITLE);
        String posterPath = movieJson.getString(POSTER_PATH);
        String posterName = null;
        if (!posterPath.equals("null"))
            posterName = posterPath.split("/")[1]; //To remove the unwanted '/' given by the api
        String releaseDate =movieJson.getString(RELEASE_DATE);
        String duration =movieJson.getString(DURATION);
        String voteAverage =movieJson.getString(VOTE_AVERAGE);
        String overview =movieJson.getString(OVERVIEW);

        return new Movie(id, title, posterName, releaseDate, duration, voteAverage, overview);

    }
}
