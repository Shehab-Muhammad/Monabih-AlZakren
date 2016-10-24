package com.islamic.monabihalzakren.ui;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.Utilities.ReminderEntity;
import com.islamic.monabihalzakren.R;
import com.quran.labs.androidquran.ui.PagerActivity;

public class DiaryDialogFrament extends DialogFragment {

	private ReminderEntity entityObject;
	private Fragment parent;

	public DiaryDialogFrament(ReminderEntity selectedObject, Fragment mFragment) {
		this.entityObject = selectedObject;
		this.parent = mFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder diaryBuilder = new AlertDialog.Builder(getActivity());
		String mTitle;
		if (entityObject instanceof AzkarEntity) {
			final AzkarEntity zekr = (AzkarEntity) entityObject;
			mTitle = Referances.getZekrName(getActivity(), zekr.zekr_id);
			diaryBuilder.setTitle(mTitle);
			diaryBuilder.setItems(R.array.zekr_dairy_actions, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						int today = (new Date()).getDate();
						while (zekr.start_time.getDate() == today) {
							zekr.getNextRemindTime();
						}
						try {
							DataContext.editZekr(getActivity(), zekr);
							getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
									getActivity().getIntent());
						} catch (IOException e) {
							e.printStackTrace();
						}
						return;

					case 1:
						EditDialogFragment editFragment = new EditDialogFragment(zekr);
						editFragment.setTargetFragment(parent, Referances.EDIT_DIALOG_REQ);
						editFragment.show(parent.getFragmentManager(), "Edit dialog");
						return;

					case 2:
						boolean isSabah = (zekr.zekr_id == Referances.ZEKR_TYPE_SBAH_MASA2);
						DataContext.removeZekr(getActivity(), zekr, isSabah);
						getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
								getActivity().getIntent());
						return;

					default:
						break;
					}
				}
			});
		} else if (entityObject instanceof KhatmahEntity) {
			final KhatmahEntity mKhatmah = (KhatmahEntity) entityObject;
			diaryBuilder.setTitle(mKhatmah.name);
			diaryBuilder.setItems(R.array.khatmah_diary_actions, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						int today = (new Date()).getDate();
						if (today == mKhatmah.getRemindTime().getDate()) {
							mKhatmah.remind_time.setDate(today + 1);
							DataContext.editKhatmah(getActivity(), mKhatmah);
							getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
									getActivity().getIntent());
						}

						return;

					case 1:
						Intent readWerd = new Intent(getActivity(), PagerActivity.class);
						readWerd.putExtra(Referances.QURAN_START_TAG, mKhatmah.getStartPage());
						startActivity(readWerd);

						return;

					case 2:
						try {
							DataContext.updateKhatmah(getActivity(), mKhatmah.tag);
							Toast.makeText(getActivity(), getString(R.string.khatmah_update_toast), Toast.LENGTH_SHORT)
									.show();
							getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
									getActivity().getIntent());
						} catch (IOException e) {
							e.printStackTrace();
						}

						return;

					case 3:
						String khatmahString = (new Gson()).toJson(mKhatmah);
						Intent editKhatmah = new Intent(getActivity(), KhatmahActivity.class);
						editKhatmah.putExtra(Referances.KHATMAH_STATUES, Referances.KHATMAH_STATUES_EDIT);
						editKhatmah.putExtra(Referances.KHATMAH_EDIT_OBJECT, khatmahString);
						startActivity(editKhatmah);

						return;

					case 4:
						RenameKhatmahDialog rename = new RenameKhatmahDialog(mKhatmah);
						rename.setTargetFragment(parent, Referances.RENAME_DIALOG_REQ);
						rename.show(parent.getFragmentManager(), "Rename Dialog");

						return;

					case 5:
						DeleteConfirmDialog delete = new DeleteConfirmDialog(mKhatmah);
						delete.setTargetFragment(parent, Referances.DELETE_CONFIRM_DIALOG_REQ);
						delete.show(parent.getFragmentManager(), "Delete confirm dialog");
						return;
					}
				}
			});
		}
		return diaryBuilder.create();
	}

}
