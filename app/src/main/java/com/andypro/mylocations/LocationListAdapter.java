package com.andypro.mylocations;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.SimpleCursorTreeAdapter;

import com.andypro.mylocations.provider.LocationProvider;
import com.andypro.mylocations.utils.Constants;

/**
 * Created by Andy on 01.12.2017.
 */

public class LocationListAdapter extends SimpleCursorTreeAdapter {

    private MainActivity mActivity;
//    private LayoutInflater mInflater;

    public LocationListAdapter(Cursor cursor, Context context,
                               String[] groupFrom, int[] groupTo,
                               String[] childFrom, int[] childTo) {
        super(context, cursor,
                R.layout.group_list_item, groupFrom, groupTo,
                R.layout.child_list_item, childFrom, childTo);
        mActivity = (MainActivity) context;
//        mInflater = LayoutInflater.from(context);
    }

    protected Cursor getChildrenCursor(Cursor groupCursor) {

        Cursor itemCursor = getGroup(groupCursor.getPosition());

        CursorLoader cursorLoader = new CursorLoader(mActivity,
                LocationProvider.LOCATION_URI,
                Constants.LOCATION_PROJECTION,
                Constants.LOCATION_CATEGORY,
                new String[] { itemCursor.getString(itemCursor
                        .getColumnIndex(Constants.COMMON_ID)) },
                Constants.DEFAULT_SORT_ORDER);

/*
        CursorLoader cursorLoader = new CursorLoader(mActivity,
                LocationProvider.LOCATION_URI,
                Constants.LOCATION_PROJECTION,
                null,null,
                Constants.DEFAULT_SORT_ORDER);
*/

        Cursor childCursor = null;

        try {
            childCursor = cursorLoader.loadInBackground();
            Log.d(Constants.LOG_TAG, "childCursor, count of locations: " + childCursor.getCount());
            childCursor.moveToFirst();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
        }

        return childCursor;
    }

            /*
        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
        */

}
