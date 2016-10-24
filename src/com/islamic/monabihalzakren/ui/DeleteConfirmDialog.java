package com.islamic.monabihalzakren.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.Utilities.ReminderEntity;
import com.islamic.monabihalzakren.R;

public class DeleteConfirmDialog extends DialogFragment {

	private ReminderEntity deleteEntity;

	public DeleteConfirmDialog(ReminderEntity obj) {
		this.deleteEntity = obj;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder mBuilder = new Builder(getActivity());
		mBuilder.setTitle(R.string.delete_confirm);
		String message = null;
		if (deleteEntity instanceof AzkarEntity)
			message = getString(R.string.delete_confirm_message, getString(R.string.delete_confirm_zekr));
		else if (deleteEntity instanceof KhatmahEntity)
			message = getString(R.string.delete_confirm_message, ((KhatmahEntity) deleteEntity).name);
		mBuilder.setMessage(message);
		mBuilder.setPositiveButton(android.R.string.yes, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (deleteEntity instanceof AzkarEntity) {
					boolean isSabah = (((AzkarEntity) deleteEntity).zekr_id == Referances.ZEKR_TYPE_SBAH_MASA2);
					DataContext.removeZekr(getActivity(), ((AzkarEntity) deleteEntity), isSabah);

				} else if (deleteEntity instanceof KhatmahEntity) {
					DataContext.removeKhatmah(getActivity(), (KhatmahEntity) deleteEntity);
				}
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
						getActivity().getIntent());
			}
		});
		mBuilder.setNegativeButton(android.R.string.no, null);

		return mBuilder.create();
	}

}
