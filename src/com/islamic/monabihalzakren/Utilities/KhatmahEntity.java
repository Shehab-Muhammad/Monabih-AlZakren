package com.islamic.monabihalzakren.Utilities;

import java.util.Date;

import com.quran.labs.androidquran.data.QuranInfo;

public class KhatmahEntity extends ReminderEntity {

	public int sura, ayah, werd_page, tag, req_code = -1;
	public boolean remind, autoName;
	public String name = null;
	public Date remind_time = null;

	public KhatmahEntity(int startSura, int startAyah, int werd, int khatmahTag, Date notifyTime, String khatmahName,
			int reqCode) {
		sura = startSura;
		ayah = startAyah;
		werd_page = werd;
		tag = khatmahTag;
		name = khatmahName;
		remind = true;
		req_code = reqCode;
		remind_time = mTime = notifyTime;
	}

	public KhatmahEntity(int startSura, int startAyah, int werd, int khatmahTag, String khatmahName) {
		sura = startSura;
		ayah = startAyah;
		werd_page = werd;
		tag = khatmahTag;
		name = khatmahName;
		remind = false;
	}

	public void update() {
		int currentPage = QuranInfo.getPageFromSuraAyah(sura, ayah);
		int newPage = currentPage + werd_page;
		ayah = QuranInfo.PAGE_AYAH_START[newPage - 1];
		sura = QuranInfo.getSuraNumberFromPage(newPage);
		getRemindTime();
	}

	public int[] nextWerd() {
		int[] suraAyah = new int[2];
		int currentPage = QuranInfo.getPageFromSuraAyah(sura, ayah);
		int newPage = currentPage + werd_page;
		suraAyah[0] = QuranInfo.getSuraNumberFromPage(newPage);
		suraAyah[1] = QuranInfo.PAGE_AYAH_START[newPage - 1];
		return suraAyah;
	}

	@Override
	public Date getRemindTime() {
		if (!remind)
			return null;
		Date now = new Date();
		while (now.compareTo(remind_time) > 0) {
			remind_time.setDate(remind_time.getDate() + 1);
		}
		int day = remind_time.getDate();
		mTime = remind_time;
		return remind_time;
	}

	public int getStartPage() {
		return QuranInfo.getPageFromSuraAyah(sura, ayah);
	}

}
