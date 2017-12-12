package com.andypro.mylocations;

//import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.app.DialogFragment;
//import android.os.Parcel;
//import android.content.ContentValues;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.util.ArrayMap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.andypro.mylocations.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class ListDialogFragment extends DialogFragment {

//    boolean locationMode;

    public interface ListDialogCallbacks {
//        public void onDialogPositiveClick(DialogFragment dialog);
//        public void onDialogNegativeClick(DialogFragment dialog);
        void onOkListViewDialog(View view);
    }

//    LocationDialogListener mListener;

    public ListDialogFragment() {
        // Empty constructor required for DialogFragment
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LocationDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LocationDialogListener");
        }
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LocationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LocationDialogListener");
        }
    }
    */
    public static ListDialogFragment newInstance(boolean locationMode, int cmd, HashMap entry,
                                                 ArrayList<ArrayList<String>> categories) {
        ListDialogFragment dlg = new ListDialogFragment();
        Bundle args = new Bundle();

//        if (obj instanceof Location)
//            args.putParcelable("edited_item", (Location) obj);
//        if (obj instanceof Category)
//            args.putParcelable("edited_item", (Category) obj);

//        args.putParcelable("edited_item", obj instanceof Location ? (Location) obj : (Category) obj);
        args.putBoolean("locationMode", locationMode);
        args.putInt("cmd", cmd);
        args.putSerializable("entry", entry);

//        args.putSerializable("categories", categories);
        args.putStringArrayList("categories_ids", categories.get(0));
        args.putStringArrayList("categories_names", categories.get(1));

        dlg.setArguments(args);
        return dlg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        EditText etName, etAddress, etLat, etLng, etZoom;

//        boolean mode = getArguments().containsKey("location");
//        boolean locationMode = obj instanceof Location;

//        Location location = getArguments().getParcelable("location");
        Bundle bundle = getArguments();
        final boolean locationMode = bundle.getBoolean("locationMode");
        final int cmd = bundle.getInt("cmd");
        final HashMap entry = (HashMap) bundle.getSerializable("entry");
        ArrayList categories_ids = bundle.getStringArrayList("categories_ids");
        ArrayList categories_names = bundle.getStringArrayList("categories_names");

//        final ContentValues dbContent;

//        Log.d(Constants.LOG_TAG, "hash:"+entry.get("name")+":"+entry.get("lat")+":"+entry.get("lng"));


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(locationMode ?
                R.layout.location_dialog : R.layout.category_dialog, null);

        ((EditText) view.findViewById(R.id.etName)).setText(entry.get("name").toString());


        if (locationMode) {

//            String[] data = {"one", "two", "three", "four", "five"};
//            Collection vc = categories.values();
//            String[] vcs = vc.toArray(new String[vc.size()]);


            Log.d(Constants.LOG_TAG, "arr ids: " + categories_ids);
            Log.d(Constants.LOG_TAG, "arr names: " + categories_names);
//            Log.d(Constants.LOG_TAG, "arr: " + vc.get(0));
//            Log.d(Constants.LOG_TAG, "arr: " + vc.get(1));


            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item, categories_names);

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = view.findViewById(R.id.spinner);
            spinner.setAdapter(spinnerAdapter);

            // заголовок
            // spinner.setPrompt("Category");
            // выделяем элемент
            spinner.setSelection(categories_ids.indexOf(entry.get("category").toString()));
            // устанавливаем обработчик нажатия
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.d(Constants.LOG_TAG, "spinner pos:" + position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });


            ((EditText) view.findViewById(R.id.etAddress)).setText(entry.get("address").toString());
            ((EditText) view.findViewById(R.id.etLat)).setText(entry.get("lat").toString());
            ((EditText) view.findViewById(R.id.etLng)).setText(entry.get("lng").toString());
            ((EditText) view.findViewById(R.id.etZoom)).setText(entry.get("zoom").toString());
        }

        int title_id = cmd == R.id.menu_add ?
                (locationMode ? R.string.new_location : R.string.new_category) :
                (locationMode ? R.string.edit_location : R.string.edit_category);
        builder.setView(view)
//                .setTitle(R.string.new_location)
                .setTitle(title_id)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        mListener.onDialogPositiveClick(ListDialogFragment.this);

//                        ListDialogCallbacks mListener = (ListDialogCallbacks) getActivity();
//                        mListener.onOkListViewDialog(view);


//                        spinner.getSelectedItemPosition()

                        ((ListDialogCallbacks) getActivity()).onOkListViewDialog(view);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        mListener.onDialogNegativeClick(ListDialogFragment.this);
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}