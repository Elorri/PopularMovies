package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryFragment extends Fragment {


    ArrayList<Movie> mDiscoverMoviesPosterPath;
    CustomAdapter mDiscoveryAdapter;
    private TmdbAccess tmdbAccess;

    public DiscoveryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CustomAdapter will take poster path in the String[] and populate the GridView with the corresponding images.
        mDiscoverMoviesPosterPath = new ArrayList<Movie>();
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
                mDiscoveryAdapter.refresh(result);
            }
        }


    }

    public class CustomAdapter extends ArrayAdapter<Movie> {

        public CustomAdapter(Context context, ArrayList<Movie> thumbIds) {
            //We pass '0' for the 'int resource' layout, because the layout we are going to inflate can vary. Will be R.layout.grid_item_layout_default or R.layout.grid_item_layout
            super(context,0,thumbIds);
        }

        public void refresh(Movie[] results) {
            clear();
            addAll(Arrays.asList(results));
            // no need to call notifyDataSetChanged() because it's already call in addAll
        }

        public int getCount() {
            return super.getCount();
        }

        public Movie getItem(int position) {
            return super.getItem(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View customView;
            if (getItem(position).getPosterName() == null) { //The poster image doesn't exist. Display the movie title instead
                if ((convertView == null)||(convertView instanceof ImageView))  {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    customView =  inflater.inflate(R.layout.grid_item_layout_default, parent, false);
                } else {
                    customView = convertView;
                }
                ((TextView)customView).setText(getItem(position).getTitle());
            } else {//The poster image exists, we can display the image
                if((convertView == null)||(convertView instanceof TextView)) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    customView =  inflater.inflate(R.layout.grid_item_layout, parent, false);
                } else {
                    customView =  convertView;
                }
                URL posterURL=tmdbAccess.constructPosterImageURL(getItem(position).getPosterName());
                Picasso.with(getActivity()).load(posterURL.toString()).into((ImageView)customView);
            }
            return customView;
        }

    }
}
