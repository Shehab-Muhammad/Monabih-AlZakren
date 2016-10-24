package com.islamic.monabihalzakren.ui.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.islamic.monabihalzakren.Utilities.Referances;

public class TimePickerPref extends DialogPreference {
	private Date time = Calendar.getInstance().getTime();
	private TimePicker picker = null;

	public TimePickerPref(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);
		setPositiveButtonText(ctxt.getResources()
				.getString(android.R.string.ok));
		setNegativeButtonText(ctxt.getResources().getString(
				android.R.string.cancel));
	}

	@Override
	protected View onCreateDialogView() {
		picker = new TimePicker(getContext());

		return (picker);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		picker.setCurrentHour(time.getHours());
		picker.setCurrentMinute(time.getMinutes());
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			time.setHours(picker.getCurrentHour());
			time.setMinutes(picker.getCurrentMinute());

			String timeString = (String) DateFormat.format(
					Referances.TIME_FORMAT, time);

			if (callChangeListener(timeString)) {
				persistString(timeString);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return (a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		String timeStr = null;

		if (restoreValue) {
			if (defaultValue == null) {
				time.setHours(0);
				time.setMinutes(0);
				String intialTime = (String) DateFormat.format(
						Referances.TIME_FORMAT, time);
				timeStr = getPersistedString(intialTime);
			} else {
				timeStr = getPersistedString(defaultValue.toString());
			}
		} else {
			timeStr = defaultValue.toString();
			persistString(timeStr);
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					Referances.TIME_FORMAT);
			time = format.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}