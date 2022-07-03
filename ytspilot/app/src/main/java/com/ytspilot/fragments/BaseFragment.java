package com.ytspilot.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.ytspilot.SessionManager;

import java.util.Calendar;
import java.util.Date;


public class BaseFragment extends Fragment {
    //    maintenance
    Dialog progress;
    Gson mGson;

    SessionManager session;
    String driver_id = "";
    String vehicle_id = "";


    public void ShowDatepicker(final EditText et) {
        final Calendar c = Calendar.getInstance();


        final Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_YEAR, -7);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String selecteddate = "" + year ;

                if((monthOfYear + 1) < 10){

                    selecteddate = selecteddate + "0"+( monthOfYear + 1);
                }else{
                    selecteddate = selecteddate +( monthOfYear + 1);
                }
                if(dayOfMonth < 10){

                    selecteddate = selecteddate + "0"+dayOfMonth;
                }else{
                    selecteddate = selecteddate +dayOfMonth;
                }


                int selectedddateint = Integer.parseInt(selecteddate);


                if (selectedddateint <= dateinint(c) && selectedddateint >= dateinint(maxDate)) {
                    et.setText("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                } else {
                    ShowToast("Please select proper date");
                }
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.getDatePicker().setMinDate(maxDate.getTime().getTime());
        dialog.show();
    }


    public int dateinint(Calendar c) {

        String s = "" + c.get(Calendar.YEAR);

        if ((c.get(Calendar.MONTH) + 1) < 10) {
            s = s + "0" + (c.get(Calendar.MONTH) + 1);
        } else {
            s = s + (c.get(Calendar.MONTH) + 1);
        }
        if (c.get(Calendar.DATE) < 10) {
            s = s + "0" + (c.get(Calendar.DATE));
        } else {
            s = s + (c.get(Calendar.DATE));
        }


        return Integer.parseInt(s);
    }

    public void ShowToast(String message) {
        if (message.length() > 0)
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    protected void resetEdittext(View view) {
        if (view instanceof ViewGroup
                && ((ViewGroup) view).getChildCount() != 0) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                resetEdittext(((ViewGroup) view).getChildAt(i));
            }
        } else {

            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }

        }
    }


    public Boolean ValidateEt(EditText et) {

        return ValidateEt(et, 0);
    }

    public Boolean ValidateEt(EditText et, int length) {

        if (et != null) {
            String s = et.getText().toString().trim();
            if (s.length() > length) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

}
