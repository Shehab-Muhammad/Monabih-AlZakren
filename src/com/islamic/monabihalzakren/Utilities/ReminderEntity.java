package com.islamic.monabihalzakren.Utilities;

import java.util.Date;

public abstract class ReminderEntity implements Comparable<ReminderEntity> {

	public Date mTime = null;

	abstract public Date getRemindTime();

	@Override
	public int compareTo(ReminderEntity another) {
		if (getRemindTime() == null || another.getRemindTime() == null)
			return 0;
		return this.mTime.compareTo(another.mTime);
	}

}
