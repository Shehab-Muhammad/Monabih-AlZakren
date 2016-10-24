package com.islamic.monabihalzakren.background;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.ui.SettingsFragment;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.ui.PagerActivity;

public class AlarmReceiver extends WakefulBroadcastReceiver {
	private AlarmManager aManager;
	private PendingIntent ASPIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
		switch (intent.getIntExtra(Referances.NOTIFICATION_CLEARED, -1)) {
		case Referances.NOTIFICATION_ID_ZEKR: {
			Referances.removeZekrNotification(mPref, intent.getIntExtra(Referances.FILE_NAME_TAG, -1));
			return;
		}
		case Referances.NOTIFICATION_ID_KHATMA: {
			Referances.removeKhatmahNotification(mPref, intent.getIntExtra(Referances.FILE_NAME_TAG, -1));
			return;
		}
		}

		switch (intent.getIntExtra(Referances.ALARM_ID, -1)) {
		case Referances.NOTIFICATION_ID_ZEKR:
			Date currentTime = new Date();
			Date wakeTime = SettingsFragment.getWakeTime(context);
			Date sleepTime = SettingsFragment.getSleepTime(context);
			int wakeCompare = wakeTime.compareTo(currentTime);
			int sleepCompare = sleepTime.compareTo(currentTime);
			if ((wakeCompare > 0) && (sleepCompare < 0)) {
				return;
			}
			break;
		case Referances.NOTIFICATION_ACTION_DONE:
			try {
				DataContext.updateKhatmah(context, intent.getIntExtra(Referances.FILE_NAME_TAG, -1));
				int id = intent.getIntExtra(Referances.ALARM_REQUEST_CODE, 0);
				cancelNotification(context, id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		case Referances.NOTIFICATION_ACTION_READ:
			Intent quran = new Intent(context, PagerActivity.class);
			int sura = intent.getIntExtra(Referances.KHATMAH_SURA, 1);
			int ayah = intent.getIntExtra(Referances.KHATMAH_AYAH, 1);
			int page = QuranInfo.getPageFromSuraAyah(sura, ayah);
			quran.putExtra("page", page);
			quran.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			int id = intent.getIntExtra(Referances.ALARM_REQUEST_CODE, 0);
			cancelNotification(context, id);
			context.startActivity(quran);
			return;
		}

		Intent serviceIntent = new Intent(context, AlarmReceiverService.class);
		serviceIntent.putExtras(intent);
		startWakefulService(context, serviceIntent);
		// Toast.makeText(context, "Alarm worked", Toast.LENGTH_LONG).show();
	}

	public void cancelNotification(Context context, int code) {
		NotificationManager alarmNotificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		alarmNotificationManager.cancel(code);
		Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		context.sendBroadcast(it);
	}

	public void setZekrAlarm(Context context, AzkarEntity mZekr) {
		aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
		alarmIntent.putExtra(Referances.NOTIFICATION_STATUES, Referances.NOTIFICATION_STATUES_ADD);
		alarmIntent.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ID_ZEKR);
		alarmIntent.putExtra(Referances.ALARM_NOTIFY_TYPE, mZekr.notify_type);
		alarmIntent.putExtra(Referances.ALARM_REQUEST_CODE, mZekr.req_code);
		alarmIntent.putExtra(Referances.FILE_NAME_TAG, mZekr.tag);
		alarmIntent.putExtra(Referances.ZEKR_TYPE, mZekr.zekr_id);
		ASPIntent = PendingIntent.getBroadcast(context.getApplicationContext(), mZekr.req_code, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		long repeatInterval = Referances.getZekrRepeatInterval(mZekr);

		long startAlarm = mZekr.getRemindTime().getTime();

		aManager.setRepeating(AlarmManager.RTC_WAKEUP, startAlarm, repeatInterval, ASPIntent);
	}

	public void setKhatmahAlarm(Context context, KhatmahEntity mKhatmah) {
		aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
		alarmIntent.putExtra(Referances.NOTIFICATION_STATUES, Referances.NOTIFICATION_STATUES_ADD);
		alarmIntent.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ID_KHATMA);
		alarmIntent.putExtra(Referances.ALARM_REQUEST_CODE, mKhatmah.req_code);
		alarmIntent.putExtra(Referances.FILE_NAME_TAG, mKhatmah.tag);
		alarmIntent.putExtra(Referances.KHATMAH_TITLE, mKhatmah.name);
		alarmIntent.putExtra(Referances.KHATMAH_WERD, mKhatmah.werd_page);
		alarmIntent.putExtra(Referances.KHATMAH_SURA, mKhatmah.sura);
		alarmIntent.putExtra(Referances.KHATMAH_AYAH, mKhatmah.ayah);

		ASPIntent = PendingIntent.getBroadcast(context.getApplicationContext(), mKhatmah.req_code, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		aManager.setRepeating(AlarmManager.RTC_WAKEUP, mKhatmah.getRemindTime().getTime(), AlarmManager.INTERVAL_DAY,
				ASPIntent);
	}

	public void delAlarm(Context context) {
		if (aManager != null)
			aManager.cancel(ASPIntent);
	}

}
