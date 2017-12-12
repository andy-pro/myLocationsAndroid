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

import com.andypro.mylocations.provider.LocationProvider;
import com.andypro.mylocations.utils.Constants;

public class MainActivity extends AppCompatActivity implements
        ListViewFragment.ListViewCallbacks,
        ListDialogFragment.ListDialogCallbacks,
        MapFragment.MapCallbacks {

//    String name = "London";
//    double lat = 51.510452;
//    double lng = -0.127716;
//    float zoom = 10F;
//    long locationId = -1;
    boolean withMap = true;
    Location location;

    final int REQUEST_CODE_MAP = 1;
    final int DIALOG_DELETE = 1;

    boolean locationMode;
    int currentCmd;
    HashMap currentEntry;

    ActionMode mActionMode;
    ArrayList<ArrayList<String>> categoryList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                locationMode = true;
                currentCmd = R.id.menu_add;
                currentEntry = new Location(true, getEntryId()).toHashMap();
                showListViewDialog();
            }
        });

        Location location = (savedInstanceState == null) ? new Location(false, -1) :
                (Location) savedInstanceState.getParcelable("location");

//        Location location = new Location(false, -1);

        withMap = findViewById(R.id.map_frame) != null;
        if (withMap)
            showMap(location);

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
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("location", location);
//            startActivity(intent);
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

            if (currentCmd == R.id.menu_new_location) {
                locationMode = true;
                currentCmd = R.id.menu_add;
//                categoryId = getEntryId();
                currentEntry = new Location(true, getEntryId()).toHashMap();
            }
//            else if (locationMode && currentCmd == R.id.menu_add) {
//                categoryId = getEntryValue(Constants.LOCATION_CATEGORY);
//            }

//            if (currentCmd == R.id.menu_add) {
//                currentEntry = locationMode ? new Location(true, categoryId).toHashMap()
//                        : new Category().toHashMap();
//            }
            else
            if (!locationMode && currentCmd == R.id.menu_add) {
                currentEntry = new Category().toHashMap();
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

        ListViewFragment list = (ListViewFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_list_fragment);
        categoryList = list.getCategoryNames();

//        log("GET group position: " +  list.groupPosition);

        if (currentCmd == R.id.menu_delete){
            /* DELETE mode */
            Log.d(Constants.LOG_TAG, "delete dialog on show: entry:" + currentEntry);
            showDialog(DIALOG_DELETE);
        } else {
            /* ADD, EDIT modes */
            FragmentManager fm = getSupportFragmentManager();
            Log.d(Constants.LOG_TAG, "list dialog on show: entry:" + currentEntry.get("name"));
            ListDialogFragment dlg = ListDialogFragment.newInstance(
                    locationMode, currentCmd, currentEntry, categoryList);
            dlg.show(fm, "edit_entry_dialog");
        }
    }

    public void onOkListViewDialog(View view) {
        int count;
        ContentValues cv = null;

//        Long currentId = currentCmd == Constants.MENU_ADD ? null :
//                Long.parseLong(currentEntry.get(Constants.COMMON_ID).toString());
//        String currentId = currentEntry.get(Constants.COMMON_ID).toString();
        String[] args = { currentEntry.get(Constants.COMMON_ID).toString() };

        Uri uri = locationMode ? LocationProvider.LOCATION_URI : LocationProvider.CATEGORY_URI;

        if (currentCmd != R.id.menu_delete) {
            // for "insert" command currentId=null, so ContentValues do not contains "_id"
//            cv = locationMode ? new Location(view).toContentValues(currentId) :
            cv = locationMode ? Location.getContentValues(view, categoryList) :
                    Category.getContentValues(view);
        }

        categoryList = null;

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
                    .setIcon(android.R.drawable.ic_dialog_info)
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
                    log("cam pos = " + pos);
                    break;
            }
            // если вернулось не ОК
        } else {
            Toast.makeText(this, "Wrong map result", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapPositionIdle(CameraPosition pos) {

    }

//    int getEntryValue(String key) {
    int getEntryId() {
        if (currentEntry == null) {
            return -1;
        }
        return Integer.parseInt(currentEntry.get(Constants.COMMON_ID).toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("name", name);
//        outState.putDouble("lat", lat);
//        outState.putDouble("lng", lng);
//        outState.putFloat("zoom", zoom);
//        outState.putLong("id", locationId);

        outState.putParcelable("location", location);
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

    private void log(String msg) {
        Log.d(Constants.LOG_TAG, msg);
    }

}