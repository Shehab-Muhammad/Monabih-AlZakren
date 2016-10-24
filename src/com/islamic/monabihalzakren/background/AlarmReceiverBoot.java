package com.islamic.monabihalzakren.background;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;

public class AlarmReceiverBoot extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w("boot_broadcast_poc", "starting service...");
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			List<AzkarEntity> azkar = DataContext.getAllAzkar(context, true, true);
			List<KhatmahEntity> khatamat = DataContext.getAllKhatmat(context, true);
			for (AzkarEntity mZekr : azkar) {
				AlarmReceiver zekrAlarm = new AlarmReceiver();
				mZekr.getRemindTime();
				zekrAlarm.setZekrAlarm(context.getApplicationContext(), mZekr);
			}
			for (KhatmahEntity mKhatmah : khatamat) {
				if (mKhatmah.remind) {
					AlarmReceiver khatmahAlarm = new AlarmReceiver();
					khatmahAlarm.setKhatmahAlarm(context.getApplicationContext(), mKhatmah);
				}
			}
		}
	}

}
