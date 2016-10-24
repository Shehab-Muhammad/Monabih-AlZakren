package com.islamic.monabihalzakren.background;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.InboxStyle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.ui.AzkarScreenActivity;
import com.islamic.monabihalzakren.ui.MonabihActivity;
import com.islamic.monabihalzakren.ui.PlaceholderFragment;
import com.quran.labs.androidquran.data.QuranInfo;

public class AlarmReceiverService extends IntentService {
	private SharedPreferences mPrefs;

	public AlarmReceiverService() {
		super("AlarmService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		NotificationManager alarmNotificationManager = (NotificationManager) this
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_notification);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Bitmap largeIcon = null;
		String title = "", message = "";
		int notificationId = intent.getIntExtra(Referances.ALARM_ID, 0);
		int notifyType = intent.getIntExtra(Referances.ALARM_NOTIFY_TYPE, 0);
		int nameTag = intent.getIntExtra(Referances.FILE_NAME_TAG, -1);
		int requestId = 0;
		int zekrId = 0;
		Intent resultIntent = null;
		Intent clearNotifyIntent = new Intent(this, AlarmReceiver.class);
		clearNotifyIntent.putExtra(Referances.NOTIFICATION_STATUES, Referances.NOTIFICATION_STATUES_CLEAR);
		if (notificationId == Referances.NOTIFICATION_ID_ZEKR) {
			zekrId = intent.getIntExtra(Referances.ZEKR_TYPE, 0);
			if (zekrId == Referances.ZEKR_TYPE_VARIED) {
				zekrId = (int) (Math.random() * (Referances.ZEKR_TYPE_EST3FAR - Referances.ZEKR_TYPE_TASBEH) + Referances.ZEKR_TYPE_TASBEH);
			}
			if (notifyType == Referances.NOTIFY_TYPE_ZEKR_SOUND) {
				mBuilder.setSound(Referances.getZekrSound(this, zekrId));
			} else if (notifyType == Referances.NOTIFY_TYPE_OPEN_APP) {
				Intent azkarScreen = new Intent(getApplicationContext(), AzkarScreenActivity.class);
				azkarScreen.putExtra(Referances.ZEKR_TYPE, zekrId);
				azkarScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(azkarScreen);
				AlarmReceiver.completeWakefulIntent(intent);
				return;
			} else
				mBuilder.setDefaults(Notification.DEFAULT_ALL);

			if (zekrId == Referances.ZEKR_TYPE_MASA2 || zekrId == Referances.ZEKR_TYPE_SBAH_MASA2
					|| zekrId == Referances.ZEKR_TYPE_SLEEP) {
				resultIntent = new Intent(this, AzkarScreenActivity.class);
				resultIntent.putExtra(Referances.ZEKR_TYPE, zekrId);
			} else {
				resultIntent = new Intent(this, MonabihActivity.class);
				resultIntent.putExtra(PlaceholderFragment.ARG_SECTION_NUMBER, PlaceholderFragment.AZKAR_SECTION);
			}

			largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_azkar);
			title = getResources().getString(R.string.notification_title_azkar);
			message = getResources().getString(R.string.notification_message_azkar)
					+ Referances.getZekrName(this, zekrId);
			requestId = Referances.NOTIFICATION_REQUEST_DHIKR;
			mBuilder.setGroup(Referances.NOTIFICATION_GROUP_AZKAR);
			clearNotifyIntent.putExtra(Referances.NOTIFICATION_CLEARED, Referances.NOTIFICATION_ID_ZEKR);
			String notificationsJson = mPrefs.getString(Referances.KEY_PREF_ISSUED_AZKAR, null);
			InboxStyle mInbox = buildSummary(notificationId, nameTag, notificationsJson);
			if (mInbox != null)
				mBuilder.setStyle(mInbox);
			else {
				List<Integer> tages = new ArrayList<Integer>();
				tages.add(nameTag);
				String tagesS = (new Gson()).toJson(tages);
				mPrefs.edit().putString(Referances.KEY_PREF_ISSUED_AZKAR, tagesS).commit();
			}

		} else if (notificationId == Referances.NOTIFICATION_ID_KHATMA) {
			int sura = intent.getIntExtra(Referances.KHATMAH_SURA, 1);
			int ayah = intent.getIntExtra(Referances.KHATMAH_AYAH, 1);
			int werd = intent.getIntExtra(Referances.KHATMAH_WERD, 1);
			title = intent.getStringExtra(Referances.KHATMAH_TITLE);
			largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_quran);
			message = getString(R.string.notification_message_khatmah, werd, ayah,
					QuranInfo.getSuraName(this, sura, false));
			String notificationsJson = mPrefs.getString(Referances.KEY_PREF_ISSUED_KHATMAT, null);
			InboxStyle mInbox = buildSummary(notificationId, nameTag, notificationsJson);
			if (mInbox != null)
				mBuilder.setStyle(mInbox);
			else {
				List<Integer> tages = new ArrayList<Integer>();
				tages.add(nameTag);
				String tagesS = (new Gson()).toJson(tages);
				mPrefs.edit().putString(Referances.KEY_PREF_ISSUED_KHATMAT, tagesS).commit();
				mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

				Intent readIntent = new Intent(this, AlarmReceiver.class);
				readIntent.putExtras(intent);
				readIntent.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ACTION_READ);
				PendingIntent piReadAction = PendingIntent.getBroadcast(this, Referances.NOTIFICATION_ACTION_READ,
						readIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				Intent doneIntent = new Intent(this, AlarmReceiver.class);
				doneIntent.putExtras(intent);
				doneIntent.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ACTION_DONE);
				PendingIntent piDoneAction = PendingIntent.getBroadcast(this, Referances.NOTIFICATION_ACTION_DONE,
						doneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				mBuilder.addAction(android.R.drawable.ic_menu_view, getString(R.string.notification_action_read),
						piReadAction);
				mBuilder.addAction(R.drawable.ic_checkmark_holo_light, getString(R.string.notification_action_done),
						piDoneAction);
			}
			mBuilder.setDefaults(Notification.DEFAULT_ALL);
			requestId = Referances.NOTIFICATION_REQUEST_KHATMAH;
			resultIntent = new Intent(this, MonabihActivity.class);
			resultIntent.putExtra(PlaceholderFragment.ARG_SECTION_NUMBER, PlaceholderFragment.KHATMA_SECTION);
			clearNotifyIntent.putExtra(Referances.NOTIFICATION_CLEARED, Referances.NOTIFICATION_ID_KHATMA);

		}

		PendingIntent nPI = PendingIntent.getActivity(this, requestId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(nPI);
		mBuilder.setLargeIcon(largeIcon);
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(message);
		mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

		mBuilder.setAutoCancel(true);
		clearNotifyIntent.putExtra(Referances.FILE_NAME_TAG, nameTag);
		PendingIntent mDeleteIntent = PendingIntent.getBroadcast(this, requestId, clearNotifyIntent, 0);
		mBuilder.setDeleteIntent(mDeleteIntent);
		alarmNotificationManager.notify(requestId, mBuilder.build());

		AlarmReceiver.completeWakefulIntent(intent);
	}

	private NotificationCompat.InboxStyle buildSummary(int notificationId, int newTag, String notificationsJson) {
		InboxStyle mStyle = new InboxStyle();
		Type listType = new TypeToken<List<Integer>>() {
		}.getType();
		Gson readWrite = new Gson();
		List<Integer> mNotificationTags = readWrite.fromJson(notificationsJson, listType);
		int summaryCount = 0;
		if (mNotificationTags == null)
			return null;
		if (mNotificationTags.contains(newTag))
			mNotificationTags.remove((Integer) newTag);
		mNotificationTags.add(newTag);
		notificationsJson = readWrite.toJson(mNotificationTags);
		if (notificationId == Referances.NOTIFICATION_ID_ZEKR) {
			List<AzkarEntity> allAzkar = DataContext.getAllAzkar(this, false, true);
			for (AzkarEntity mZekr : allAzkar) {
				if (mNotificationTags.contains(mZekr.tag)) {
					String mLine = getResources().getString(R.string.notification_message_azkar) + " "
							+ Referances.getZekrName(this, mZekr.zekr_id);
					mStyle.addLine(mLine);
					summaryCount++;
				}
			}
			String type = getResources().getStringArray(R.array.menu1)[1];
			String title = getString(R.string.azkar);
			mStyle.setBigContentTitle(getString(R.string.summary_title, summaryCount, type));
			mStyle.setSummaryText(getString(R.string.summary_text, title));
			mPrefs.edit().putString(Referances.KEY_PREF_ISSUED_AZKAR, notificationsJson).commit();
		} else if (notificationId == Referances.NOTIFICATION_ID_KHATMA) {
			List<KhatmahEntity> allKhatmat = DataContext.getAllKhatmat(this, true);
			for (KhatmahEntity mKhatmah : allKhatmat) {
				if (mNotificationTags.contains(mKhatmah.tag)) {
					String mLine = getString(R.string.notification_message_khatmah, mKhatmah.werd_page, mKhatmah.ayah,
							QuranInfo.getSuraName(this, mKhatmah.sura, false));
					mStyle.addLine(mLine);
					summaryCount++;
				}
			}
			String type = getString(R.string.khatmah);
			String title = getString(R.string.khatmat);
			mStyle.setBigContentTitle(getString(R.string.summary_title, summaryCount, type));
			mStyle.setSummaryText(getString(R.string.summary_text, title));
			mPrefs.edit().putString(Referances.KEY_PREF_ISSUED_KHATMAT, notificationsJson).commit();
		}

		return mStyle;
	}
}
