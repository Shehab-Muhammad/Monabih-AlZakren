package com.quran.labs.androidquran.ui;

import static com.quran.labs.androidquran.data.Constants.PAGES_LAST;
import static com.quran.labs.androidquran.data.Constants.PAGES_LAST_DUAL;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.ui.MonabihActivity;
import com.islamic.monabihalzakren.ui.MonabihApplication;
import com.islamic.monabihalzakren.ui.PlaceholderFragment;
import com.quran.labs.androidquran.common.TranslationItem;
import com.quran.labs.androidquran.data.AyahInfoDatabaseHandler;
import com.quran.labs.androidquran.data.Constants;
import com.quran.labs.androidquran.data.QuranDataProvider;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.data.SuraAyah;
import com.quran.labs.androidquran.service.QuranDownloadService;
import com.quran.labs.androidquran.service.util.ServiceIntentHelper;
import com.quran.labs.androidquran.task.AsyncTask;
import com.quran.labs.androidquran.ui.fragment.JumpFragment;
import com.quran.labs.androidquran.ui.fragment.TabletFragment;
import com.quran.labs.androidquran.ui.helpers.AyahSelectedListener;
import com.quran.labs.androidquran.ui.helpers.AyahTracker;
import com.quran.labs.androidquran.ui.helpers.FragmentStatePagerAdapter;
import com.quran.labs.androidquran.ui.helpers.QuranDisplayHelper;
import com.quran.labs.androidquran.ui.helpers.QuranPageAdapter;
import com.quran.labs.androidquran.ui.helpers.QuranPageWorker;
import com.quran.labs.androidquran.util.QuranFileUtils;
import com.quran.labs.androidquran.util.QuranScreenInfo;
import com.quran.labs.androidquran.util.QuranUtils;
import com.quran.labs.androidquran.widgets.SlidingUpPanelLayout;

public class PagerActivity extends SherlockFragmentActivity implements AyahSelectedListener {
	private static final String TAG = "PagerActivity";
	private static final String AUDIO_DOWNLOAD_KEY = "AUDIO_DOWNLOAD_KEY";
	private static final String LAST_AUDIO_DL_REQUEST = "LAST_AUDIO_DL_REQUEST";
	public static final String LAST_READ_PAGE = "LAST_READ_PAGE";
	private static final String LAST_READING_MODE_IS_TRANSLATION = "LAST_READING_MODE_IS_TRANSLATION";
	private static final String LAST_ACTIONBAR_STATE = "LAST_ACTIONBAR_STATE";
	private static final String LAST_START_POINT = "LAST_START_POINT";
	private static final String LAST_ENDING_POINT = "LAST_ENDING_POINT";

	public static final String EXTRA_JUMP_TO_TRANSLATION = "jumpToTranslation";
	public static final String EXTRA_HIGHLIGHT_SURA = "highlightSura";
	public static final String EXTRA_HIGHLIGHT_AYAH = "highlightAyah";
	public static final String LAST_WAS_DUAL_PAGES = "wasDualPages";

	private static final long DEFAULT_HIDE_AFTER_TIME = 2000;

	private QuranPageWorker mWorker = null;
	private SharedPreferences mPrefs = null;
	private long mLastPopupTime = 0;
	private boolean mIsActionBarHidden = true;
	private ViewPager mViewPager = null;
	private QuranPageAdapter mPagerAdapter = null;
	private boolean mShouldReconnect = false;
	private SparseBooleanArray mBookmarksCache = null;
	private boolean mShowingTranslation = false;
	private AlertDialog mPromptDialog = null;
	private List<TranslationItem> mTranslations;
	private AyahInfoDatabaseHandler mAyahInfoAdapter, mTabletAyahInfoAdapter;
	private boolean mDualPages = false;
	private boolean mIsLandscape;

	public static final int MSG_HIDE_ACTIONBAR = 1;

	private Set<AsyncTask> mCurrentTasks = new HashSet<AsyncTask>();

	// AYAH ACTION PANEL STUFF
	// Max height of sliding panel (% of screen)
	private SlidingUpPanelLayout mSlidingPanel;
	private ViewPager mSlidingPager;
	private FragmentStatePagerAdapter mSlidingPagerAdapter;
	private boolean mIsInAyahMode;
	private SuraAyah mStart;
	private SuraAyah mEnd;

	private final PagerHandler mHandler = new PagerHandler(this);

