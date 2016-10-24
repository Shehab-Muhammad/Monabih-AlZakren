package com.islamic.monabihalzakren.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.islamic.monabihalzakren.background.AlarmReceiver;
import com.islamic.monabihalzakren.R;

public class DataContext {

	private static final String STORAGE_PATH_AZKAR = "/MonabihData/Azkar";
	public static final String AZKAR_FILES_NAME_TAG = "zekr_";
	public static final String KHATMAT_FILES_NAME_TAG = "khatmah_";

	public static int writeZekr(Context context, AzkarEntity mObject) throws IOException {
		String filename = AZKAR_FILES_NAME_TAG + String.valueOf(mObject.tag) + ".json";
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File dir = new File(path);
		Gson gson = new Gson();
		String s = gson.toJson(mObject);
		if (mObject.notify_type != -1) {
			AlarmReceiver mAlarm = new AlarmReceiver();
			mAlarm.setZekrAlarm(context, mObject);
		}

		FileOutputStream outputStream;

		try {
			dir.mkdirs();
			File mFile = new File(path, filename);
			mFile.createNewFile();
			outputStream = new FileOutputStream(mFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
			myOutWriter.append(s);
			myOutWriter.close();
			outputStream.close();

			return mObject.tag;
		} catch (IOException e) {
			throw e;
		}
	}

	public static int writeKhatmah(Context context, KhatmahEntity mObject) throws IOException {
		String filename = "khatmah_" + String.valueOf(mObject.tag) + ".json";
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File dir = new File(path);
		Gson gson = new Gson();
		String s = gson.toJson(mObject);
		if (mObject.remind) {
			AlarmReceiver mAlarm = new AlarmReceiver();
			mAlarm.setKhatmahAlarm(context, mObject);
		}

		FileOutputStream outputStream;

		try {
			dir.mkdirs();
			File mFile = new File(path, filename);
			mFile.createNewFile();
			outputStream = new FileOutputStream(mFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
			myOutWriter.append(s);
			myOutWriter.close();
			outputStream.close();

			return mObject.tag;
		} catch (IOException e) {
			throw e;
		}
	}

	public static List<AzkarEntity> getAllAzkar(Context context, boolean getAll, boolean getNotifyOnly) {
		List<AzkarEntity> azkar = new ArrayList<AzkarEntity>();
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File directory = new File(path);
		directory.mkdirs();
		String[] filesNames = directory.list();
		AzkarEntity temp = null;
		for (String fileName : filesNames) {
			if (fileName.startsWith(AZKAR_FILES_NAME_TAG) && fileName.endsWith(".json")) {
				AzkarEntity zekrFound = getZekr(context, fileName);
				if (getNotifyOnly && zekrFound.start_time == null)
					continue;
				if (getAll) {
					azkar.add(zekrFound);
					continue;
				}
				if (zekrFound.zekr_id == Referances.ZEKR_TYPE_SBAH_MASA2)
					temp = zekrFound;
				else if (zekrFound.zekr_id == Referances.ZEKR_TYPE_MASA2) {
					if (temp.compareTo(zekrFound) > 0)
						azkar.add(zekrFound);
					else
						azkar.add(temp);
				} else
					azkar.add(zekrFound);
			}
		}
		return azkar;
	}

	public static AzkarEntity getZekr(Context context, String fileName) {
		AzkarEntity mZekr = null;
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File mFile = new File(path, fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(mFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			String json = sb.toString();
			Gson gsonR = new Gson();
			mZekr = gsonR.fromJson(json, AzkarEntity.class);
			bufferedReader.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mZekr;
	}

	public static List<KhatmahEntity> getAllKhatmat(Context context, boolean getNotifyOnly) {
		List<KhatmahEntity> khatmat = new ArrayList<KhatmahEntity>();
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File directory = new File(path);
		directory.mkdirs();
		String[] filesNames = directory.list();
		for (String fileName : filesNames) {
			if (fileName.startsWith(KHATMAT_FILES_NAME_TAG) && fileName.endsWith(".json")) {
				KhatmahEntity zekrFound = getKhatmah(context, fileName);
				if (getNotifyOnly && !zekrFound.remind)
					continue;
				khatmat.add(zekrFound);
			}
		}
		return khatmat;
	}

	public static KhatmahEntity getKhatmah(Context context, String fileName) {
		KhatmahEntity mKhatmah = null;
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File mFile = new File(path, fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(mFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			String json = sb.toString();
			Gson gsonR = new Gson();
			mKhatmah = gsonR.fromJson(json, KhatmahEntity.class);
			if (mKhatmah.remind == false)
				mKhatmah.name = context.getString(R.string.khatmah_name) + " " + ((Integer) (mKhatmah.tag)).toString();
			bufferedReader.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mKhatmah;
	}

	public static boolean removeZekr(Context context, AzkarEntity mZekr, boolean removeNext) {
		String fileName = AZKAR_FILES_NAME_TAG + String.valueOf(mZekr.tag) + ".json";
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File deleteFile = new File(path, fileName);
		boolean deleted = deleteFile.delete();
		if (deleted) {
			AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
			alarmIntent.putExtra(Referances.NOTIFICATION_STATUES, Referances.NOTIFICATION_STATUES_ADD);
			alarmIntent.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ID_ZEKR);
			alarmIntent.putExtra(Referances.ALARM_NOTIFY_TYPE, mZekr.notify_type);
			alarmIntent.putExtra(Referances.ALARM_REQUEST_CODE, mZekr.req_code);
			alarmIntent.putExtra(Referances.FILE_NAME_TAG, mZekr.tag);
			alarmIntent.putExtra(Referances.ZEKR_TYPE, mZekr.zekr_id);
			PendingIntent ASPIntent = PendingIntent.getBroadcast(context.getApplicationContext(), mZekr.req_code,
					alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			aManager.cancel(ASPIntent);
			if (removeNext) {
				fileName = AZKAR_FILES_NAME_TAG + String.valueOf(mZekr.tag + 1) + ".json";
				removeZekr(context, getZekr(context, fileName), false);
			}
		}
		return deleted;
	}

	public static boolean removeKhatmah(Context context, KhatmahEntity mKhatmah) {
		String fileName = KHATMAT_FILES_NAME_TAG + String.valueOf(mKhatmah.tag) + ".json";
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File deleteFile = new File(path, fileName);
		boolean deleted = deleteFile.delete();
		if (deleted && mKhatmah.remind) {
			AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
			alarmIntent.putExtra(Referances.NOTIFICATION_STATUES, Referances.NOTIFICATION_STATUES_ADD);
			alarmIntent.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ID_KHATMA);
			alarmIntent.putExtra(Referances.ALARM_REQUEST_CODE, mKhatmah.req_code);
			alarmIntent.putExtra(Referances.FILE_NAME_TAG, mKhatmah.tag);
			alarmIntent.putExtra(Referances.KHATMAH_TITLE, mKhatmah.name);
			alarmIntent.putExtra(Referances.KHATMAH_WERD, mKhatmah.werd_page);
			alarmIntent.putExtra(Referances.KHATMAH_SURA, mKhatmah.sura);
			alarmIntent.putExtra(Referances.KHATMAH_AYAH, mKhatmah.ayah);

			PendingIntent ASPIntent = PendingIntent.getBroadcast(context.getApplicationContext(), mKhatmah.req_code,
					alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			aManager.cancel(ASPIntent);
		}
		return deleted;
	}

	public static void editZekr(Context context, AzkarEntity mObject) throws IOException {
		if (removeZekr(context, mObject, false)) {
			mObject.req_code += 500;
			writeZekr(context, mObject);
		}
	}

	public static void updateKhatmah(Context context, int tag) throws IOException {
		String filename = KHATMAT_FILES_NAME_TAG + String.valueOf(tag) + ".json";
		KhatmahEntity mObject = getKhatmah(context, filename);

		if (removeKhatmah(context, mObject)) {
			mObject.update();
			mObject.req_code += 500;
			int today = (new Date()).getDate();
			if (today == mObject.getRemindTime().getDate())
				mObject.remind_time.setDate(today + 1);
			writeKhatmah(context, mObject);
		}
	}

	public static void editKhatmah(Context context, KhatmahEntity mObject) {
		if (removeKhatmah(context, mObject)) {
			mObject.req_code += 500;
			try {
				writeKhatmah(context, mObject);
				Toast.makeText(context, context.getString(R.string.khatmah_edit_done), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int lastZekrTag(Context context) {
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File directory = new File(path);
		directory.mkdirs();
		String[] filesNames = directory.list();
		String tag = "0";
		for (String fileName : filesNames) {
			if (fileName.startsWith(AZKAR_FILES_NAME_TAG) && fileName.endsWith(".json")) {
				tag = (String) fileName.subSequence(5, fileName.length() - 5);
			}
		}
		return Integer.parseInt(tag);
	}

	public static int lastKhatmahTag(Context context) {
		String path = context.getExternalFilesDir(null).getPath() + "/MonabihData";
		File directory = new File(path);
		directory.mkdirs();
		String[] filesNames = directory.list();
		String tag = "0";
		for (String fileName : filesNames) {
			if (fileName.startsWith(KHATMAT_FILES_NAME_TAG) && fileName.endsWith(".json")) {
				tag = (String) fileName.subSequence(8, fileName.length() - 5);
			}
		}
		return Integer.parseInt(tag);
	}

}
