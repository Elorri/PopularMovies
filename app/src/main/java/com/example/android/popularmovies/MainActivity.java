package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private static final String DETAILFRAGMENT_TAG = "detail_fragment";
    private boolean mTwoPane;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id
                        .detail_fragment_container, new DetailFragment(), DETAILFRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
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
            if (null != mainFragment) {
                mainFragment.updateUI(sortOrder);
            }
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != detailFragment) {
                detailFragment.updateUI(sortOrder);
            }
            mSortOrder = sortOrder;
        }
    }

    @Override
    public void onItemSelected(Uri uri, boolean firstDisplay) {
        if(mTwoPane){
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, uri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }else{
            if(firstDisplay==false) {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }
}
