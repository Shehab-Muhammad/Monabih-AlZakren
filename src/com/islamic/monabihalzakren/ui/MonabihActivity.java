package com.islamic.monabihalzakren.ui;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.islamic.monabihalzakren.R;

public class MonabihActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	public ActionBarRtlizer rtlizer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		((MonabihApplication) getApplication()).refreshLocale(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(
				R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		PreferenceManager.setDefaultValues(this, R.xml.app_preference, false);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		Bundle args = new Bundle();
		switch (position) {
		case 3:
			SettingsFragment sFragment = new SettingsFragment();
			args.putInt(PlaceholderFragment.ARG_SECTION_NUMBER, position + 1);
			sFragment.setArguments(args);
			getFragmentManager().beginTransaction().replace(R.id.container, sFragment).commit();
			break;
		case 4:
			Intent aboutUs = new Intent(this, AboutActivity.class);
			startActivity(aboutUs);
			break;
		default:
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.container, PlaceholderFragment.newInstance(position + 1),
							String.valueOf(position + 1)).commit();
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getResources().getStringArray(R.array.menu1)[0];
			break;
		case 2:
			mTitle = getResources().getStringArray(R.array.menu1)[1];
			break;
		case 3:
			mTitle = getResources().getStringArray(R.array.menu1)[2];
			break;
		case 4:
			mTitle = getResources().getStringArray(R.array.menu2)[0];
			break;
		case 5:
			mTitle = getResources().getStringArray(R.array.menu2)[1];
			break;
		case 6:
			mTitle = getResources().getStringArray(R.array.menu2)[2];
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar == null)
			return;
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.closeDrawer();
			return;
		} else if (mNavigationDrawerFragment.getCurrentPosition() != PlaceholderFragment.DIARY_SECTION) {
			mNavigationDrawerFragment.selectItem(PlaceholderFragment.DIARY_SECTION);
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		int mCurrentPosition = getIntent().getIntExtra(PlaceholderFragment.ARG_SECTION_NUMBER, 0);
		mNavigationDrawerFragment.selectItem(mCurrentPosition);
		if (rtlizer != null)
			rtlizer.rtlize(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getIntent().putExtra(PlaceholderFragment.ARG_SECTION_NUMBER, mNavigationDrawerFragment.getCurrentPosition());
	}

}
