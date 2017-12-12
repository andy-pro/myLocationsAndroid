package com.andypro.mylocations;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.model.CameraPosition;

public class MapActivity extends FragmentActivity implements
        MapFragment.MapCallbacks {

//    CameraPosition pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE
                && isLarge()) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            /*
            Intent intent = getIntent();
            MapFragment map = MapFragment.newInstance(
                    intent.getStringExtra("name"),
                    intent.getDoubleExtra("lat", 51.510452),
                    intent.getDoubleExtra("lng", -0.127716),
                    intent.getFloatExtra("zoom", 10),
                    intent.getLongExtra("id", 0));
            */

            Intent intent = getIntent();
            Location location = intent.getParcelableExtra("location");
//            MapFragment map = MapFragment.newInstance(getIntent().getParcelableExtra("location"));
            MapFragment map = MapFragment.newInstance(location);

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, map).commit();
        }
    }

    boolean isLarge() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onMapPositionIdle(CameraPosition pos) {
//        pos = _pos;
        Intent intent = new Intent();
//        intent.putExtra("alignment", Gravity.LEFT);
        intent.putExtra("camera_position", pos);
        setResult(RESULT_OK, intent);
    }

}