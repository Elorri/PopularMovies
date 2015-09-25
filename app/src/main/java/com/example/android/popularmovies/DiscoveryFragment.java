package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryFragment extends Fragment {

    String[] mDiscoverMoviesPosterPath;
    ImageAdapter mDiscoveryAdapter;

    public DiscoveryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The ImageAdapter will take poster path in the String[] and populate the GridView with the corresponding images.
        mDiscoverMoviesPosterPath = new String[]{};
        mDiscoveryAdapter = new ImageAdapter(getActivity(), mDiscoverMoviesPosterPath);

        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        gridView.setAdapter(mDiscoveryAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movie = mDiscoveryAdapter.getItem(position);
                Toast.makeText(getActivity(), movie, Toast.LENGTH_SHORT).show();
            }
        });
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
                mDiscoveryAdapter.updateResults(result);
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
            return moviesList;

        }


    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private String[] mThumbIds;

        public ImageAdapter(Context c, String[] thumbIds) {
            mContext = c;
            mThumbIds = thumbIds;
        }

        public void updateResults(String[] results) {
            mThumbIds = results;
            //Triggers the list update
            notifyDataSetChanged();
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public String getItem(int position) {
            return mThumbIds[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                imageView = (ImageView) inflater.inflate(R.layout.grid_item_layout, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + mThumbIds[position]).into(imageView);
            return imageView;
        }

    }
}
