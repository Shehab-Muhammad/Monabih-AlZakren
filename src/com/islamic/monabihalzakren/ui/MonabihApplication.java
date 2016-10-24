package com.islamic.monabihalzakren.ui;

import java.lang.reflect.Field;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;

import com.islamic.monabihalzakren.Utilities.Referances;

public class MonabihApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// SharedPreferences sp =
		// PreferenceManager.getDefaultSharedPreferences(this);
		// sp.edit().putBoolean(NavigationDrawerFragment.PREF_USER_LEARNED_DRAWER,
		// false).apply();
		refreshLocale(false);
	}

	@SuppressLint("NewApi")
	public void refreshLocale(boolean force) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String language = prefs.getBoolean(Referances.KEY_PREF_LANG, true) ? "ar" : null;
		Locale locale = null;
		if ("ar".equals(language)) {
			locale = new Locale("ar");
			try {
				final Typeface customFontTypeface = Typeface.createFromAsset(getAssets(),
						"fonts/DroidNaskh-Regular.ttf");

				final Field defaultFontTypefaceField = Typeface.class.getDeclaredField("SANS_SERIF");
				defaultFontTypefaceField.setAccessible(true);
				defaultFontTypefaceField.set(null, customFontTypeface);
			} catch (Exception e) {
				// Log.e("Can not set custom font " +
				// "fonts/DroidNaskh-Regular.ttf" + " instead of " + "DEFAULT");
			}
		} else if (force) {

			// get the system locale (since we overwrote the default locale)
			locale = Resources.getSystem().getConfiguration().locale;
		}
		if (language == null) {
			try {
				final Typeface regular = Typeface.SANS_SERIF;

				Field DEFAULT = Typeface.class.getDeclaredField("SANS_SERIF");
				DEFAULT.setAccessible(true);
				DEFAULT.set(null, regular);

			} catch (Exception e) {
			}
		}

		if (locale != null) {
			Locale.setDefault(locale);
			Configuration config = getResources().getConfiguration();
			config.locale = locale;
			getResources().updateConfiguration(config, getResources().getDisplayMetrics());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
				config.setLayoutDirection(locale);
		}
	}
}
