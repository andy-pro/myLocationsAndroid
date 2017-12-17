package com.andypro.mylocations;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.HashMap;

import com.andypro.mylocations.utils.Constants;

import android.util.Log;

public class ListDialogFragment extends DialogFragment {
//
//    public interface ListDialogCallbacks {
//        void onOkListViewDialog(ContentValues cv);
//    }

    boolean locationMode;
    ArrayList<String> categories_ids, categories_names;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final MainActivity mActivity = (MainActivity) getActivity();
        final HashMap entry = mActivity.currentEntry;
        final int cmd = mActivity.currentCmd;
        locationMode = mActivity.locationMode;

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View view = inflater.inflate(locationMode ?
                R.layout.location_dialog : R.layout.category_dialog, null);

        ((EditText) view.findViewById(R.id.etName)).setText(entry.get("name").toString());

        if (locationMode) {

            if (savedInstanceState == null) {
                ArrayList<ArrayList<String>> categories = mActivity.getListViewFragment().getCategoryNames();
                categories_ids = categories.get(0);
                categories_names = categories.get(1);
            } else {
                categories_ids = savedInstanceState.getStringArrayList("categories_ids");
                categories_names = savedInstanceState.getStringArrayList("categories_names");
            }

            Log.d(Constants.LOG_TAG, "#######################################");
            Log.d(Constants.LOG_TAG, "categories array ids: " + categories_ids);
            Log.d(Constants.LOG_TAG, "categories array names: " + categories_names);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                    mActivity, android.R.layout.simple_spinner_item, categories_names);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = view.findViewById(R.id.spinner);
            spinner.setAdapter(spinnerAdapter);
            // заголовок
            // spinner.setPrompt("Category");
            // выделяем элемент
            spinner.setSelection(categories_ids.indexOf(entry.get("category").toString()));
            // устанавливаем обработчик нажатия
            /*
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.d(Constants.LOG_TAG, "spinner pos:" + position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            }); */

            ((EditText) view.findViewById(R.id.etAddress)).setText(entry.get("address").toString());
            final EditText etLat = view.findViewById(R.id.etLat);
            final EditText etLng = view.findViewById(R.id.etLng);
            final EditText etZoom = view.findViewById(R.id.etZoom);
            setPositionToForm(etLat, etLng, etZoom, entry);

            ImageButton positionBtn = view.findViewById(R.id.ibSetPos);
            positionBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setPositionToForm(etLat, etLng, etZoom, mActivity.currentMapPosition.toHashMap());
                }
            });
        }

        int title_id = cmd == R.id.menu_add ?
                (locationMode ? R.string.new_location : R.string.new_category) :
                (locationMode ? R.string.edit_location : R.string.edit_category);
        builder.setView(view)
                .setTitle(title_id)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        spinner.getSelectedItemPosition()
//                        ((ListDialogCallbacks) getActivity()).onOkListViewDialog(
                        mActivity.onOkListViewDialog(
                                locationMode ? Location.getContentValues(view, categories_ids) :
                                        Category.getContentValues(view)
                        );

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(Constants.LOG_TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Log.d(Constants.LOG_TAG, "List dialog save instance");
        Log.d(Constants.LOG_TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (locationMode) {
            outState.putStringArrayList("categories_ids", categories_ids);
            outState.putStringArrayList("categories_names", categories_names);
        }
    }

    private void setPositionToForm(EditText etLat, EditText etLng, EditText etZoom, HashMap entry) {
        etLat.setText(Location.format(entry, Constants.LOCATION_LAT));
        etLng.setText(Location.format(entry, Constants.LOCATION_LNG));
        etZoom.setText(Location.format(entry, Constants.LOCATION_ZOOM));
    }

}