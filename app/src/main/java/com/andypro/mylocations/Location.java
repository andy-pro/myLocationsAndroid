package com.andypro.mylocations;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
//import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.andypro.mylocations.utils.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Location implements Parcelable {

    String name, address;
    double lat, lng;
    float zoom;
    long _id, category;

    /*
    public Location(boolean empty, long categoryId) {
        if (empty) {
            name = "";
            address = "";
            lat = 0;
            lng = 0;
            category = categoryId;
        } else {
//            name = "London";
//            address = "United Kingdom";
            lat = 51.510452;
            lng = -0.127716;
            category = -1;
        }
        zoom = 10F;
        _id = -1;
    }
    */

    public Location() {
        name = "";
        address = "";
//        category = categoryId;
        category = -1;
        lat = 51.510452;
        lng = -0.12771;
        zoom = 10F;
        _id = -1;
    }

    /*
    public Location(String name, String address, double lat, double lng, float zoom, long id) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.zoom = zoom;
        this.id = id;
    }

    // create Location from View
    public Location(View view) {
        name = ((TextView) view.findViewById(R.id.tvName)).getText().toString();
        address = ((TextView) view.findViewById(R.id.tvAddress)).getText().toString();
        lat = Double.parseDouble(((TextView) view.findViewById(R.id.tvLat)).getText().toString());
        lng = Double.parseDouble(((TextView) view.findViewById(R.id.tvLng)).getText().toString());
        zoom = Float.parseFloat(((TextView) view.findViewById(R.id.tvZoom)).getText().toString());
        id = Long.parseLong(((TextView) view.findViewById(R.id.tvId)).getText().toString());
    }

    // create Location from Dialog View
    public Location(View view) {
        name = ((EditText) view.findViewById(R.id.etName)).getText().toString();
        address = ((EditText) view.findViewById(R.id.etAddress)).getText().toString();
        lat = Double.parseDouble(((EditText) view.findViewById(R.id.etLat)).getText().toString());
        lng = Double.parseDouble(((EditText) view.findViewById(R.id.etLng)).getText().toString());
        zoom = Float.parseFloat(((EditText) view.findViewById(R.id.etZoom)).getText().toString());
//        id = Long.parseLong(((TextView) view.findViewById(R.id.tvId)).getText().toString());
    }
    */

    // create Location from Database Cursor
    public Location(Cursor crs) {
        name = crs.getString(crs.getColumnIndex(Constants.COMMON_NAME));
        address = crs.getString(crs.getColumnIndex(Constants.LOCATION_ADDRESS));
        lat = crs.getDouble(crs.getColumnIndex(Constants.LOCATION_LAT));
        lng = crs.getDouble(crs.getColumnIndex(Constants.LOCATION_LNG));
        zoom = crs.getFloat(crs.getColumnIndex(Constants.LOCATION_ZOOM));
        category = crs.getLong(crs.getColumnIndex(Constants.LOCATION_CATEGORY));
        _id = crs.getLong(crs.getColumnIndex(Constants.COMMON_ID));
    }

//    public String getType() { return this.type; }
//    public int getModel() { return this.model; }
//    public String getColor() { return this.color; }

    // parcel part
    public Location(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        zoom = in.readFloat();
        _id = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeFloat(zoom);
        dest.writeLong(_id);
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {

        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);  //using parcelable constructor
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public HashMap toHashMap() {
        HashMap entry = new HashMap();
        entry.put(Constants.COMMON_NAME, name);
        entry.put(Constants.LOCATION_ADDRESS, address);
        entry.put(Constants.LOCATION_LAT, lat);
        entry.put(Constants.LOCATION_LNG, lng);
        entry.put(Constants.LOCATION_ZOOM, zoom);
        entry.put(Constants.LOCATION_CATEGORY, category);
        entry.put(Constants.COMMON_ID, _id);
//        Log.d(Constants.LOG_TAG, "hash:"+entry.get("name")+":"+entry.get("lat")+":"+entry.get("lng"));
        return entry;
    }

    /*
    public ContentValues toContentValues(Long id) {
        ContentValues entry = new ContentValues();
        if (id != null) {
            entry.put(Constants.COMMON_ID, id);
        }
        entry.put(Constants.COMMON_NAME, name);
        entry.put(Constants.LOCATION_ADDRESS, address);
        entry.put(Constants.LOCATION_LAT, lat);
        entry.put(Constants.LOCATION_LNG, lng);
        entry.put(Constants.LOCATION_ZOOM, zoom);
//        entry.put(Constants.LOCATION_CATEGORY, category);
        entry.put(Constants.LOCATION_CATEGORY, 2);
        return entry;
    }
    */

    public static ContentValues getContentValues(View view, ArrayList<String> categories_ids) {

        String name = ((EditText) view.findViewById(R.id.etName)).getText().toString();
        String address = ((EditText) view.findViewById(R.id.etAddress)).getText().toString();
        Double lat = Double.parseDouble(((EditText) view.findViewById(R.id.etLat)).getText().toString());
        Double lng = Double.parseDouble(((EditText) view.findViewById(R.id.etLng)).getText().toString());
        Float zoom = Float.parseFloat(((EditText) view.findViewById(R.id.etZoom)).getText().toString());

        int pos = ((Spinner) view.findViewById(R.id.spinner)).getSelectedItemPosition();
        long categoryId = Long.parseLong(categories_ids.get(pos));
//        id = Long.parseLong(((TextView) view.findViewById(R.id.tvId)).getText().toString());

        ContentValues entry = new ContentValues();
//        if (id != null) {
//            entry.put(Constants.COMMON_ID, id);
//        }
        entry.put(Constants.COMMON_NAME, name);
        entry.put(Constants.LOCATION_ADDRESS, address);
        entry.put(Constants.LOCATION_LAT, lat);
        entry.put(Constants.LOCATION_LNG, lng);
        entry.put(Constants.LOCATION_ZOOM, zoom);
        entry.put(Constants.LOCATION_CATEGORY, categoryId);
        return entry;

    }

    public static String format(HashMap entry, String key) {
        /*
        String fs = key == Constants.LOCATION_ZOOM ? "%.2f" : "%.5f";
        return String.format(fs, Float.parseFloat(entry.get(key).toString()));
        String fs = key == Constants.LOCATION_ZOOM ? "#0.00" : "#0.00000";
        return new DecimalFormat(fs).format(Float.parseFloat(entry.get(key).toString()));
        */
        String src = entry.get(key).toString();
        int pos = src.indexOf(".");
        if (pos == -1) {
            return src;
        } else {
            int len = src.length();
            int end = pos + (key == Constants.LOCATION_ZOOM ? 3 : 6);
            return src.substring(0, end > len ? len : end);
//            Log.d("myLogs", "len:"+len+"end:"+end);
        }
    }

}
