package com.andypro.mylocations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
//import android.widget.TextView;

import com.andypro.mylocations.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//import android.content.Intent;


public class MapFragment extends Fragment implements
        OnMapReadyCallback, OnCameraIdleListener {

    private GoogleMap gMap;
//    private Location location;
//    MapCallbacks mListener;

    public interface MapCallbacks {
        void onMapPositionIdle(CameraPosition pos);
    }

    public static MapFragment newInstance(Location location) {
        Log.d(Constants.LOG_TAG, "Map fragment new instance");
        MapFragment map = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable("location", location);
        map.setArguments(args);
        return map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);

        // Obtain the SupportMapFragment and get notified when the map_fragment is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        mapFragment.getMapAsync(this);

        return v;
    }

    private boolean checkReady() {
        if (gMap == null) {
            Toast.makeText(getActivity(), R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

//    long getLocationId() {
////        return getArguments().getLong("id");
//        return location._id;
//    }

    public void setMapLocation(Location location) {
        if (!checkReady()) {
            return;
        }

        Log.d(Constants.LOG_TAG, "Camera factory for: " + location.name);
        CameraPosition pos =
                new CameraPosition.Builder().target(new LatLng(location.lat, location.lng))
                        .zoom(location.zoom)
                        .bearing(0)
                        .tilt(25)
                        .build();


        CameraUpdate update = CameraUpdateFactory.newCameraPosition(pos);

        gMap.animateCamera(update, 100, null);

    }



    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        /*
        name = getArguments().getString("name");
        lat = getArguments().getDouble("lat");
        lng = getArguments().getDouble("lng");
        zoom = getArguments().getFloat("zoom");

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
        LatLng location = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(location).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        */
        Location location = getArguments().getParcelable("location");
        String name = location.name;
        LatLng pos = new LatLng(location.lat, location.lng);
        gMap.addMarker(new MarkerOptions().position(pos).title(name));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, location.zoom));
        gMap.setOnCameraIdleListener(this);
    }

    @Override
    public void onCameraIdle() {
        CameraPosition pos = gMap.getCameraPosition();
        ((MapCallbacks) getActivity()).onMapPositionIdle(pos);
//        mCameraTextView.setText(mMap.getCameraPosition().toString());
    }

}
