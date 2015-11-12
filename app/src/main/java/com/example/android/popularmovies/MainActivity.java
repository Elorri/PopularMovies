package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.sync.MoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private static final String DETAILFRAGMENT_TAG = "detail_fragment";
    private boolean mTwoPane;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2]+" : "+Utility.thread()+" : MainActivity : object created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (findViewById(R.id.detail_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id
                        .detail_fragment_container, new DetailFragment(), DETAILFRAGMENT_TAG).commit();
                Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2]+ ": "+Utility.thread()+" : onCreate : " +
                        "DetailFragment " +
                        ": object created");
            }
        } else {
            mTwoPane = false;
        }
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : " +
                " : DetailFragment.mTwoPane :  change state");
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
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2]+ ": "+Utility.thread()+" : onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2]+ ": "+Utility.thread()+" : onOptionsItemSelected : SettingsActivity intent : object created");
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortOrderPreferences(this);
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2]+ ": "+Utility.thread()+" : MainFragment : object created");
            if (null != mainFragment) {
                mainFragment.onSettingsChange();
            }
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != detailFragment) {
                detailFragment.onSettingsChange();
            }
            mSortOrder = sortOrder;
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                    "MainActivity.mSortOrder : change state");
        }
    }

    @Override
    public void onItemSelected(Uri uri, boolean firstDisplay) {
        if(mTwoPane){
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, uri);

            DetailFragment fragment = new DetailFragment();
            Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : onItemSelected" +
                    " : DetailFragment :  object created");
            fragment.setArguments(arguments);
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread()+" : onItemSelected" +
                    " : DetailFragment :  change state");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }else{
            if(firstDisplay==false) {
                Intent intent = new Intent(this, DetailActivity.class);
                Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2]+ ": "+Utility.thread()+" : DetailActivity intent : object created");
                intent.setData(uri);
                Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": "+Utility.thread() +
                        " : DetailActivity intent :  change state");
                startActivity(intent);
            }
        }
    }




}
