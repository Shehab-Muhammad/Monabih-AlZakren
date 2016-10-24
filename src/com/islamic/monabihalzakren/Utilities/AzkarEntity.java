package com.islamic.monabihalzakren.Utilities;

import java.util.Date;

public class AzkarEntity extends ReminderEntity {

	public int zekr_id, tag, repeat_id, notify_type, req_code;
	public Date start_time;

	public AzkarEntity(int id, Date sTime, int re_id, int nt, int fileTag, int rq) {
		zekr_id = id;
		start_time = mTime = sTime;
		repeat_id = re_id;
		notify_type = nt;
		tag = fileTag;
		req_code = rq;
	}

	@Override
	public Date getRemindTime() {

		if (start_time == null)
			return null;
		Date now = new Date();
		int comp = now.compareTo(start_time);

		while (comp > 0) {
			comp = now.compareTo(getNextRemindTime());
		}
		mTime = start_time;
		return start_time;
	}

	public Date getNextRemindTime() {
		long repeatInterval = Referances.getZekrRepeatInterval(this);
		long startAlarm = start_time.getTime();
		startAlarm += repeatInterval;
		start_time = new Date(startAlarm);
		return start_time;
	}

}
