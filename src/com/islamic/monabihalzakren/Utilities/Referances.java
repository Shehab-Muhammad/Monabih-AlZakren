package com.islamic.monabihalzakren.Utilities;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.ui.SettingsFragment;

public class Referances {

	public static final int ZEKR_TYPE_TASBEH = 11;
	public static final int ZEKR_TYPE_HAMD = 12;
	public static final int ZEKR_TYPE_TAKBER = 13;
	public static final int ZEKR_TYPE_TAWHED = 14;
	public static final int ZEKR_TYPE_SUBHAN_WBHAMDO = 15;
	public static final int ZEKR_TYPE_HWQALA = 16;
	public static final int ZEKR_TYPE_SYED_EST5FAR = 17;
	public static final int ZEKR_TYPE_SALAH_3NABI = 18;
	public static final int ZEKR_TYPE_EST3FAR = 19;
	public static final int ZEKR_TYPE_SBAH_MASA2 = 20;
	public static final int ZEKR_TYPE_SLEEP = 21;
	public static final int ZEKR_TYPE_DOHA = 22;
	public static final int ZEKR_TYPE_MASA2 = 23;
	public static final int ZEKR_TYPE_VARIED = 24;

	public static final int NOTIFICATION_REPEAT_QUARTER = 101;
	public static final int NOTIFICATION_REPEAT_HALF = 102;
	public static final int NOTIFICATION_REPEAT_HOUR = 103;
	public static final int NOTIFICATION_REPEAT_2HOURS = 104;
	public static final int NOTIFICATION_REPEAT_3HOURS = 105;
	public static final int NOTIFICATION_REPEAT_4HOURS = 106;
	public static final int NOTIFICATION_REPEAT_6HOURS = 107;
	public static final int NOTIFICATION_REPEAT_12HOURS = 108;
	public static final int NOTIFICATION_REPEAT_DAY = 109;

	public static final int EDIT_DIALOG_REQ = 123;
	public static final int DIARY_DIALOG_REQ = 456;
	public static final int RENAME_DIALOG_REQ = 789;
	public static final int DELETE_CONFIRM_DIALOG_REQ = 1011;

	public static final int NOTIFY_TYPE_NOTICATION = 1001;
	public static final int NOTIFY_TYPE_ZEKR_SOUND = 1002;
	public static final int NOTIFY_TYPE_OPEN_APP = 1003;

	public static final int NOTIFICATION_STATUES_ADD = 1004;
	public static final int NOTIFICATION_STATUES_CLEAR = 1005;

	public static String KEY_PREF_LANG = "pref_lang";
	public static String KEY_PREF_WAKE_TIME = "pref_wake_time";
	public static String KEY_PREF_SLEEP_TIME = "pref_sleep_time";
	public static String KEY_PREF_SABAH_TIME = "pref_sabah_azkar";
	public static String KEY_PREF_MASA2_TIME = "pref_masa2_azkar";
	public static String KEY_PREF_DOHAH_TIME = "pref_salah_aldoha";
	public static String KEY_PREF_ABOUT = "pref_about";
	public static String KEY_LEARNED_ADD_ZEKR = "learned_add_zekr_button";
	public static String KEY_LEARNED_ADD_KHATMAH = "learned_add_khatmah_button";
	public static String KEY_PREF_ISSUED_AZKAR = "pref_issued_azkar_notifications";
	public static String KEY_PREF_ISSUED_KHATMAT = "pref_issued_khatmat_notifications";

	public static final int NOTIFICATION_ID_ZEKR = 10000;
	public static final int NOTIFICATION_ID_KHATMA = 100000;
	public static final int NOTIFICATION_ACTION_READ = 101;
	public static final int NOTIFICATION_ACTION_DONE = 102;
	public static final int NOTIFICATION_ACTION_NOT_NOW = 103;

	public static int NOTIFICATION_REQUEST_DHIKR = 2000;
	public static int NOTIFICATION_REQUEST_KHATMAH = 3000;

