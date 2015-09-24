package com.example.android.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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
        mDiscoveryAdapter = new ArrayAdapter<String>( getActivity(),  R.layout.grid_item_layout, R.id.grid_item_layout, imageNames);

        View rootView=inflater.inflate(R.layout.fragment_discovery, container, false);


        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_discovery);
        gridView.setAdapter(mDiscoveryAdapter);
        return rootView;
    }
}
