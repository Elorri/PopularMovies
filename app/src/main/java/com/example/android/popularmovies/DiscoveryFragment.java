package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryFragment extends Fragment {


    Movie[] mDiscoverMoviesPosterPath;
    CustomAdapter mDiscoveryAdapter;
    private TmdbAccess tmdbAccess;

    public DiscoveryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CustomAdapter will take poster path in the String[] and populate the GridView with the corresponding images.
        mDiscoverMoviesPosterPath = new Movie[]{};
        mDiscoveryAdapter = new CustomAdapter(getActivity(), mDiscoverMoviesPosterPath);

        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        gridView.setAdapter(mDiscoveryAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mDiscoveryAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movie.getId());
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMoviesTask movieTask = new FetchMoviesTask();
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = sharedPrefs.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_popularity));
        movieTask.execute(sortType);
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            tmdbAccess=new TmdbAccess();
            return tmdbAccess.getMoviesSortBy(params[0]);
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mDiscoveryAdapter.updateResults(result);
            }
        }


    }

    public class CustomAdapter extends BaseAdapter {
        private Context mContext;
        private Movie[] mThumbIds;

        public CustomAdapter(Context c, Movie[] thumbIds) {
            mContext = c;
            mThumbIds = thumbIds;
        }

        public void updateResults(Movie[] results) {
            mThumbIds = results;
            //Triggers the list update
            notifyDataSetChanged();
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Movie getItem(int position) {
            return mThumbIds[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View customView;
            if (mThumbIds[position].getPosterName() == null) { //The poster image doesn't exist. Display the movie title instead
                if ((convertView == null)||(convertView instanceof ImageView))  {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    customView =  inflater.inflate(R.layout.grid_item_layout_default, parent, false);
                } else {
                    customView = convertView;
                }
                ((TextView)customView).setText(mThumbIds[position].getTitle());
            } else {//The poster image exists, we can display the image
                if((convertView == null)||(convertView instanceof TextView)) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    customView =  inflater.inflate(R.layout.grid_item_layout, parent, false);
                } else {
                    customView =  convertView;
                }
                URL posterURL=tmdbAccess.constructPosterImageURL(mThumbIds[position].getPosterName());
                Picasso.with(getActivity()).load(posterURL.toString()).into((ImageView)customView);
            }
            return customView;
        }

    }
}