	private static class PagerHandler extends Handler {
		private final WeakReference<PagerActivity> mActivity;

		public PagerHandler(PagerActivity activity) {
			mActivity = new WeakReference<PagerActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			PagerActivity activity = mActivity.get();
			if (activity != null) {
				if (msg.what == MSG_HIDE_ACTIONBAR) {
					activity.toggleActionBarVisibility(false);
				} else {
					super.handleMessage(msg);
				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		((MonabihApplication) getApplication()).refreshLocale(false);

		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedInstanceState);
		android.view.Window window = getWindow();

		boolean refresh = false;

		// make sure to remake QuranScreenInfo if it doesn't exist, as it
		// is needed to get images, to get the highlighting db, etc.
		QuranScreenInfo qsi = QuranScreenInfo.getOrMakeInstance(this);
		mDualPages = QuranUtils.isDualPages(this, qsi);

		// initialize ayah info database
		String filename = QuranFileUtils.getAyaPositionFileName();
		try {
			mAyahInfoAdapter = new AyahInfoDatabaseHandler(this, filename);
		} catch (Exception e) {
			// no ayah info database available
		}

		mTabletAyahInfoAdapter = null;
		if (qsi.isTablet(this)) {
			try {
				filename = QuranFileUtils.getAyaPositionFileName(qsi.getTabletWidthParam());
				mTabletAyahInfoAdapter = new AyahInfoDatabaseHandler(this, filename);
			} catch (Exception e) {
				// no ayah info database available for tablet
			}
		}

		int page = -1;

		mIsActionBarHidden = true;
		if (savedInstanceState != null) {
			android.util.Log.d(TAG, "non-null saved instance state!");
			Serializable lastAudioRequest = savedInstanceState.getSerializable(LAST_AUDIO_DL_REQUEST);

			page = savedInstanceState.getInt(LAST_READ_PAGE, -1);
			if (page != -1) {
				page = PAGES_LAST - page;
			}
			mShowingTranslation = savedInstanceState.getBoolean(LAST_READING_MODE_IS_TRANSLATION, false);
			if (savedInstanceState.containsKey(LAST_ACTIONBAR_STATE)) {
				mIsActionBarHidden = !savedInstanceState.getBoolean(LAST_ACTIONBAR_STATE);
			}
			boolean lastWasDualPages = savedInstanceState.getBoolean(LAST_WAS_DUAL_PAGES, mDualPages);
			refresh = (lastWasDualPages != mDualPages);

			mStart = savedInstanceState.getParcelable(LAST_START_POINT);
			mEnd = savedInstanceState.getParcelable(LAST_ENDING_POINT);
		}

		mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final Resources resources = getResources();
		mIsLandscape = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		int background = resources.getColor(R.color.transparent_actionbar_color);
		setContentView(R.layout.quran_page_activity_slider);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(background));

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (page == -1) {
				page = PAGES_LAST - extras.getInt("page", Constants.PAGES_FIRST);
			}

			mShowingTranslation = extras.getBoolean(EXTRA_JUMP_TO_TRANSLATION, mShowingTranslation);
		}

		updateActionBarTitle(PAGES_LAST - page);

		mWorker = QuranPageWorker.getInstance(this);
		mLastPopupTime = System.currentTimeMillis();
		mPagerAdapter = new QuranPageAdapter(getSupportFragmentManager(), mDualPages, mShowingTranslation);
		mViewPager = (ViewPager) findViewById(R.id.quran_pager);
		mViewPager.setAdapter(mPagerAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				Log.d(TAG, "onPageSelected(): " + position);
				int page = QuranInfo.getPageFromPos(position, mDualPages);
				mLastPopupTime = QuranDisplayHelper.displayMarkerPopup(PagerActivity.this, page, mLastPopupTime);
				if (mDualPages) {
					mLastPopupTime = QuranDisplayHelper
							.displayMarkerPopup(PagerActivity.this, page - 1, mLastPopupTime);
				}

				updateActionBarTitle(page);

			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setUiVisibilityListener();
		}
		toggleActionBarVisibility(true);

		if (mDualPages) {
			mViewPager.setCurrentItem(page / 2);
		} else {
			mViewPager.setCurrentItem(page);
		}

		// QuranSettings.setLastPage(this, PAGES_LAST - page);
		setLoading(false);

		// just got created, need to reconnect to service
		mShouldReconnect = true;