	public static String ALARM_ID = "alarm_Id";
	public static String ZEKR_TYPE = "Zekr_type";
	public static String ALARM_NOTIFY_TYPE = "notification_type";
	public static String ALARM_REQUEST_CODE = "request_code";
	public static String KHATMAH_TITLE = "khatmah_title";
	public static String KHATMAH_SURA = "khatmah_sura";
	public static String KHATMAH_AYAH = "khatmah_ayah";
	public static String KHATMAH_WERD = "khatmah_werd";
	public static String NOTIFICATION_GROUP_AZKAR = "azkar_group";
	public static String NOTIFICATION_GROUP_KHATMAH = "khatmah_group";
	public static String NOTIFICATION_STATUES = "notification_statues";
	public static String NOTIFICATION_CLEARED = "cleared_notification";
	public static String FILE_NAME_TAG = "name_tage";
	public static String ALARM_CREATOR_CONTEXT = "creator_context";
	public static String TIME_FORMAT = "hh:mm aaa";
	public static String KHATMAH_STATUES = "khatmah_is";
	public static String KHATMAH_STATUES_EDIT = "khatmah_edit";
	public static String KHATMAH_STATUES_NEW = "khatmah_new";
	public static String KHATMAH_EDIT_OBJECT = "khatmah_object";
	public static String QURAN_START_TAG = "page";

	public static void setSubViewsTypeface(Context context, List<TextView> mViews) {
		Typeface fieldsTf = Typeface.createFromAsset(context.getAssets(), "fonts/DroidSansArabic.ttf");

		for (TextView mView : mViews) {
			mView.setTypeface(fieldsTf);
		}
	}

	public static void setHeaderViewsTypeface(Context context, List<TextView> mViews) {

		Typeface titleTf = Typeface.createFromAsset(context.getAssets(), "fonts/DroidNaskh-Regular.ttf");
		for (TextView mView : mViews) {
			mView.setTypeface(titleTf);
		}
	}

	public static String getZekrName(Context context, int id) {
		String zekr;
		switch (id) {
		case ZEKR_TYPE_TASBEH:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[0];
			break;
		case ZEKR_TYPE_HAMD:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[1];
			break;
		case ZEKR_TYPE_TAKBER:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[2];
			break;
		case ZEKR_TYPE_TAWHED:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[3];
			break;
		case ZEKR_TYPE_SUBHAN_WBHAMDO:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[4];
			break;
		case ZEKR_TYPE_HWQALA:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[5];
			break;
		case ZEKR_TYPE_SYED_EST5FAR:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[6];
			break;
		case ZEKR_TYPE_SALAH_3NABI:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[7];
			break;
		case ZEKR_TYPE_SBAH_MASA2:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[10];
			break;
		case ZEKR_TYPE_MASA2:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[10];
			break;
		case ZEKR_TYPE_EST3FAR:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[8];
			break;
		case ZEKR_TYPE_SLEEP:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[11];
			break;
		case ZEKR_TYPE_DOHA:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[12];
			break;
		case ZEKR_TYPE_VARIED:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[9];
			break;
		default:
			zekr = context.getResources().getStringArray(R.array.zekr_ids)[0];
		}
		return zekr;
	}

