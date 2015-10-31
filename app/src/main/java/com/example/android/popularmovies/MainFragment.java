package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {


//    private ArrayList<Movie> mDiscoverMovies;
//    private CustomAdapter mDiscoveryAdapter;
    private MoviesAdapter mMoviesAdapter;
    private TmdbAccess tmdbAccess;
    private BroadcastReceiver receiver;
    private String lastSortType;


    private static final String MOVIE_ARRAY_LIST_TAG = "movie_array_list_tag";


    private static final String[] MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_TITLE
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_PATH = 1;
    static final int COL_TITLE = 2;


    public MainFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //if the ArrayListof Movies already exist we use it otherwise we create another one.
//        if ((savedInstanceState == null) || (!savedInstanceState.containsKey(MOVIE_ARRAY_LIST_TAG)))
//            mDiscoverMovies = new ArrayList<Movie>();
//        else
//            mDiscoverMovies = savedInstanceState.getParcelableArrayList(MOVIE_ARRAY_LIST_TAG);
//        mDiscoveryAdapter = new CustomAdapter(getActivity(), mDiscoverMovies);
//        tmdbAccess = new TmdbAccess();

        Cursor cur = getActivity().getContentResolver().query(
                MovieEntry.buildMovieSortByUri(Utility.getSortOrderPreferences(getContext())),
                MOVIE_COLUMNS,
                null,
                null,
                null);
        // The CursorAdapter will take data from our cursor and populate the GridView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        tmdbAccess=new TmdbAccess(getContext());
        mMoviesAdapter = new MoviesAdapter(getActivity(), cur, 0,tmdbAccess);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        gridView.setAdapter(mMoviesAdapter);
//        gridView.setAdapter(mDiscoveryAdapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Movie movie = mDiscoveryAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, movie);
//                startActivity(intent);
//            }
//        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        receiver = new InternetReceiver(getActivity()) {
            @Override
            protected void refresh() {
                String sortOrder= Utility.getSortOrderPreferences(getContext());
                if ((lastSortType==null)||!sortOrder.equals(lastSortType)) {
                    FetchMoviesTask movieTask = new FetchMoviesTask();
                    movieTask.execute(sortOrder);
                    lastSortType=sortOrder;
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(receiver, filter);
        super.onStart();
    }


    @Override
    public void onStop() {
        getActivity().unregisterReceiver(receiver);
        super.onStop();
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Cursor> {

        private static final String LOG_TAG = "PopularMovies";

        @Override
        protected Cursor doInBackground(String... params) {
            //tmdbAccess.syncMovies(params[0]);
            Cursor cur = getActivity().getContentResolver().query(
                    MovieEntry.buildMovieSortByUri(Utility.getSortOrderPreferences(getContext())),
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
            return cur;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            if (result != null) {
                //mDiscoveryAdapter.refresh(result);
                mMoviesAdapter.changeCursor(result);
            }
        }

    }


    public class CustomAdapter extends ArrayAdapter<Movie> {

        public CustomAdapter(Context context, ArrayList<Movie> thumbIds) {
            //We pass '0' for the 'int resource' layout, because the layout we are going to inflate can vary. Will be R.layout.grid_item_layout_default or R.layout.grid_item_layout
            super(context, 0, thumbIds);
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
                if ((convertView == null) || (convertView instanceof ImageView)) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    customView = inflater.inflate(R.layout.grid_item_layout_default, parent, false);
                } else {
                    customView = convertView;
                }
                ((TextView) customView).setText(getItem(position).getTitle());
            } else {//The poster image exists, we can display the image
                if ((convertView == null) || (convertView instanceof TextView)) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    customView = inflater.inflate(R.layout.grid_item_layout, parent, false);
                } else {
                    customView = convertView;
                }
                URL posterURL = tmdbAccess.constructPosterImageURL(getItem(position).getPosterName());
                Picasso.with(getActivity()).load(posterURL.toString()).into((ImageView) customView);
            }
            return customView;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelableArrayList(MOVIE_ARRAY_LIST_TAG, mDiscoverMovies);
    }
}
