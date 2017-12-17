package com.andypro.mylocations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.widget.AdapterView;

import java.util.HashMap;
import java.util.ArrayList;
import android.database.Cursor;
import android.widget.Toast;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.andypro.mylocations.provider.LocationProvider;
import com.andypro.mylocations.utils.Constants;

public class MainActivity extends AppCompatActivity implements
        ListViewFragment.ListViewCallbacks,
//        ListDialogFragment.ListDialogCallbacks,
        MapFragment.MapCallbacks {

    final int REQUEST_CODE_MAP = 1;
    final int DIALOG_DELETE = 1;
//    String name = "London";
//    double lat = 51.510452;
//    double lng = -0.127716;
//    float zoom = 10F;
//    long locationId = -1;

    boolean withMap = true;
//    CameraPosition cameraPosition = null;
    Location currentMapPosition;
    boolean locationMode;
    int currentCmd;
    HashMap currentEntry;

    ActionMode mActionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentMapPosition = savedInstanceState.getParcelable("map_position");
            locationMode = savedInstanceState.getBoolean("location_mode");
            currentCmd = savedInstanceState.getInt("current_cmd");
            currentEntry = (HashMap) savedInstanceState.getSerializable("current_entry");
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                currentCmd = R.id.menu_add;
                int count = getListViewFragment().getCategoryCount();
                if (count > 0) {
                    currentEntry = getEntryFromMapPosition();
                    locationMode = true;
                } else {
                    currentEntry = new Category().toHashMap();
                    locationMode = false;
                }
                showListViewDialog();
            }
        });

        currentMapPosition = savedInstanceState == null ? new Location() :
                (Location) savedInstanceState.getParcelable("map_position");

        withMap = findViewById(R.id.map_frame) != null;
        if (withMap)
            showMap(currentMapPosition);

        log("Start app");
    }

    /* ListView click */
    @Override
    public void onLocationSelected(Cursor cursor) {
        showMap(new Location(cursor));
    }

    void showMap(Location location) {
        if (withMap) {
            MapFragment map = (MapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_frame);
            if (map == null) {
                map = MapFragment.newInstance(location);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map_frame, map).commit();
            } else {
                map.setMapLocation(location);
            }

        } else {
//            Intent intent = new Intent(this, MapActivity.class);
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            intent.putExtra("location", location);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        }
    }

    /* ListView long click */
    @Override
    public void onListItemLongClick(boolean mode, Cursor cursor) {
        locationMode = mode;
        currentEntry = mode ?  new Location(cursor).toHashMap() : new Category(cursor).toHashMap();

//        String name = cursor.getString(cursor.getColumnIndex(Constants.COMMON_NAME));
        String name = currentEntry.get(Constants.COMMON_NAME).toString();
//        mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        mActionMode = startSupportActionMode(mActionModeCallback);
        mActionMode.setTitle(name);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            mode.getMenuInflater().inflate(R.menu.context, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.findItem(R.id.menu_add)
                    .setTitle(locationMode ? R.string.new_location : R.string.new_category);
            menu.findItem(R.id.menu_new_location).setVisible(!locationMode);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            Category category;
//            Location location;
//            long categoryId = -1;
            currentCmd = item.getItemId();
//            boolean menu_add = currentCmd == R.id.menu_add;

/*
            if (currentCmd == R.id.menu_new_location) {
                locationMode = true;
                currentCmd = R.id.menu_add;
                currentEntry = new Location(true, getEntryId()).toHashMap();
            }
*/
            if (currentCmd == R.id.menu_new_location) {
                /* new location for current category,
                   performed in categoryMode */
                currentCmd = R.id.menu_add;
                currentEntry = getEntryFromMapPosition();
                locationMode = true;
            }
            else if (locationMode && currentCmd == R.id.menu_add) {
                /* new location in locationMode */
                String address = currentEntry.get(Constants.LOCATION_ADDRESS).toString();
                currentEntry = getEntryFromMapPosition();
                currentEntry.put(Constants.LOCATION_ADDRESS, address);
                /*
                currentLocation.name = currentEntry.get("name").toString();
                currentLocation.address = currentEntry.get("address").toString();
                currentLocation.category = getEntryId();
                currentEntry = currentLocation.toHashMap();
                */

            }
            /*
            else if (locationMode && currentCmd == R.id.menu_edit) {
                currentEntry.put(Constants.LOCATION_LAT, currentMapPosition.lat);
                currentEntry.put(Constants.LOCATION_LNG, currentMapPosition.lng);
                currentEntry.put(Constants.LOCATION_ZOOM, currentMapPosition.zoom);
            }
            */

//            if (currentCmd == R.id.menu_add) {
//                currentEntry = locationMode ? new Location(true, categoryId).toHashMap()
//                        : new Category().toHashMap();
//            }
            else if (!locationMode && currentCmd == R.id.menu_add) {
//                currentEntry = new Category().toHashMap();
                currentEntry.put(Constants.COMMON_NAME, "");
            }

            showListViewDialog();
            mode.finish();
            return true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    /* ListView Dialog */
    private void showListViewDialog() {

//        ListViewFragment list = (ListViewFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.main_list_fragment);
//        categoryList = list.getCategoryNames();

//        log("GET group position: " +  list.groupPosition);

        if (currentCmd == R.id.menu_delete){
            /* DELETE mode */
            Log.d(Constants.LOG_TAG, "delete dialog on show: entry:" + currentEntry);
            showDialog(DIALOG_DELETE);
        } else {
            /* ADD, EDIT modes */
            /*
            categoryList = getListViewFragment().getCategoryNames();
            FragmentManager fm = getSupportFragmentManager();
            Log.d(Constants.LOG_TAG, "list dialog on show: entry:" + currentEntry.get("name") + categoryList);
            ListDialogFragment dlg = ListDialogFragment.newInstance();
//            ListDialogFragment dlg = ListDialogFragment.newInstance(
//                    locationMode, currentCmd, currentEntry, categoryList);
            dlg.show(fm, "edit_entry_dialog");
            */
            ListDialogFragment dialog = new ListDialogFragment();
            dialog.show(getSupportFragmentManager(), "ListDialogFragment");
        }
    }

    public void onOkListViewDialog(ContentValues cv) {
        int count;
        String[] args = { currentEntry.get(Constants.COMMON_ID).toString() };
        Uri uri = locationMode ? LocationProvider.LOCATION_URI : LocationProvider.CATEGORY_URI;

        Log.d(Constants.LOG_TAG, "db content from fragment: " + cv);

        switch (currentCmd) {
            case R.id.menu_add:
                Uri res = getContentResolver().insert(uri, cv);
                Log.d(Constants.LOG_TAG, "insert, result Uri : " + res.toString());
                break;
            case R.id.menu_edit:
                count = getContentResolver().update(uri, cv, Constants.COMMON_ID, args);
                Log.d(Constants.LOG_TAG, "inserted count: " + count);
                break;
            case R.id.menu_delete:
                count = getContentResolver().delete(uri, Constants.COMMON_ID, args);
                Log.d(Constants.LOG_TAG, "deleted count: " + count);
                break;
        }

        /*
        String name = cv.getAsString("name");
        Toast.makeText(this, "Entry name \"" +
                name + "\", db operation: OK", Toast.LENGTH_SHORT).show();
        */

    }

    /* create DeleteDialog */
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DELETE) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("---")
                    .setMessage(R.string.shure)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setPositiveButton(R.string.ok, deleteDialogListener)
                    .setNegativeButton(R.string.cancel, deleteDialogListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        Log.d(Constants.LOG_TAG, "onPrepare Delete Dialog");
        if (id == DIALOG_DELETE) {
//            dialog.setTitle(String.valueOf(R.string.del_location) + " \"" + entryName + "\"");
            dialog.setTitle(getString(locationMode ?
                    R.string.del_location : R.string.del_category)
                    + " \"" + currentEntry.get("name") + "\"");
        }
    }

    OnClickListener deleteDialogListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    onOkListViewDialog(null);
                    break;
                case Dialog.BUTTON_NEGATIVE:
//                    finish();
                    Log.d(Constants.LOG_TAG, "map fragment: no");
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // запишем в лог значения requestCode и resultCode
        Log.d(Constants.LOG_TAG, "requestCode = " + requestCode + ", resultCode = " + resultCode);
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_MAP:
//                    int color = data.getIntExtra("color", Color.WHITE);
                    CameraPosition pos = data.getParcelableExtra("camera_position");
                    onMapPositionIdle(pos);
//                    log("cam pos = " + pos);
                    break;
            }
            // если вернулось не ОК
        } else {
            Toast.makeText(this, "Wrong map result", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapPositionIdle(CameraPosition pos) {
        LatLng target = pos.target;
//        currentLocation.name = "";
//        currentLocation.address = "";
        currentMapPosition.lat = target.latitude;
        currentMapPosition.lng = target.longitude;
        currentMapPosition.zoom = pos.zoom;
        log("MainActivity onMapPositionIdle, set new pos: " + target);
    }

    public HashMap getEntryFromMapPosition() {
        HashMap entry = currentMapPosition.toHashMap();
        entry.put(Constants.LOCATION_CATEGORY, getEntryId());
        return entry;
    }

    public int getEntryId() {
        log("CurrentEntry" + currentEntry);
        if (currentEntry == null) {
            return -1;
        }
        return Integer.parseInt(currentEntry.get(
                locationMode ? Constants.LOCATION_CATEGORY : Constants.COMMON_ID
        ).toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* class variables
        Location currentMapPosition;
        boolean locationMode;
        int currentCmd;
        HashMap currentEntry;
        ActionMode mActionMode;
        */
        super.onSaveInstanceState(outState);
        outState.putParcelable("map_position", currentMapPosition);
        outState.putBoolean("location_mode", locationMode);
        outState.putInt("current_cmd", currentCmd);
        outState.putSerializable("current_entry", currentEntry);
//        outState.putParcelable("", );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ListViewFragment getListViewFragment() {
        return (ListViewFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_list_fragment);
    }

    private void log(String msg) {
        Log.d(Constants.LOG_TAG, msg);
    }

}