package com.example.android.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

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
public class TmdbSync {

private static TmdbSync instance=null;


    private static final String LOG_TAG = "PopularMovies";
    private static final String API_KEY = "";
    private final Context context;



    private TmdbSync(Context context){
        this.context=context;
    }

    static TmdbSync getInstance(Context context){
        if (instance == null)
            instance = new TmdbSync(context);
        return instance;
    }

    public void syncMovies(String sortBy) {
        URL url = buildMovieListQuery(sortBy);
        String popularMoviesJsonStr = getJsonString(url);
        try {
             syncMoviesFromJson(popularMoviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    private static ContentValues getMovieById(String id){
        URL url = buildMovieDetailQuery(id);
        String aMovieJsonStr = getJsonString(url);

        try {
            return getOneMovieFromJson(aMovieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildPosterImageURL(String posterName) {
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

    private static URL buildMovieListQuery(String sortByValue) {
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

    private static URL buildMovieDetailQuery(String id) {
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



    private static String getJsonString(URL url) {
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


        //ContentValues[] moviesList = new ContentValues[moviesArray.length()];
        int i = 0;
        for (; i < moviesArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject aMovie = moviesArray.getJSONObject(i);

            // Create Movie Object
            String movieId = aMovie.getString(ID);
           ContentValues aMovieValue = getMovieById(movieId);

            //We insert each movie one by one and we insert trailers and reviews in bulk. We
            // can't insert movies in bulk because of the foreign key constraint on trailers and
            // reviews.
            context.getContentResolver().insert(MovieEntry.CONTENT_URI, aMovieValue);
            syncTrailers(movieId);
            syncReviews(movieId);
        }
        //int inserted=context.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, moviesList);
        Log.d(LOG_TAG, "Inserted "+i+" movies. Sync completed");
    }

    private void syncTrailers(String movieId) {
        URL url = buildTrailersListQuery(movieId);
        String trailersJsonStr = getJsonString(url);
        try {
            syncTrailersFromJson(trailersJsonStr, movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void syncReviews(String movieId) {
        URL url = buildReviewsListQuery(movieId);
        String reviewsJsonStr = getJsonString(url);
        try {
            syncReviewsFromJson(reviewsJsonStr, movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private static URL buildTrailersListQuery(String movieId) {
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String TRAILERS_QUERY_PARAM = "videos";
                final String KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath(TRAILERS_QUERY_PARAM)
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        .build();
                return new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error " + e);
                return null;
            }
    }

    private static URL buildReviewsListQuery(String movieId) {
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String REVIEWS_QUERY_PARAM = "reviews";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(REVIEWS_QUERY_PARAM)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .build();
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error " + e);
            return null;
        }
    }

    private void syncTrailersFromJson(String trailersJsonStr, String movieId) throws
            JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = trailersJson.getJSONArray(RESULTS);


        ContentValues[] trailersList = new ContentValues[trailersArray.length()];
        for (int i = 0; i < trailersArray.length(); i++) {

            // Get the JSON object representing the trailer
            JSONObject aTrailer = trailersArray.getJSONObject(i);
            trailersList[i] = getOneTrailerFromJson(aTrailer, movieId);
        }
        int inserted=context.getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI,
                trailersList);
        Log.d(LOG_TAG, "Inserted "+inserted+" trailers.");
    }

    private void syncReviewsFromJson(String reviewsJsonStr, String movieId) throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);


        ContentValues[] reviewsList = new ContentValues[reviewsArray.length()];
        for (int i = 0; i < reviewsArray.length(); i++) {

            // Get the JSON object representing the review
            JSONObject aReview = reviewsArray.getJSONObject(i);
            reviewsList[i] = getOneReviewFromJson(aReview, movieId);
        }
        int inserted=context.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, reviewsList);
        Log.d(LOG_TAG, "Inserted "+inserted+" reviews.");
    }



    private static ContentValues getOneMovieFromJson(String theMovieJsonStr) throws JSONException {

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






    private static ContentValues getOneTrailerFromJson(JSONObject aTrailer, String movieId) throws
            JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String _ID = "id";
        final String KEY="key";
        final String NAME = "name";
        final String TYPE = "type";

        String id = aTrailer.getString(_ID);
        String key =aTrailer.getString(KEY);
        String name = aTrailer.getString(NAME);
        String type =aTrailer.getString(TYPE);

        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailerEntry._ID,id);
        trailerValues.put(TrailerEntry.COLUMN_KEY,key);
        trailerValues.put(TrailerEntry.COLUMN_NAME,name);
        trailerValues.put(TrailerEntry.COLUMN_TYPE,type);
        trailerValues.put(TrailerEntry.COLUMN_MOVIE_ID,movieId);
        return trailerValues;
    }

    private ContentValues getOneReviewFromJson(JSONObject aReview, String movieId) throws
            JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String _ID = "id";
        final String AUTHOR="author";
        final String CONTENT = "content";

        String _id = aReview.getString(_ID);
        String author =aReview.getString(AUTHOR);
        String content = aReview.getString(CONTENT);

        ContentValues reviewValues = new ContentValues();
        reviewValues.put(ReviewEntry._ID,_id);
        reviewValues.put(ReviewEntry.COLUMN_AUTHOR,author);
        reviewValues.put(ReviewEntry.COLUMN_CONTENT,content);
        reviewValues.put(ReviewEntry.COLUMN_MOVIE_ID,movieId);
        return reviewValues;
    }
}
