package com.uwcse403.pocketpickup.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment  {
	public static final String STATE_DATE_INIT = "dpf_date_init";
	public static final String STATE_DATE_MIN = "dpf_date_min";
	public static final String STATE_DATE_MAX = "dpf_date_max";
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	final DatePickerDialog dpd;
        final Calendar c;
        long minDate;
        long maxDate;
        int year, month, day;
        
    	c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        
        Bundle args = this.getArguments();

    	// Update calendar if given initial date
    	long initDate = args.getLong(STATE_DATE_INIT);
    	if (initDate != 0L) {
        	c.setTimeInMillis(initDate);
    	}
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    	        	
        minDate = args.getLong(STATE_DATE_MIN);
        maxDate = args.getLong(STATE_DATE_MAX);
 

        // Create a new instance of DatePickerDialog and set date constraints
        dpd = new DatePickerDialog(getActivity(), (OnDateSetListener)getActivity(), year, month, day);
        final DatePicker picker = dpd.getDatePicker();
        
        // minimum date defaults to current date
        if (minDate != 0L) {
        	picker.setMinDate(minDate);
        } else {
        	picker.setMinDate(c.getTimeInMillis());
        }
        
        // no default maximum date
        if (maxDate != 0L) {
        	picker.setMaxDate(maxDate);
        }
        
        return dpd;
    }
}
