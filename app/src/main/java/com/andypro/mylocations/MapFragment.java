package com.andypro.mylocations;

import android.content.Context;
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

    public interface MapCallbacks {
        void onMapPositionIdle(CameraPosition pos);
    }

    MapCallbacks mListener;
    private GoogleMap gMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (MapCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLocationSelectedListener");
        }
    }

    /*
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        MAP_TYPE_NORMAL: Basic map.
        MAP_TYPE_SATELLITE: Satellite imagery.
        MAP_TYPE_HYBRID: Satellite imagery with roads and labels.
        MAP_TYPE_TERRAIN: Topographic data.
        MAP_TYPE_NONE: No base map tiles.
    */

//    private Location location;
//    MapCallbacks mListener;


    public static MapFragment newInstance(Location location) {
        Log.d(Constants.LOG_TAG, "MapFragment: new instance");
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

    public void setMapLocation(Location location) {
        if (!checkReady()) {
            return;
        }
        // Log.d(Constants.LOG_TAG, "Camera factory for: " + location.name);
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
        Location location = getArguments().getParcelable("location");
        LatLng pos = new LatLng(location.lat, location.lng);
        // gMap.addMarker(new MarkerOptions().position(pos).title(location.name));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, location.zoom));
        gMap.setOnCameraIdleListener(this);
    }

    @Override
    public void onCameraIdle() {
        CameraPosition pos = gMap.getCameraPosition();
//        ((MapCallbacks) getActivity()).onMapPositionIdle(pos);
        mListener.onMapPositionIdle(pos);
//        mCameraTextView.setText(mMap.getCameraPosition().toString());
    }

}
