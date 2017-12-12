package com.andypro.mylocations.provider;

//import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.andypro.mylocations.R;
import com.andypro.mylocations.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocationDBHelper extends SQLiteOpenHelper {

    Context context;

    public static final String DATABASE_NAME = "locations.db";
    public static final int DATABASE_VERSION = 1;

    public LocationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String categoryQuery = "create table " + Constants.CATEGORY_TABLE + "("
                + Constants.COMMON_ID + " integer primary key, "
                + Constants.COMMON_NAME + " text" + ");";

        String locationQuery = "create table " + Constants.LOCATION_TABLE + "("
                + Constants.COMMON_ID + " integer primary key autoincrement, "
                + Constants.COMMON_NAME + " text, "
                + Constants.LOCATION_ADDRESS + " text, "
                + Constants.LOCATION_LAT + " real, "
                + Constants.LOCATION_LNG + " real, "
                + Constants.LOCATION_ZOOM + " real, "
                + Constants.LOCATION_CATEGORY + " integer,"
                + " FOREIGN KEY (" + Constants.LOCATION_CATEGORY + ") REFERENCES "
                + Constants.CATEGORY_TABLE + "(" + Constants.COMMON_ID
                + ") ON DELETE CASCADE);";

        db.execSQL(categoryQuery);
        db.execSQL(locationQuery);

        populateInitialData(db);

    }

    // https://stackoverflow.com/questions/22791217/should-i-enable-foreign-key-constraint-in-onopen-or-onconfigure
    // @SuppressLint("NewApi")
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
//            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL("PRAGMA foreign_keys=1");
//            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + Constants.CATEGORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATION_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.LOG_TAG, "Downgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Constants.CATEGORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATION_TABLE);
        onCreate(db);
    }

    protected void populateInitialData(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        String data = loadRaw(R.raw.data);

        try {
            JSONObject main = new JSONObject(data);
            JSONArray items = main.getJSONArray("categories");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                cv.put(Constants.COMMON_ID, item.getInt("id"));
                cv.put(Constants.COMMON_NAME, item.getString("name"));
                long idx = db.insert(Constants.CATEGORY_TABLE, null, cv);
                Log.d(Constants.LOG_TAG,"Category insert: " + idx);
            }

            cv.clear();
            items = main.getJSONArray("locations");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                cv.put(Constants.COMMON_NAME, item.getString("name"));
                cv.put(Constants.LOCATION_ADDRESS, item.getString("address"));
                cv.put(Constants.LOCATION_CATEGORY, item.getInt("category"));
                cv.put(Constants.LOCATION_LAT, item.getDouble("lat"));
                cv.put(Constants.LOCATION_LNG, item.getDouble("lng"));
                cv.put(Constants.LOCATION_ZOOM, item.getDouble("zoom"));
                long ldx = db.insert(Constants.LOCATION_TABLE, null, cv);
                Log.d(Constants.LOG_TAG, "Location insert: " + ldx);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected String loadRaw(int id) {
        InputStream inputStream = context.getResources().openRawResource(id);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return byteArrayOutputStream.toString();
    }

}
