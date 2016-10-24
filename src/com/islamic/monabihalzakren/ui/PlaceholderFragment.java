package com.islamic.monabihalzakren.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class PlaceholderFragment extends android.app.Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final int DIARY_SECTION = 0;
	public static final int AZKAR_SECTION = 1;
	public static final int KHATMA_SECTION = 2;
	public static final int SETTINGS_SECTION = 3;
	public static final int ABOUT_SECTION = 4;
	public static final int RATE_SECTION = 5;

	/**
	 * Returns a new instance of selected fragment.
	 */
	public static Fragment newInstance(int sectionNumber) {
		Fragment fragment = new Fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		switch (sectionNumber) {
		case 1:
			fragment = new DiaryFragment();
			break;
		case 2:
			fragment = new AzkarFragment();
			break;
		case 3:
			fragment = new KhatmahFragment();
			break;
		}

		fragment.setArguments(args);
		return fragment;
	}

	public PlaceholderFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MonabihActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		// ((MainActivity) activity).restoreActionBar();
	}

}