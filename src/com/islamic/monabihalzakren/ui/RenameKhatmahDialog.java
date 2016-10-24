package com.islamic.monabihalzakren.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.R;

public class RenameKhatmahDialog extends DialogFragment {

	public KhatmahEntity mKhatmah;

	public RenameKhatmahDialog(KhatmahEntity obj) {
		this.mKhatmah = obj;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.rename_khatmah_dialog, null);
		final EditText mEditText = (EditText) dialogView.findViewById(R.id.edited_name);
		mEditText.setText(mKhatmah.name);
		mBuilder.setTitle(R.string.rename_khatmah);
		mBuilder.setView(dialogView);
		mBuilder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = mEditText.getText().toString();
				if (name.equals(""))
					name = getString(R.string.khatmah_name) + " " + String.valueOf(mKhatmah.tag);
				mKhatmah.name = name;
				DataContext.editKhatmah(getActivity(), mKhatmah);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
						getActivity().getIntent());
			}
		});

		mBuilder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});

		return mBuilder.create();
	}

}
