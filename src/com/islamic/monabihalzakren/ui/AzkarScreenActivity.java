package com.islamic.monabihalzakren.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.Utilities.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class AzkarScreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_azkar_screen);
		final View contentView = findViewById(R.id.fullscreen_content);
		ImageView screenContent = (ImageView) findViewById(R.id.dikhr_screen);
		TextView dihkrName = (TextView) findViewById(R.id.zekr_screen_title);
		int zekrId = getIntent().getIntExtra(Referances.ZEKR_TYPE, 0);
		int imgSrc = 0;
		dihkrName.setText(Referances.getZekrName(this, zekrId));
		switch (zekrId) {
		case Referances.ZEKR_TYPE_MASA2:
			imgSrc = R.drawable.mas2screen;
			break;
		case Referances.ZEKR_TYPE_SBAH_MASA2:
			imgSrc = R.drawable.azkar1;
			break;
		case Referances.ZEKR_TYPE_SLEEP:
			imgSrc = R.drawable.sleepscreen;
			break;
		}

		screenContent.setImageResource(imgSrc);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		// int height = displaymetrics.heightPixels;
		// int wwidth = displaymetrics.widthPixels;

		contentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_POINTER_UP:
					// if(prevEvent == MotionEvent.ACTION_DOWN)
					mSystemUiHider.toggle();
					return true;
				}
				// prevEvent = event.getAction();
				return false;
			}
		});
		// LayoutParams lp = new LayoutParams(wwidth,
		// LayoutParams.WRAP_CONTENT);
		// contentView.setLayoutParams(lp);
		// mSystemUiHider
		// .setOnVisibilityChangeListener(new
		// SystemUiHider.OnVisibilityChangeListener() {
		// // Cached values.
		// int mControlsHeight;
		// int mShortAnimTime;
		//
		// @Override
		// @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		// public void onVisibilityChange(boolean visible) {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
		// // If the ViewPropertyAnimator API is available
		// // (Honeycomb MR2 and later), use it to animate the
		// // in-layout UI controls at the bottom of the
		// // screen.
		// if (mControlsHeight == 0) {
		// mControlsHeight = controlsView.getHeight();
		// }
		// if (mShortAnimTime == 0) {
		// mShortAnimTime = getResources().getInteger(
		// android.R.integer.config_shortAnimTime);
		// }
		// controlsView
		// .animate()
		// .translationY(visible ? 0 : mControlsHeight)
		// .setDuration(mShortAnimTime);
		// } else {
		// // If the ViewPropertyAnimator APIs aren't
		// // available, simply show or hide the in-layout UI
		// // controls.
		// controlsView.setVisibility(visible ? View.VISIBLE
		// : View.GONE);
		// }
		//
		// if (visible && AUTO_HIDE) {
		// // Schedule a hide().
		// delayedHide(AUTO_HIDE_DELAY_MILLIS);
		// }
		// }
		// });

		// Set up the user interaction to manually show or hide the system UI.

		// getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(1000);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent backIntent = new Intent(this, MonabihActivity.class);
			backIntent.putExtra(PlaceholderFragment.ARG_SECTION_NUMBER, PlaceholderFragment.AZKAR_SECTION);
			startActivity(backIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public ViewGroup getActionBarContainerView() {
		int resId;
		ViewGroup actionBarView;

		Window window = this.getWindow();
		View view = window.getDecorView();

		resId = getResources().getIdentifier("action_bar_container", "id", this.getPackageName());
		actionBarView = (ViewGroup) view.findViewById(resId);

		if (actionBarView == null) {
			resId = getResources().getIdentifier("action_bar_container", "id", "android");
			actionBarView = (ViewGroup) view.findViewById(resId);
		}

		return actionBarView;
	}
}
