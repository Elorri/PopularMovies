package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryFragment extends Fragment {

    ArrayAdapter<String> mDiscoveryAdapter;

    public DiscoveryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create some mock data for the ListView.
        String[] data = {
                "MadMaxFuryRoad.jpg",
                "JurassicWorld.jpg",
                "AvengersLèredUltron.jpg",
                "AlaPoursuiteDeDemain.jpg",
                "LesMinions.jpg",
                "SanAndreas.jpg",
                "TerminatorGenisys.jpg"
        };
        List<String> imageNames = new ArrayList<String>(Arrays.asList(data));

        // The ArrayAdapter will take data and populate the GridView it's attached to.
        mDiscoveryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.grid_item_layout, R.id.grid_item_layout, imageNames);

        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);


        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        gridView.setAdapter(mDiscoveryAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMoviesTask movieTask = new FetchMoviesTask();
        movieTask.execute("popularity.desc");
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private final String API_KEY = "4691965cfc3e6f0591bc595986e92e84";

        @Override
        protected String[] doInBackground(String... params) {
            URL url = constructTMDbURL(params[0]);
            String popularMoviesJsonStr = getJsonString(url);

            try {
                return getDiscoveryDataFromJson(popularMoviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mDiscoveryAdapter.clear();
                mDiscoveryAdapter.addAll(result);
            }
        }

        private URL constructTMDbURL(String sortByValue) {
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


        private String getJsonString(URL url) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

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
                moviesJsonStr = buffer.toString();
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
            return moviesJsonStr;
        }


        private String[] getDiscoveryDataFromJson(String moviesJsonStr) throws JSONException {


            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String ID = "id";
            final String POSTER_PATH = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);


            String[] moviesList = new String[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {

                // Get the JSON object representing the movie
                JSONObject aMovie = moviesArray.getJSONObject(i);

                // For now, only the poster path
                String posterPath = aMovie.getString(POSTER_PATH);
                moviesList[i] = posterPath;
            }

            for (String s : moviesList) {
                Log.v(LOG_TAG, "Movies entries: " + s);
            }
            return moviesList;

        }


    }

}
