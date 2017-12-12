package com.andypro.mylocations.utils;

public class Constants {

	public static final String LOG_TAG = "myLogs";

	public static final String AUTHORITY = "com.andypro.mylocations.provider.Location";

	public static final String CATEGORY_TABLE = "category";
	public static final String LOCATION_TABLE = "location";

	/*
	public static final String CATEGORIES = "/categories";
	public static final String LOCATIONS = "/locations";
	public static final String LOCATION_ID = "LocationID";
	public static final String CATEGORY_ID = "CategoryID";
	public static final String LOCATION_NAME = "LocationName";
	public static final String CATEGORY_NAME = "CategoryName";
	*/

	// menu commands
	//	public static final int MENU_ADD = 1;
	//	public static final int MENU_EDIT = 2;
	//	public static final int MENU_DELETE = 3;
	// R.id.menu_add
	// R.id.menu_edit
	// R.id.menu_delete
	// R.id.menu_new_location
	// R.id.menu_copy_location

	// DB columns
	public static final String COMMON_ID = "_id";
	public static final String COMMON_NAME = "name";

	public static final String LOCATION_ADDRESS = "address";
	public static final String LOCATION_LAT = "lat";
	public static final String LOCATION_LNG = "lng";
	public static final String LOCATION_ZOOM = "zoom";
	public static final String LOCATION_CATEGORY = "category";

	public static final String[] CATEGORY_PROJECTION = new String[] {
			COMMON_ID, COMMON_NAME };

	public static final String[] LOCATION_PROJECTION = new String[] {
			COMMON_ID,
			COMMON_NAME,
			Constants.LOCATION_ADDRESS,
			Constants.LOCATION_LAT,
			Constants.LOCATION_LNG,
			Constants.LOCATION_ZOOM,
			Constants.LOCATION_CATEGORY
	};

	public static final String DEFAULT_SORT_ORDER = COMMON_NAME
			+ " COLLATE NOCASE ASC";

}