		// enforce orientation lock
		// if (QuranSettings.isLockOrientation(this)) {
		// int current = getResources().getConfiguration().orientation;
		// if (QuranSettings.isLandscapeOrientation(this)) {
		// if (current == Configuration.ORIENTATION_PORTRAIT) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// return;
		// }
		// } else if (current == Configuration.ORIENTATION_LANDSCAPE) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// return;
		// }
		// }

		if (refresh) {
			final int curPage = PAGES_LAST - page;
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mPagerAdapter.notifyDataSetChanged();
					int page = curPage;
					if (mDualPages) {
						if (page % 2 != 0) {
							page++;
						}
						page = PAGES_LAST_DUAL - (page / 2);
					} else {
						if (page % 2 == 0) {
							page--;
						}
						page = PAGES_LAST - page;
					}
					mViewPager.setCurrentItem(page);
				}
			});
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			mHandler.sendEmptyMessageDelayed(MSG_HIDE_ACTIONBAR, DEFAULT_HIDE_AFTER_TIME);
		} else {
			mHandler.removeMessages(MSG_HIDE_ACTIONBAR);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setUiVisibility(boolean isVisible) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mIsLandscape) {
			setUiVisibilityKitKat(isVisible);
			return;
		}

		int flags;
		if (isVisible) {
			flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		} else {
			flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		}
		mViewPager.setSystemUiVisibility(flags);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setUiVisibilityKitKat(boolean isVisible) {
		int flags;
		if (isVisible) {
			flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		} else {
			flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE;
		}
		mViewPager.setSystemUiVisibility(flags);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setUiVisibilityListener() {
		mViewPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int flags) {
				boolean visible = (flags & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0;
				mIsActionBarHidden = !visible;
				if (visible) {
					getSherlock().getActionBar().show();
				} else {
					getSherlock().getActionBar().hide();
				}

			}
		});
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void clearUiVisibilityListener() {
		mViewPager.setOnSystemUiVisibilityChangeListener(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mShouldReconnect) {
			mShouldReconnect = false;
		}

	}

	public AyahInfoDatabaseHandler getAyahInfoDatabase(String widthParam) {
		if (QuranScreenInfo.getInstance().getWidthParam().equals(widthParam)) {
			return mAyahInfoAdapter;
		} else {
			return mTabletAyahInfoAdapter;
		}
	}

	public void showGetRequiredFilesDialog() {
		if (mPromptDialog != null) {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.download_extra_data)
				.setPositiveButton(R.string.downloadPrompt_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int option) {
						downloadRequiredFiles();
						dialog.dismiss();
						mPromptDialog = null;
					}
				}).setNegativeButton(R.string.downloadPrompt_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int option) {
						dialog.dismiss();
						mPromptDialog = null;
					}
				});
		mPromptDialog = builder.create();
		mPromptDialog.show();
	}

	public void downloadRequiredFiles() {
		int downloadType = QuranDownloadService.DOWNLOAD_TYPE_ARABIC_SEARCH_DB;

		boolean haveDownload = false;
		QuranScreenInfo qsi = QuranScreenInfo.getOrMakeInstance(this);
		if (!QuranFileUtils.haveAyaPositionFile(this)) {
			String url = QuranFileUtils.getAyaPositionFileUrl();
			if (QuranUtils.isDualPages(this, qsi)) {
				url = QuranFileUtils.getAyaPositionFileUrl(qsi.getTabletWidthParam());
			}
			String destination = QuranFileUtils.getQuranDatabaseDirectory(this);
			// start the download
			String notificationTitle = getString(R.string.highlighting_database);
			Intent intent = ServiceIntentHelper.getDownloadIntent(this, url, destination, notificationTitle,
					AUDIO_DOWNLOAD_KEY, downloadType);
			startService(intent);

			haveDownload = true;
		}

		if (!QuranFileUtils.hasArabicSearchDatabase(this)) {
			String url = QuranFileUtils.getArabicSearchDatabaseUrl();

			// show "downloading required files" unless we already showed that
			// for
			// highlighting database, in which case show
			// "downloading search data"
			String notificationTitle = getString(R.string.highlighting_database);
			if (haveDownload) {
				notificationTitle = getString(R.string.search_data);
			}

			Intent intent = ServiceIntentHelper
					.getDownloadIntent(this, url, QuranFileUtils.getQuranDatabaseDirectory(this), notificationTitle,
							AUDIO_DOWNLOAD_KEY, downloadType);
			intent.putExtra(QuranDownloadService.EXTRA_OUTPUT_FILE_NAME, QuranDataProvider.QURAN_ARABIC_DATABASE);
			startService(intent);
		}

		if (downloadType != QuranDownloadService.DOWNLOAD_TYPE_AUDIO) {
			// if audio is playing, just show a status notification
			Toast.makeText(this, R.string.downloading_title, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (intent == null) {
			return;
		}

		Bundle extras = intent.getExtras();
		if (extras != null) {
			int page = PAGES_LAST - extras.getInt("page", Constants.PAGES_FIRST);
			updateActionBarTitle(PAGES_LAST - page);

			boolean currentValue = mShowingTranslation;
			mShowingTranslation = false;

			if (mShowingTranslation != currentValue) {
				if (mShowingTranslation) {
					// mPagerAdapter.setTranslationMode();
				} else {
					mPagerAdapter.setQuranMode();
				}

				invalidateOptionsMenu();
			}

			if (mDualPages) {
				page = page / 2;
			}
			mViewPager.setCurrentItem(page);
		}

		setIntent(intent);
	}

	public void jumpTo(int page) {
		Intent i = new Intent(this, PagerActivity.class);
		i.putExtra("page", page);
		onNewIntent(i);
	}

	@Override
	public void onPause() {
		if (mPromptDialog != null) {
			mPromptDialog.dismiss();
			mPromptDialog = null;
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		android.util.Log.d(TAG, "onDestroy()");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			clearUiVisibilityListener();
		}

		if (mAyahInfoAdapter != null) {
			mAyahInfoAdapter.closeDatabase();
		}

		if (mTabletAyahInfoAdapter != null) {
			mTabletAyahInfoAdapter.closeDatabase();
		}

		// If there are any unfinished tasks, stop them
		if (!mCurrentTasks.isEmpty()) {
			// Use a copy to avoid concurrent modification when calling cancel
			// since cancel causes the task to remove itself from this set
			List<AsyncTask> currentTasks = new ArrayList<AsyncTask>(mCurrentTasks);
			for (AsyncTask task : currentTasks) {
				task.cancel(true);
			}
		}

		super.onDestroy();
	}

	public boolean registerTask(AsyncTask task) {
		return mCurrentTasks.add(task);
	}

	public boolean unregisterTask(AsyncTask task) {
		return mCurrentTasks.remove(task);
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		int lastPage = QuranInfo.getPageFromPos(mViewPager.getCurrentItem(), mDualPages);
		state.putSerializable(LAST_READ_PAGE, lastPage);
		state.putBoolean(LAST_READING_MODE_IS_TRANSLATION, mShowingTranslation);
		state.putBoolean(LAST_ACTIONBAR_STATE, mIsActionBarHidden);
		state.putBoolean(LAST_WAS_DUAL_PAGES, mDualPages);
		if (mStart != null && mEnd != null) {
			state.putParcelable(LAST_START_POINT, mStart);
			state.putParcelable(LAST_ENDING_POINT, mEnd);
		}
		super.onSaveInstanceState(state);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.quran_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem quran = menu.findItem(R.id.goto_quran);
		if (!mShowingTranslation) {
			quran.setVisible(false);
		} else {
			quran.setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		if (itemId == R.id.goto_quran) {
			switchToQuran();
			return true;
		} else if (itemId == R.id.settings) {
			Intent i = new Intent(this, MonabihActivity.class);
			i.putExtra(PlaceholderFragment.ARG_SECTION_NUMBER, PlaceholderFragment.SETTINGS_SECTION);
			startActivity(i);
			finish();
			return true;
		} else if (itemId == R.id.search) {
			return onSearchRequested();
		} else if (itemId == android.R.id.home) {
			finish();
			return true;
		} else if (itemId == R.id.jump) {
			FragmentManager fm = getSupportFragmentManager();
			JumpFragment jumpDialog = new JumpFragment();
			jumpDialog.show(fm, JumpFragment.TAG);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshQuranPages() {
		int pos = mViewPager.getCurrentItem();
		int start = (pos == 0) ? pos : pos - 1;
		int end = (pos == mPagerAdapter.getCount() - 1) ? pos : pos + 1;
		for (int i = start; i <= end; i++) {
			Fragment f = mPagerAdapter.getFragmentIfExists(i);
			if (f != null && f instanceof AyahTracker) {
				((AyahTracker) f).updateView();
			}
		}
	}

	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

	public void setLoading(boolean isLoading) {
		setSupportProgressBarIndeterminateVisibility(isLoading);
	}

	public void setLoadingIfPage(int page) {
		int position = mViewPager.getCurrentItem();
		int currentPage = PAGES_LAST - position;
		if (currentPage == page) {
			setLoading(true);
		}
	}

	public void switchToQuran() {
		mPagerAdapter.setQuranMode();
		mShowingTranslation = false;
		int page = getCurrentPage();
		invalidateOptionsMenu();
		updateActionBarTitle(page);
	}

	ActionBar.OnNavigationListener mNavigationCallback = new ActionBar.OnNavigationListener() {
		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			Log.d(TAG, "item chosen: " + itemPosition);
			if (mTranslations != null && mTranslations.size() > itemPosition) {
				TranslationItem item = mTranslations.get(itemPosition);
				mPrefs.edit().putString(Constants.PREF_ACTIVE_TRANSLATION, item.filename).commit();

				int pos = mViewPager.getCurrentItem() - 1;
				for (int count = 0; count < 3; count++) {
					if (pos + count < 0) {
						continue;
					}
					Fragment f = mPagerAdapter.getFragmentIfExists(pos + count);
					if (f != null && f instanceof TabletFragment) {
						((TabletFragment) f).refresh(item.filename);
					}
				}
				return true;
			}
			return false;
		}
	};

	public List<TranslationItem> getTranslations() {
		return mTranslations;
	}

	private void updateActionBarTitle(int page) {
		String sura = QuranInfo.getSuraNameFromPage(this, page, true);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(sura);
		// String desc = QuranInfo.getPageSubtitle(this, page);
		// actionBar.setSubtitle(desc);
	}

	private int getCurrentPage() {
		return QuranInfo.getPageFromPos(mViewPager.getCurrentItem(), mDualPages);
	}

	public void toggleActionBarVisibility(boolean visible) {
		if (!(visible ^ mIsActionBarHidden)) {
			toggleActionBar();
		}
	}

	public void toggleActionBar() {
		if (mIsActionBarHidden) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				setUiVisibility(true);
			} else {
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getSupportActionBar().show();

			}

			mIsActionBarHidden = false;
		} else {
			mHandler.removeMessages(MSG_HIDE_ACTIONBAR);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				setUiVisibility(false);
			} else {
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				getSupportActionBar().hide();

			}

			mIsActionBarHidden = true;
		}
	}

	public QuranPageWorker getQuranPageWorker() {
		return mWorker;
	}

	class IsPageBookmarkedTask extends AsyncTask<Integer, Void, SparseBooleanArray> {

		@Override
		protected SparseBooleanArray doInBackground(Integer... params) {
			if (params == null) {
				return null;
			}

			SparseBooleanArray result = new SparseBooleanArray();
			for (Integer page : params) {
				result.put(page, false);
			}

			return result;
		}

		@Override
		protected void onPostExecute(SparseBooleanArray result) {
			if (result != null) {
				int size = result.size();
				for (int i = 0; i < size; i++) {
					int page = result.keyAt(i);
					boolean bookmarked = result.get(page);
					mBookmarksCache.put(page, bookmarked);
				}
				invalidateOptionsMenu();
			}
		}
	}

	// #######################################################################
	// #################### AYAH ACTION PANEL STUFF ####################
	// #######################################################################

	@Override
	public boolean isListeningForAyahSelection(EventType eventType) {
		return eventType == EventType.LONG_PRESS || eventType == EventType.SINGLE_TAP && mIsInAyahMode;
	}

	@Override
	public boolean onAyahSelected(EventType eventType, SuraAyah suraAyah, AyahTracker tracker) {
		return false;
	}

	@Override
	public boolean onClick(EventType eventType) {
		switch (eventType) {
		case SINGLE_TAP:
			toggleActionBar();
			return true;
		default:
			return false;
		}
	}

	public SuraAyah getSelectionStart() {
		return mStart;
	}

	public SuraAyah getSelectionEnd() {
		return mEnd;
	}

	private class SlidingPanelListener implements SlidingUpPanelLayout.PanelSlideListener {

		@Override
		public void onPanelSlide(View panel, float slideOffset) {
		}

		@Override
		public void onPanelCollapsed(View panel) {

			mSlidingPanel.hidePane();
		}

		@Override
		public void onPanelExpanded(View panel) {
		}

		@Override
		public void onPanelAnchored(View panel) {
		}
	}

}