package com.islamic.monabihalzakren.ui;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.Referances;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	private ActionBarRtlizer rtlizer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.app_preference);
		setHasOptionsMenu(true);
		Preference aboutPref = findPreference(Referances.KEY_PREF_ABOUT);
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent about = new Intent(getActivity(), AboutActivity.class);
				startActivity(about);
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MonabihActivity) activity).onSectionAttached(getArguments().getInt(PlaceholderFragment.ARG_SECTION_NUMBER));
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		if (rtlizer != null)
			rtlizer.rtlize(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	public static Boolean isArabic(Context context) {

		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Referances.KEY_PREF_LANG, true);
	}

	public static Date getWakeTime(Context context) {
		String timeString = PreferenceManager.getDefaultSharedPreferences(context).getString(
				Referances.KEY_PREF_WAKE_TIME, null);
		SimpleDateFormat format = new SimpleDateFormat(Referances.TIME_FORMAT);
		Date time = new Date();

		try {
			time.setHours(format.parse(timeString).getHours());
			time.setMinutes(format.parse(timeString).getMinutes());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		return time;

	}

	public static Date getSleepTime(Context context) {
		String timeString = PreferenceManager.getDefaultSharedPreferences(context).getString(
				Referances.KEY_PREF_SLEEP_TIME, null);
		SimpleDateFormat format = new SimpleDateFormat(Referances.TIME_FORMAT);
		Date time = new Date();

		try {
			time.setHours(format.parse(timeString).getHours());
			time.setMinutes(format.parse(timeString).getMinutes());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		return time;
	}

	public static Date getSabahTime(Context context) {
		String timeString = PreferenceManager.getDefaultSharedPreferences(context).getString(
				Referances.KEY_PREF_SABAH_TIME, null);
		SimpleDateFormat format = new SimpleDateFormat(Referances.TIME_FORMAT);
		Date time = new Date();

		try {
			time.setHours(format.parse(timeString).getHours());
			time.setMinutes(format.parse(timeString).getMinutes());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		return time;

	}

	public static Date getMasa2Time(Context context) {
		String timeString = PreferenceManager.getDefaultSharedPreferences(context).getString(
				Referances.KEY_PREF_MASA2_TIME, null);
		SimpleDateFormat format = new SimpleDateFormat(Referances.TIME_FORMAT);
		Date time = new Date();

		try {
			time.setHours(format.parse(timeString).getHours());
			time.setMinutes(format.parse(timeString).getMinutes());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		return time;
	}

	public static Date getDohahTime(Context context) {
		String timeString = PreferenceManager.getDefaultSharedPreferences(context).getString(
				Referances.KEY_PREF_DOHAH_TIME, null);
		SimpleDateFormat format = new SimpleDateFormat(Referances.TIME_FORMAT);
		Date time = new Date();

		try {
			time.setHours(format.parse(timeString).getHours());
			time.setMinutes(format.parse(timeString).getMinutes());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		return time;

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(Referances.KEY_PREF_LANG)) {
			((MonabihApplication) getActivity().getApplication()).refreshLocale(true);
			Intent mIntent = getActivity().getIntent();
			mIntent.putExtra(PlaceholderFragment.ARG_SECTION_NUMBER, 3);
			getActivity().finish();
			startActivity(mIntent);
		} else if (key.equals(Referances.KEY_PREF_SABAH_TIME)) {
			List<AzkarEntity> azkar = DataContext.getAllAzkar(getActivity(), true, true);
			for (AzkarEntity zekr : azkar) {
				if (zekr.zekr_id == Referances.ZEKR_TYPE_SBAH_MASA2) {
					zekr.start_time = getSabahTime(getActivity());
					try {
						DataContext.editZekr(getActivity(), zekr);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (key.equals(Referances.KEY_PREF_MASA2_TIME)) {
			List<AzkarEntity> azkar = DataContext.getAllAzkar(getActivity(), true, true);
			for (AzkarEntity zekr : azkar) {
				if (zekr.zekr_id == Referances.ZEKR_TYPE_MASA2) {
					zekr.start_time = getMasa2Time(getActivity());
					try {
						DataContext.editZekr(getActivity(), zekr);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (key.equals(Referances.KEY_PREF_DOHAH_TIME)) {
			List<AzkarEntity> azkar = DataContext.getAllAzkar(getActivity(), true, true);
			for (AzkarEntity zekr : azkar) {
				if (zekr.zekr_id == Referances.ZEKR_TYPE_DOHA) {
					zekr.start_time = getDohahTime(getActivity());
					try {
						DataContext.editZekr(getActivity(), zekr);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (SettingsFragment.isArabic(getActivity())) {
			if (rtlizer == null) {
				rtlizer = new ActionBarRtlizer(getActivity());
				rtlizer.rtlize(getActivity());
			}
		}
	}

}
