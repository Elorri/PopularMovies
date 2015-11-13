package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.sync.MoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "detail_fragment";
    private boolean mTwoPane;


    private Uri mMainUri;
    static final String MAIN_URI = "mMainUri";
    private MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility.thread() + " : MainActivity : object created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity); //This will call the MainFragment onCreate

        if (savedInstanceState == null)
            mMainUri = buildMoviesUri();
        else
            mMainUri = savedInstanceState.getParcelable(MAIN_URI);
        mMainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " :mMainFragment.mMainUri :  change state");
        mMainFragment.setMainUri(mMainUri);

        if (findViewById(R.id.detail_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id
                        .detail_fragment_container, new DetailFragment(), DETAILFRAGMENT_TAG).commit();
                Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : onCreate : " +
                        "DetailFragment " +
                        ": object created");
            }
        } else {
            mTwoPane = false;
        }
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : MainActivity.mTwoPane :  change state");
        MoviesSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : ");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : onOptionsItemSelected : SettingsActivity intent : object created");
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                "currentMainUri : object created");
        Uri currentMainUri = buildMoviesUri();
        if (!Utility.compareUris(mMainUri, currentMainUri)) {
            onMainUriChange(currentMainUri);
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                    "MainActivity.mMainUri : change state");
        }
    }

    private void onMainUriChange(Uri newUri) {
        mMainUri = newUri;
        mMainFragment.setMainUri(mMainUri);
        mMainFragment.onMainUriChange();

        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (null != detailFragment) {
            detailFragment.onMainUriChange();
        }
    }



    @Override
    public void onItemSelected(Uri uri, boolean firstDisplay) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, uri);

            DetailFragment fragment = new DetailFragment();
            Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : onItemSelected" +
                    " : DetailFragment :  object created");
            fragment.setArguments(arguments);
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : onItemSelected" +
                    " : DetailFragment :  change state");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            if (firstDisplay == false) {
                Intent intent = new Intent(this, DetailActivity.class);
                Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : DetailActivity intent : object created");
                intent.setData(uri);
                Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() +
                        " : DetailActivity intent :  change state");
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MAIN_URI, mMainUri);
    }

    private Uri buildMoviesUri() {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : movies uri :  object created");
        String sortBy = Utility.getSortOrderPreferences(this);
        if (sortBy.equals(getString(R.string.pref_sort_order_favorite)))
            return MovieContract.MovieEntry.buildMoviesFavoriteUri();
        return MovieContract.MovieEntry.buildMoviesSortByUri(sortBy);
    }

}
