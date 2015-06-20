package com.brandonlassiter.traceroute;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

/**
 * Created by brandon on 6/19/15.
 */
public class TraceRouteMap extends MapFragment {

    GoogleMap map;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        map = getMap();

    }
}
