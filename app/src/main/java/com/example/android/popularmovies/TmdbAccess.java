package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

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




    private static final String LOG_TAG = "PopularMovies";
    private final String API_KEY = "4691965cfc3e6f0591bc595986e92e84";
    private final Context context;


    TmdbAccess(Context context){
        this.context=context;
    }

    public void syncMovies(String sortBy) {
        URL url = constructMovieListQuery(sortBy);
        String popularMoviesJsonStr = getJsonString(url);

        try {
             syncMoviesFromJson(popularMoviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    private ContentValues getMovieById(String id){
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




    private void syncMoviesFromJson(String moviesJsonStr) throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String ID = "id";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);


        ContentValues[] moviesList = new ContentValues[moviesArray.length()];
        for (int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject aMovie = moviesArray.getJSONObject(i);

            // Create Movie Object
            String id = aMovie.getString(ID);
            moviesList[i] = getMovieById(id);
        }
        int inserted=context.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, moviesList);
        Log.d(LOG_TAG, "Sync task complete. "+inserted+" reccords inserted");
    }

    private ContentValues getOneMovieFromJson(String theMovieJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String ID = "id";
        final String TITLE="title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String DURATION = "runtime";
        final String POPULARITY = "popularity";

        JSONObject movieJson = new JSONObject(theMovieJsonStr);

        String _id = movieJson.getString(ID);
        String title =movieJson.getString(TITLE);
        String posterPath = movieJson.getString(POSTER_PATH);
        String posterName = null;
        if (!posterPath.equals("null"))
            posterName = posterPath.split("/")[1]; //To remove the unwanted '/' given by the api
        String releaseDate =movieJson.getString(RELEASE_DATE);
        String duration =movieJson.getString(DURATION);
        String rate =movieJson.getString(VOTE_AVERAGE);
        String popularity =movieJson.getString(POPULARITY);
        String plotSynopsis =movieJson.getString(OVERVIEW);


        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry._ID,_id);
        movieValues.put(MovieEntry.COLUMN_TITLE,title);
        movieValues.put(MovieEntry.COLUMN_DURATION,duration);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE,releaseDate);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH,posterName);
        movieValues.put(MovieEntry.COLUMN_PLOT_SYNOPSIS,plotSynopsis);
        movieValues.put(MovieEntry.COLUMN_RATE,rate);
        movieValues.put(MovieEntry.COLUMN_POPULARITY,popularity);
        movieValues.put(MovieEntry.COLUMN_FAVORITE,MovieEntry.FAVORITE_OFF_VALUE);
        return movieValues;

    }




//    private ContentValues getOneReviewFromJson(String theReviewJsonStr) throws JSONException {
//
//        // These are the names of the JSON objects that need to be extracted.
//        final String ID = "id";
//        final String TITLE="title";
//        final String POSTER_PATH = "poster_path";
//        final String OVERVIEW = "overview";
//        final String VOTE_AVERAGE = "vote_average";
//        final String RELEASE_DATE = "release_date";
//        final String DURATION = "runtime";
//
//        JSONObject movieJson = new JSONObject(theMovieJsonStr);
//
//        String id = movieJson.getString(ID);
//        String title =movieJson.getString(TITLE);
//        String posterPath = movieJson.getString(POSTER_PATH);
//        String posterName = null;
//        if (!posterPath.equals("null"))
//            posterName = posterPath.split("/")[1]; //To remove the unwanted '/' given by the api
//        String releaseDate =movieJson.getString(RELEASE_DATE);
//        String duration =movieJson.getString(DURATION);
//        String voteAverage =movieJson.getString(VOTE_AVERAGE);
//        String overview =movieJson.getString(OVERVIEW);
//
//
//        ContentValues movieValues = new ContentValues();
//        reviewValues.put(ReviewEntry._ID,_id);
//        reviewValues.put(ReviewEntry.COLUMN_AUTHOR,author);
//        reviewValues.put(ReviewEntry.COLUMN_CONTENT,content);
//        reviewValues.put(ReviewEntry.COLUMN_MOVIE_ID,movie_id);
//        return movieValues;
//    }
//
//    private ContentValues getTrailerFromJson(String theTrailerJsonStr) throws JSONException {
//
//        // These are the names of the JSON objects that need to be extracted.
//        final String ID = "id";
//        final String TITLE="title";
//        final String POSTER_PATH = "poster_path";
//        final String OVERVIEW = "overview";
//        final String VOTE_AVERAGE = "vote_average";
//        final String RELEASE_DATE = "release_date";
//        final String DURATION = "runtime";
//
//        JSONObject movieJson = new JSONObject(theMovieJsonStr);
//
//        String id = movieJson.getString(ID);
//        String title =movieJson.getString(TITLE);
//        String posterPath = movieJson.getString(POSTER_PATH);
//        String posterName = null;
//        if (!posterPath.equals("null"))
//            posterName = posterPath.split("/")[1]; //To remove the unwanted '/' given by the api
//        String releaseDate =movieJson.getString(RELEASE_DATE);
//        String duration =movieJson.getString(DURATION);
//        String voteAverage =movieJson.getString(VOTE_AVERAGE);
//        String overview =movieJson.getString(OVERVIEW);
//
//
//        ContentValues movieValues = new ContentValues();
//        trailerValues.put(TrailerEntry._ID,_id);
//        trailerValues.put(TrailerEntry.COLUMN_KEY,key);
//        trailerValues.put(TrailerEntry.COLUMN_NAME,name);
//        trailerValues.put(TrailerEntry.COLUMN_TYPE,type);
//        trailerValues.put(TrailerEntry.COLUMN_MOVIE_ID,movie_id);
//        return movieValues;
//    }


}
