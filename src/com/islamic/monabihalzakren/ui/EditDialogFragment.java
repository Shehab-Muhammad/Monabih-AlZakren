package com.islamic.monabihalzakren.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.ui.widgets.ArabicSpinnerAdapter;

public class EditDialogFragment extends DialogFragment {

	private AzkarEntity zekrObject;
	private Spinner notifyTypeSpinner, notifyRepeatSpinner;
	private TextView notifyRepeatTv, notifyTypeTv;
	private SwitchCompat notifySwitch;

	public EditDialogFragment(AzkarEntity mZekr) {
		zekrObject = mZekr;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater mInflater = getActivity().getLayoutInflater();
		AlertDialog.Builder mEditBuilder = new AlertDialog.Builder(getActivity());
		View dialogView = mInflater.inflate(R.layout.edit_zekr_dialog, null);
		notifyRepeatSpinner = (Spinner) dialogView.findViewById(R.id.edit_repeat_every);
		notifyTypeSpinner = (Spinner) dialogView.findViewById(R.id.edit_notification_type);
		notifySwitch = (SwitchCompat) dialogView.findViewById(R.id.edit_zekr_notify_switch);
		notifyRepeatTv = (TextView) dialogView.findViewById(R.id.edit_zekr_repeat_every);
		notifyTypeTv = (TextView) dialogView.findViewById(R.id.edit_notify_type);
		RelativeLayout rRaw = (RelativeLayout) dialogView.findViewById(R.id.raw2_zekr_edit);
		int notifyRepeatPos, notifyTypePos;

		List<String> notificationsList = new ArrayList(Arrays.asList(getResources().getStringArray(
				R.array.notification_types)));

		if (zekrObject.zekr_id < Referances.ZEKR_TYPE_SBAH_MASA2 || zekrObject.zekr_id == Referances.ZEKR_TYPE_DOHA) {
			notificationsList.remove(2);
		}
		if (zekrObject.zekr_id >= Referances.ZEKR_TYPE_SBAH_MASA2) {
			ViewGroup parent = (ViewGroup) rRaw.getParent();
			if (parent != null) {
				parent.removeView(rRaw);
				// RelativeLayout.LayoutParams p = (LayoutParams) nRaw
				// .getLayoutParams();
				// p.addRule(RelativeLayout.BELOW, R.id.raw1_zekr_edit);
				// nRaw.setLayoutParams(p);
			}
		}

		ArrayAdapter<String> repeatSpinnerAdapter = null, typeSpinnerAdapter = null;
		if (!SettingsFragment.isArabic(getActivity())) {

			repeatSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,
					Arrays.asList(getResources().getStringArray(R.array.notification_repeats)));
			typeSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, notificationsList);
		} else {

			repeatSpinnerAdapter = new ArabicSpinnerAdapter(getActivity(), R.layout.spinner_item,
					Arrays.asList(getResources().getStringArray(R.array.notification_repeats)));
			typeSpinnerAdapter = new ArabicSpinnerAdapter(getActivity(), R.layout.spinner_item, notificationsList);
		}

		repeatSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		notifyRepeatSpinner.setAdapter(repeatSpinnerAdapter);
		notifyTypeSpinner.setAdapter(typeSpinnerAdapter);

		switch (zekrObject.repeat_id) {
		case Referances.NOTIFICATION_REPEAT_QUARTER:
			notifyRepeatPos = 0;
			break;
		case Referances.NOTIFICATION_REPEAT_HALF:
			notifyRepeatPos = 1;
			break;
		case Referances.NOTIFICATION_REPEAT_HOUR:
			notifyRepeatPos = 2;
			break;
		case Referances.NOTIFICATION_REPEAT_2HOURS:
			notifyRepeatPos = 3;
			break;
		case Referances.NOTIFICATION_REPEAT_3HOURS:
			notifyRepeatPos = 4;
			break;
		case Referances.NOTIFICATION_REPEAT_4HOURS:
			notifyRepeatPos = 5;
			break;
		case Referances.NOTIFICATION_REPEAT_6HOURS:
			notifyRepeatPos = 6;
			break;
		case Referances.NOTIFICATION_REPEAT_12HOURS:
			notifyRepeatPos = 7;
			break;
		case Referances.NOTIFICATION_REPEAT_DAY:
			notifyRepeatPos = 8;
			break;
		default:
			notifyRepeatPos = 0;
		}

		switch (zekrObject.notify_type) {
		case Referances.NOTIFY_TYPE_NOTICATION:
			notifyTypePos = 1;
			break;
		case Referances.NOTIFY_TYPE_ZEKR_SOUND:
			notifyTypePos = 0;
			break;
		case Referances.NOTIFY_TYPE_OPEN_APP:
			notifyTypePos = 2;
			break;
		default:
			notifyTypePos = 0;
		}

		notifyRepeatSpinner.setSelection(notifyRepeatPos);
		notifyTypeSpinner.setSelection(notifyTypePos);

		notifySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				notifyRepeatSpinner.setEnabled(isChecked);
				notifyTypeSpinner.setEnabled(isChecked);
				notifyRepeatSpinner.setClickable(isChecked);
				notifyTypeSpinner.setClickable(isChecked);
				notifyTypeTv.setEnabled(isChecked);
				notifyRepeatTv.setEnabled(isChecked);
			}
		});

		mEditBuilder.setTitle(getResources().getString(R.string.zekr_edit_title));
		mEditBuilder.setView(dialogView);

		mEditBuilder.setPositiveButton(R.string.menu_edit, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (!notifySwitch.isChecked()) {
					zekrObject.repeat_id = zekrObject.notify_type = -1;
				} else {
					zekrObject.repeat_id = NewZekrActivity.getObjectRepeatId(notifyRepeatSpinner
							.getSelectedItemPosition());
					zekrObject.notify_type = NewZekrActivity.getObjectRepeatType(notifyTypeSpinner
							.getSelectedItemPosition());
				}
				try {
					if (zekrObject.zekr_id == Referances.ZEKR_TYPE_SBAH_MASA2) {
						AzkarEntity msa2 = DataContext.getZekr(getActivity(),
								DataContext.AZKAR_FILES_NAME_TAG + String.valueOf(zekrObject.tag + 1) + ".json");
						msa2.repeat_id = zekrObject.repeat_id;
						msa2.notify_type = zekrObject.notify_type;
						DataContext.editZekr(getActivity(), msa2);
					}
					DataContext.editZekr(getActivity(), zekrObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
						getActivity().getIntent());
			}
		});

		mEditBuilder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		return mEditBuilder.create();
	}

}
