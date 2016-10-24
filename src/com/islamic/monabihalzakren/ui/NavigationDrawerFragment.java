package com.islamic.monabihalzakren.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.ui.widgets.ArabicTextView;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView, sListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer,
				container, false);
		mDrawerListView = (ListView) mRelativeLayout.findViewById(R.id.list1);
		sListView = (ListView) mRelativeLayout.findViewById(R.id.list2);
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		sListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position + 3);
			}
		});

		String[] listItems1 = getResources().getStringArray(R.array.menu1);
		String[] listItems2 = getResources().getStringArray(R.array.menu2);
		int[] icons = { R.drawable.ic_diary_frag, R.drawable.ic_azkar_fragment, R.drawable.ic_khatmat_frag,
				R.drawable.ic_diary_frag_pressed, R.drawable.ic_azkar_fragment_pressed,
				R.drawable.ic_khatmat_frag_pressed };
		mDrawerListView.setAdapter(new ListViewAdapter(getActivity(), icons, listItems1));
		sListView.setAdapter(new BasicListAdapter(getActivity(), R.layout.list2_item, listItems2));
		if (mCurrentSelectedPosition < 3)
			mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		else
			sListView.setItemChecked(mCurrentSelectedPosition - 3, true);
		setListViewHeightBasedOnChildren(mDrawerListView);
		setListViewHeightBasedOnChildren(sListView);
		return mRelativeLayout;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	public void DeselectList(int index) {
		ListView mDeselectList = new ListView(getActivity());
		if (index == 1 && mDrawerListView != null) {
			((ListViewAdapter) (mDrawerListView.getAdapter())).isSecondList = true;
			((ListViewAdapter) (mDrawerListView.getAdapter())).notifyDataSetChanged();
			mDeselectList = mDrawerListView;
		} else if (sListView != null)
			mDeselectList = sListView;
		for (int i = 0; i < mDeselectList.getChildCount(); i++) {
			View mDeselectView = mDeselectList.getChildAt(i);
			mDeselectView.setBackgroundResource(android.R.color.transparent);

		}
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		if (SettingsFragment.isArabic(getActivity())) {
			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);
		}
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}
		};

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void selectItem(int position) {
		if (position < 4)
			mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			if (position < 3) {
				mDrawerListView.setItemChecked(position, true);
				DeselectList(2);
			} else if (sListView != null) {
				sListView.setItemChecked(position - 3, true);
				DeselectList(1);
			}
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
			if (!mUserLearnedDrawer && mDrawerLayout != null) {
				mDrawerLayout.openDrawer(mFragmentContainerView);
				mDrawerLayout.post(new Runnable() {
					@Override
					public void run() {
						mDrawerToggle.syncState();
					}
				});
			}

		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (SettingsFragment.isArabic(getActivity())) {
			if (item != null && item.getItemId() == android.R.id.home) {
				if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
					mDrawerLayout.closeDrawer(Gravity.RIGHT);
				} else {
					mDrawerLayout.openDrawer(Gravity.RIGHT);
				}
			}
			return false;
		} else if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	public int getCurrentPosition() {
		return mCurrentSelectedPosition;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	public void closeDrawer() {
		if (mDrawerLayout != null)
			mDrawerLayout.closeDrawer(mFragmentContainerView);
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}

	class ListViewAdapter extends ArrayAdapter<String> {

		Context activity;
		int photos[];
		String titles[];
		public boolean isSecondList = false;

		public ListViewAdapter(Context context, int pics[], String[] names) {
			super(context, R.layout.menu_raw, R.id.item_title, names);
			this.activity = context;
			this.photos = pics;
			this.titles = names;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater infalter = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
			View DrawerItem = infalter.inflate(R.layout.menu_raw, parent, false);
			TextView label = null;
			ImageView pic = (ImageView) DrawerItem.findViewById(R.id.menu_icon);
			if (SettingsFragment.isArabic(activity)) {
				label = (ArabicTextView) DrawerItem.findViewById(R.id.item_title);
			} else {
				label = (TextView) DrawerItem.findViewById(R.id.item_title);
			}
			label.setText(titles[position]);
			if (position == mCurrentSelectedPosition)
				label.setTextColor(getResources().getColor(R.color.app_color));
			pic.setImageResource(photos[(position != mCurrentSelectedPosition) ? position : position + 3]);
			DrawerItem.setBackgroundResource((position != mCurrentSelectedPosition) ? android.R.color.transparent
					: R.color.lighter_gray);
			return DrawerItem;

		}
	}

	class BasicListAdapter extends ArrayAdapter<String> {

		Context mContext;
		String[] names;
		int mResource;

		public BasicListAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
			mContext = context;
			names = objects;
			mResource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater infalter = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			View DrawerItem = infalter.inflate(mResource, parent, false);
			if (SettingsFragment.isArabic(mContext)) {
				ArabicTextView mText = (ArabicTextView) DrawerItem.findViewById(R.id.text2);
				mText.setText(names[position]);
			} else {
				TextView mText = (TextView) DrawerItem.findViewById(R.id.text2);
				mText.setText(names[position]);
			}

			return DrawerItem;
		}

	}
}
