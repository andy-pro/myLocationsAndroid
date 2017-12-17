package com.andypro.mylocations;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
//import android.support.v4.app.LoaderManager.LoaderCallbacks;

//import android.content.CursorLoader;
//import android.content.Loader;
//import android.app.LoaderManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import java.util.ArrayList;

import com.andypro.mylocations.provider.LocationProvider;
import com.andypro.mylocations.utils.Constants;

import android.util.Log;

public class ListViewFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /* Location cursor loader id */
    private static final int LIST_LOADER_ID = 1;

//    OnLocationSelectedListener mListener;
    ListViewCallbacks mListener;
//    ActionMode mActionMode;
//    Cursor currentCursor;
//    int groupPosition;
    private ExpandableListView elv;
//    boolean locationMode;
//    private static final int CM_DELETE_ID = 1;
//    private LocationListAdapter adapter;
    LocationListAdapter adapter;
//    DB db;

    public interface ListViewCallbacks {
        void onLocationSelected(Cursor cursor);
        void onListItemLongClick(boolean locationMode, Cursor cursor);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        log("ListViewFragment = onAttach");
        try {
            mListener = (ListViewCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLocationSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        log("ListViewFragment = onCreateView");
        View v = inflater.inflate(R.layout.expandable_list_view, null) ;
        elv = v.findViewById(R.id.elvMain);
        elv.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        log("ListViewFragment = onActivityCreated");

        String[] groupFrom = { Constants.COMMON_NAME };
        int[] groupTo = { R.id.categoryName };

        String[] childFrom = {
                Constants.COMMON_NAME, Constants.LOCATION_ADDRESS,
                Constants.LOCATION_LAT, Constants.LOCATION_LNG
        };
        int[] childTo = {
                R.id.tvName, R.id.tvAddress,
                R.id.tvLat, R.id.tvLng
        };
        adapter = new LocationListAdapter(null, getActivity(),
                groupFrom, groupTo, childFrom, childTo);
        elv.setAdapter(adapter);

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int group_pos, int child_pos, long id) {

                Cursor cursor = adapter.getChild(group_pos, child_pos);
                mListener.onLocationSelected(cursor);
                int index = parent.getFlatListPosition(ExpandableListView
                        .getPackedPositionForChild(group_pos, child_pos));
                parent.setItemChecked(index, true);
                return false;
            }
        });

        elv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id) {

                MainActivity main = (MainActivity) getActivity();
                if (main.mActionMode != null) {
                    return false;
                }

                long packedPosition = elv.getExpandableListPosition(position);
                boolean locationMode = ExpandableListView.getPackedPositionType(packedPosition)
                        == ExpandableListView.PACKED_POSITION_TYPE_CHILD;
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

                Cursor cursor = locationMode ? adapter.getChild(groupPosition, childPosition)
                            : adapter.getGroup(groupPosition);
                mListener.onListItemLongClick(locationMode, cursor);

                view.setSelected(true);
                return true;
            }
        });

        LoaderManager lm = getActivity().getSupportLoaderManager();
        Loader<Cursor> loader = lm.getLoader(LIST_LOADER_ID);
        log("ListViewFragment = onActivityCreated = getLoader: " + loader);
        if (loader != null && !loader.isReset()) {
            log("restart Loader!");
            lm.restartLoader(LIST_LOADER_ID, null, this);
        } else {
            log("init Loader!");
            lm.initLoader(LIST_LOADER_ID, null, this);
        }

//            lm.initLoader(LOADER_ID, null, this);

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        CursorLoader cl = new CursorLoader(getActivity(),
                LocationProvider.CATEGORY_URI, Constants.CATEGORY_PROJECTION, null,
                null, Constants.DEFAULT_SORT_ORDER);
        log("LoaderManager: onCreateLoader id=" + id + "CursorLoader=" + cl);
        return cl;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.
        int id = loader.getId();
        log("LoaderManager: onLoadFinished, id: " + id);
        if (id == LIST_LOADER_ID) {
            adapter.setGroupCursor(cursor);
            expandAllChild();
        }
    }

    private void expandAllChild() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            elv.expandGroup(i);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // is about to be closed.
        int id = loader.getId();
        log("LoaderManager: onLoaderReset, id: " + id);
        if (id != -1) {
//        if (id != LOADER_ID) {
            // child cursor
            try {
                adapter.setChildrenCursor(id, null);
            } catch (NullPointerException e) {
                Log.w("TAG", "Adapter expired, try again on the next query: "
                        + e.getMessage());
            }
        } else {
            adapter.setGroupCursor(null);
        }
    }

    public int getCategoryCount() {
        return adapter.getGroupCount();
    }

    public ArrayList<ArrayList<String>> getCategoryNames() {

        Cursor cursor = adapter.getCursor();
        ArrayList<ArrayList<String>> categories = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>(), names = new ArrayList<>();

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(Constants.COMMON_ID));
                    ids.add(id);
                    String name = cursor.getString(cursor.getColumnIndex(Constants.COMMON_NAME));
                    names.add(name);
                } while (cursor.moveToNext());
                categories.add(ids);
                categories.add(names);
            }
//            cursor.close();
        }
        return categories;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        log("ListViewFragment = onDetach");
    }

//    public void onDestroy() {
//        super.onDestroy();
////        Log.d(Constants.LOG_TAG, "ListViewFragment onDestroy");
//        db.close();
//    }

    private void log(String msg) {
        Log.d(Constants.LOG_TAG, msg);
    }

}
