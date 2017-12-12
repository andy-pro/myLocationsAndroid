package com.andypro.mylocations;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.andypro.mylocations.utils.Constants;

import java.util.HashMap;

public class Category implements Parcelable {

    String name;
    long _id;

    public Category() {
        name = "";
        _id = -1;
    }

    /*
    public Category(String name) {
        this.name = name;
    }

    public Category(String name, long id) {
        this.name = name;
        this.id = id;
    } */

    // create Location from Database Cursor
    public Category(Cursor crs) {
        name = crs.getString(crs.getColumnIndex(Constants.COMMON_NAME));
        _id = crs.getLong(crs.getColumnIndex(Constants.COMMON_ID));
    }

    // parcel part
    public Category(Parcel in) {
        name = in.readString();
        _id = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(_id);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);  //using parcelable constructor
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public HashMap toHashMap() {
        HashMap entry = new HashMap();
        entry.put(Constants.COMMON_NAME, name);
        entry.put(Constants.COMMON_ID, _id);
//        Log.d(Constants.LOG_TAG, "hash:"+entry.get("name")+":"+entry.get("lat")+":"+entry.get("lng"));
        return entry;
    }

    public static ContentValues getContentValues(View view) {

        String name = ((EditText) view.findViewById(R.id.etName)).getText().toString();

        ContentValues entry = new ContentValues();

        entry.put(Constants.COMMON_NAME, name);
        return entry;

    }

}