	public static Uri getZekrSound(Context context, int mId) {
		Uri zekrSound = null;
		switch (mId) {
		case ZEKR_TYPE_TASBEH:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tasbeh);
			break;
		case ZEKR_TYPE_HAMD:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.hamd);
			break;
		case ZEKR_TYPE_TAKBER:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.takber);
			break;
		case ZEKR_TYPE_TAWHED:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tawhed);
			break;
		case ZEKR_TYPE_SUBHAN_WBHAMDO:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.thakelatan);
			break;
		case ZEKR_TYPE_HWQALA:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.lahawl);
			break;
		case ZEKR_TYPE_SYED_EST5FAR:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.youns);
			break;
		case ZEKR_TYPE_SALAH_3NABI:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.salah);
			break;
		case ZEKR_TYPE_SBAH_MASA2:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sabahdhkir);
			break;
		case ZEKR_TYPE_EST3FAR:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.est3far);
			break;
		case ZEKR_TYPE_DOHA:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.doha);
			break;
		case ZEKR_TYPE_MASA2:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.masa2dhikr);
			break;
		case ZEKR_TYPE_SLEEP:
			zekrSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sleepdhikr);
			break;
		default:
			zekrSound = null;
		}
		return zekrSound;
	}

	public static String getZekrReminder(Context context, int notificationRepeat) {
		String reminder = context.getResources().getString(R.string.zekr_reminder);

		switch (notificationRepeat) {
		case Referances.NOTIFICATION_REPEAT_QUARTER:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[0];
			break;
		case Referances.NOTIFICATION_REPEAT_HALF:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[1];
			break;
		case Referances.NOTIFICATION_REPEAT_HOUR:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[2];
			break;
		case Referances.NOTIFICATION_REPEAT_2HOURS:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[3];
			break;
		case Referances.NOTIFICATION_REPEAT_3HOURS:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[4];
			break;
		case Referances.NOTIFICATION_REPEAT_4HOURS:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[5];
			break;
		case Referances.NOTIFICATION_REPEAT_6HOURS:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[6];
			break;
		case Referances.NOTIFICATION_REPEAT_12HOURS:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[7];
			break;
		case Referances.NOTIFICATION_REPEAT_DAY:
			reminder += " " + context.getResources().getStringArray(R.array.notification_repeats)[8];
			break;
		case -1:
			reminder += " " + context.getResources().getString(R.string.no_reminder);
			break;
		}

		return reminder;
	}

	public static long getZekrRepeatInterval(AzkarEntity mObject) {
		long repeatInterval;
		switch (mObject.repeat_id) {
		case Referances.NOTIFICATION_REPEAT_QUARTER:
			repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
			break;
		case Referances.NOTIFICATION_REPEAT_HALF:
			repeatInterval = AlarmManager.INTERVAL_HALF_HOUR;
			break;
		case Referances.NOTIFICATION_REPEAT_HOUR:
			repeatInterval = AlarmManager.INTERVAL_HOUR;
			break;
		case Referances.NOTIFICATION_REPEAT_2HOURS:
			repeatInterval = AlarmManager.INTERVAL_HOUR * 2;
			break;
		case Referances.NOTIFICATION_REPEAT_3HOURS:
			repeatInterval = AlarmManager.INTERVAL_HOUR * 3;
			break;
		case Referances.NOTIFICATION_REPEAT_4HOURS:
			repeatInterval = AlarmManager.INTERVAL_HOUR * 4;
			break;
		case Referances.NOTIFICATION_REPEAT_6HOURS:
			repeatInterval = AlarmManager.INTERVAL_HOUR * 6;
			break;
		case Referances.NOTIFICATION_REPEAT_12HOURS:
			repeatInterval = AlarmManager.INTERVAL_HALF_DAY;
			break;
		case Referances.NOTIFICATION_REPEAT_DAY:
			repeatInterval = AlarmManager.INTERVAL_DAY;
			break;
		default:
			repeatInterval = AlarmManager.INTERVAL_HALF_HOUR;
		}
		return repeatInterval;
	}

	public static String[] ArabtizeArrayDigits(Context contexts, String[] textArray) {
		for (int j = 0; j < textArray.length; j++) {
			textArray[j] = ArabtizeDigits(contexts, textArray[j]);
		}
		return textArray;
	}

	public static String ArabtizeDigits(Context context, String text) {
		char[] ARABIC_NUMS = context.getString(R.string.arabic_numbers).toCharArray();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			if (Character.isDigit(text.charAt(i)) && text.charAt(i) < 58) {
				builder.append(ARABIC_NUMS[(int) (text.charAt(i)) - 48]);
			} else {
				builder.append(text.charAt(i));
			}
		}
		text = builder.toString();
		return text;
	}

	public static String getTimeString(Context context, Date time) {
		String timeString = (String) DateFormat.format(Referances.TIME_FORMAT, time);
		if (SettingsFragment.isArabic(context)) {
			timeString = ArabtizeDigits(context, timeString);
			if (timeString.contains("pm"))
				timeString = timeString.replace("pm", "م");
			else if (timeString.contains("am"))
				timeString = timeString.replace("am", "ص");
		}
		return timeString;
	}

	public static void removeZekrNotification(SharedPreferences mPref, Integer removeTag) {
		String azkarNotifications = mPref.getString(Referances.KEY_PREF_ISSUED_AZKAR, null);
		if (azkarNotifications == null) {
			return;
		}
		Gson mGson = new Gson();
		Type listType = new TypeToken<List<Integer>>() {
		}.getType();
		List<Integer> notificationsTags = mGson.fromJson(azkarNotifications, listType);
		notificationsTags.remove(removeTag);
		azkarNotifications = mGson.toJson(notificationsTags);
		mPref.edit().putString(Referances.KEY_PREF_ISSUED_AZKAR, azkarNotifications).commit();

	}

	public static void removeKhatmahNotification(SharedPreferences mPref, Integer removeTag) {
		String khatmatNotifications = mPref.getString(Referances.KEY_PREF_ISSUED_KHATMAT, null);
		if (khatmatNotifications == null) {
			return;
		}
		Gson mGson = new Gson();
		Type listType = new TypeToken<List<Integer>>() {
		}.getType();
		List<Integer> notificationsTags = mGson.fromJson(khatmatNotifications, listType);
		notificationsTags.remove(removeTag);
		khatmatNotifications = mGson.toJson(notificationsTags);
		mPref.edit().putString(Referances.KEY_PREF_ISSUED_KHATMAT, khatmatNotifications).commit();
	}

}
