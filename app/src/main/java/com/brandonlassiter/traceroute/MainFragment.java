package com.brandonlassiter.traceroute;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by brandon on 6/11/15.
 */
public class MainFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    String mColorSelected;
    ParseObject currentRoom;
    ProgressDialog dialog;

    LatLng currentPosition;

    GoogleMap map;
    boolean updateDone = false;
    LocationManager locationManager;

    Handler h = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {

            centerMap(currentPosition);

        }

    };

    Runnable r2 = new Runnable() {
        @Override
        public void run() {

            if(RoomManager.getInstance().getCurrentRoom() != null) {

                ParseObject room = RoomManager.getInstance().getCurrentRoom();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("GameRoom");
                query.whereEqualTo("objectId", room.getObjectId());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {

                        if(parseObject != null) {
                            RoomManager.getInstance().setCurrentRoom(parseObject);

                            List<ArrayList> participants = parseObject.getList("users");

                            ArrayList<ParseObject> participants2 = participants.get(0);

                            for(ParseObject object : participants2) {
                                try {
                                    object.fetchIfNeeded();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            addMarkers(participants2);
                            h.postDelayed(r2, 5000);
                        }

                    }
                });

            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getPosition();

    }

    @Override
    public void onResume() {
        super.onResume();
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onClick(View v) {



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
    }

    public void getPosition() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);

        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 0, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                ParseUser me = ParseUser.getCurrentUser();

                if(me != null) {

                    me.put("currentLocation", new ParseGeoPoint(currentPosition.latitude, currentPosition.longitude));
                    me.saveInBackground();

                }
                h.post(r);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }

    public void centerMap(LatLng position) {

        if(position != null) {

            if(currentRoom != null) {
                map.setMyLocationEnabled(false);
            }

            CameraPosition newCamPos = new CameraPosition(position,
                    20.5f,
                    map.getCameraPosition().tilt, //use old tilt
                    map.getCameraPosition().bearing); //use old bearing
            map.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 1000, null);

        }

    }

    public void createRoom(String name) {
        dialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Creating Room");
        dialog.show();

        try {

            final ParseObject room = new ParseObject("GameRoom");

            ArrayList<ParseObject> users = new ArrayList<>();

            ParseObject participant = new ParseObject("Participant");
            participant.put("user", ParseUser.getCurrentUser());
            participant.put("color", "user");
            participant.saveInBackground();

            users.add(participant);

            room.put("users", Arrays.asList(users));
            room.put("name", name);

            room.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if(e == null) {

                        Toast.makeText(getActivity(), "Room " + room.getString("name") + " Created Successfully", Toast.LENGTH_LONG).show();
                        currentRoom = room;
                        dialog.hide();

                        RoomManager.getInstance().setCurrentRoom(room);
                        h.post(r2);
                        map.setMyLocationEnabled(false);


                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMarkers(final ArrayList<ParseObject> participants) {

        map.clear();
        final ArrayList<MarkerOptions> markers = new ArrayList<>();

        final int count = 0;
        for(final ParseObject participant : participants) {

            participant.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {

                    ParseObject pointer = participant.getParseObject("user");

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("objectId", pointer.getObjectId());
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {

                                ParseUser user = objects.get(0);
                                ParseGeoPoint geoPoint = user.getParseGeoPoint("currentLocation");
                                BitmapDescriptor icon = null;


                                if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {

                                    icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), getActivity().getResources().getIdentifier("user", "drawable", "com.brandonlassiter.traceroute")));

                                } else if(participant.getString("color").equals(mColorSelected)) {

                                    icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), getActivity().getResources().getIdentifier(participant.getString("color"), "drawable", "com.brandonlassiter.traceroute")));

                                }

                                if(icon != null) {

                                    markers.add(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                                            .icon(icon));

                                    for (MarkerOptions marker : markers) {
                                        map.addMarker(marker);
                                    }

                                }

                            }
                        }
                    });

                }
            });



        }

    }
}
