package com.andypro.mylocations.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.andypro.mylocations.utils.Constants;

public class LocationProvider extends ContentProvider {

    private static final int CATEGORY = 1;
    private static final int CATEGORY_ID = 2;
    private static final int LOCATION = 3;
    private static final int LOCATION_ID = 4;

    public static final Uri CATEGORY_URI = Uri.parse("content://"
            + Constants.AUTHORITY + "/" + Constants.CATEGORY_TABLE);

    public static final Uri LOCATION_URI = Uri.parse("content://"
            + Constants.AUTHORITY + "/" + Constants.LOCATION_TABLE);

    // The MIME type of a directory of events
    private static final String CATEGORY_CONTENT_TYPE = "vnd.android.cursor.dir/category";
    private static final String LOCATION_CONTENT_TYPE = "vnd.android.cursor.dir/location";

    // The MIME type of a single event
    private static final String CATEGORY_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/category";
    private static final String LOCATION_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/location";

    private UriMatcher uriMatcher;
    private LocationDBHelper dbHelper;

    @Override
    public boolean onCreate() {

        Log.d(Constants.LOG_TAG, "onCreate LocationProvider");

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Constants.AUTHORITY, Constants.CATEGORY_TABLE, CATEGORY);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.CATEGORY_TABLE + "#", CATEGORY_ID);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.LOCATION_TABLE, LOCATION);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.LOCATION_TABLE + "#", LOCATION_ID);

        dbHelper = new LocationDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String orderBy) {

        String args = "";
        if (selectionArgs != null) {
            args = selectionArgs[0].toString();
        }
        Log.d(Constants.LOG_TAG, "query, " + uri.toString() + ":" + selection + ":" + args);

        dbHelper.getWritableDatabase();
        String database;

        switch (uriMatcher.match(uri)) {
            case CATEGORY:
                database = Constants.CATEGORY_TABLE;
                break;
            case LOCATION:
                database = Constants.LOCATION_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (selection != null) {
            selection = selection + "=?";
        }

        Cursor cursor = db.query(database, projection, selection,
                selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        Log.d(Constants.LOG_TAG, "getType, " + uri.toString());

        switch (uriMatcher.match(uri)) {
            case CATEGORY:
                return CATEGORY_CONTENT_TYPE;
            case CATEGORY_ID:
                return CATEGORY_CONTENT_ITEM_TYPE;
            case LOCATION:
                return LOCATION_CONTENT_TYPE;
            case LOCATION_ID:
                return LOCATION_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Uri newUri;
        long id;

        switch (uriMatcher.match(uri)) {
            case CATEGORY:
                // Insert into database
                id = db.insertOrThrow(Constants.CATEGORY_TABLE, null, values);

                // Notify any watchers of the change
                newUri = ContentUris.withAppendedId(CATEGORY_URI, id);
                break;
            case LOCATION:
                // Insert into database
                id = db.insertOrThrow(Constants.LOCATION_TABLE, null, values);

                // Notify any watchers of the change
                newUri = ContentUris.withAppendedId(LOCATION_URI, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affected;

        if (selection != null) {
            selection = selection + "=?";
        }

        switch (uriMatcher.match(uri)) {
            case CATEGORY:
                affected = db.delete(Constants.CATEGORY_TABLE, selection,
                        selectionArgs);
                break;
            case LOCATION:
                affected = db.delete(Constants.LOCATION_TABLE, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int affected;

		/*
		 * Choose the table to query and a sort order based on the code returned
		 * for the incoming URI.
		 */
        switch (uriMatcher.match(uri)) {
            case CATEGORY:
                affected = db.update(Constants.CATEGORY_TABLE, values,
                        Constants.COMMON_ID + "=?", selectionArgs);
                break;
            case LOCATION:
                affected = db.update(Constants.LOCATION_TABLE, values,
                        Constants.COMMON_ID + "=?", selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

}