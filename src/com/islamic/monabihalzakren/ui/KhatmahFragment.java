package com.islamic.monabihalzakren.ui;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.ui.widgets.CardAdapter;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.ui.PagerActivity;

public class KhatmahFragment extends PlaceholderFragment implements OnClickListener, OnMenuItemClickListener {

	ActionBarRtlizer mRtlizer;
	private List<KhatmahEntity> savedKhatmat, selectedKhatmat = new ArrayList<KhatmahEntity>();
	private RecyclerView khatmahList;
	private Integer ovPosition;
	private ActionBar mActionBar;
	private ActionMode mMode;
	private ProgressBar loading;
	private TextView noKhatmat;

	private Callback mActionModeCallback = new Callback() {

		@Override
		public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.delete_khatmah:
				if (selectedKhatmat.size() > 1) {
					Builder deleteSelected = new Builder(getActivity());
					if (SettingsFragment.isArabic(getActivity()))
						deleteSelected.setMessage(getString(R.string.delete_khatmah_confirm_selected_message,
								Referances.ArabtizeDigits(getActivity(), String.valueOf(selectedKhatmat.size()))));
					else
						deleteSelected.setMessage(getString(R.string.delete_khatmah_confirm_selected_message,
								selectedKhatmat.size()));
					deleteSelected.setTitle(R.string.delete_confirm);
					deleteSelected.setPositiveButton(android.R.string.yes,
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									deleteSelected();
								}

								private void deleteSelected() {
									List<KhatmahEntity> errorList = new ArrayList<KhatmahEntity>();
									for (KhatmahEntity deleteObject : selectedKhatmat) {
										if (deleteKhatmah(deleteObject)) {
											int pos = selectedKhatmat.indexOf(deleteObject);
											CheckBox checkBox = (CheckBox) khatmahList.getChildAt(pos).findViewById(
													R.id.card_selected);
											checkBox.setChecked(false);
										} else {
											errorList.add(deleteObject);
										}
									}
									if (errorList.isEmpty()) {
										selectedKhatmat.clear();
										mMode.finish();
										Toast.makeText(getActivity(), R.string.khatmah_delete_cab_done,
												Toast.LENGTH_SHORT).show();
									} else {
										selectedKhatmat.clear();
										for (KhatmahEntity errorObject : errorList) {
											selectedKhatmat.add(errorObject);
										}
										Toast.makeText(getActivity(), R.string.khatmah_delete_cab_failed,
												Toast.LENGTH_SHORT).show();
										errorList.clear();
									}
								}
							});
					deleteSelected.setNegativeButton(android.R.string.no, null);
					deleteSelected.create().show();
				} else if (selectedKhatmat.size() == 1) {
					DeleteConfirmDialog mDialog = new DeleteConfirmDialog(selectedKhatmat.get(0));
					mDialog.setTargetFragment(KhatmahFragment.this, Referances.DELETE_CONFIRM_DIALOG_REQ);
					mDialog.show(getFragmentManager(), "Delete confirm dialog");
				}

				return true;
			case R.id.cab_khatmah_done:
				List<KhatmahEntity> errorList = new ArrayList<KhatmahEntity>();
				for (KhatmahEntity updateObject : selectedKhatmat) {
					try {
						DataContext.updateKhatmah(getActivity(), updateObject.tag);
						int pos = selectedKhatmat.indexOf(updateObject);
						CheckBox checkBox = (CheckBox) khatmahList.getChildAt(pos).findViewById(R.id.card_selected);
						checkBox.setChecked(false);

					} catch (IOException e) {
						errorList.add(updateObject);
						e.printStackTrace();
					}
				}
				if (errorList.size() == 0) {
					selectedKhatmat.clear();
					mode.finish();
					Toast.makeText(getActivity(), R.string.khatmah_update_all_toast, Toast.LENGTH_SHORT).show();
				} else {
					selectedKhatmat.clear();
					for (KhatmahEntity errorObject : errorList) {
						selectedKhatmat.add(errorObject);
					}
					errorList.clear();
				}
				invalidate();
				return true;
			}
			return false;
		}

		@Override
		public boolean onCreateActionMode(android.support.v7.view.ActionMode arg0, Menu arg1) {
			MenuInflater mInflater = arg0.getMenuInflater();
			mInflater.inflate(R.menu.khatmah_cab_menu, arg1);
			return true;
		}

		@Override
		public void onDestroyActionMode(android.support.v7.view.ActionMode arg0) {
			for (KhatmahEntity object : selectedKhatmat) {
				int pos = selectedKhatmat.indexOf(object);
				CheckBox checkBox = (CheckBox) khatmahList.getChildAt(pos).findViewById(R.id.card_selected);
				checkBox.setChecked(false);
				selectedKhatmat.remove(object);
			}

		}

		@Override
		public boolean onPrepareActionMode(android.support.v7.view.ActionMode arg0, Menu arg1) {
			return false;
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View mView = inflater.inflate(R.layout.fragment_khatma, container, false);
		khatmahList = (RecyclerView) mView.findViewById(R.id.khatmah_container);
		loading = (ProgressBar) mView.findViewById(R.id.loading_khatmah);
		mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		noKhatmat = (TextView) mView.findViewById(R.id.no_khatmat);
		LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
		khatmahList.setLayoutManager(mManager);
		NotificationManager alarmNotificationManager = (NotificationManager) getActivity().getSystemService(
				Activity.NOTIFICATION_SERVICE);
		alarmNotificationManager.cancel(Referances.NOTIFICATION_REQUEST_KHATMAH);
		SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mPref.edit().putString(Referances.KEY_PREF_ISSUED_KHATMAT, null).commit();
		new ReadKhatmat().execute(getActivity());

		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.khatmah_fragment_menu, menu);
		if (SettingsFragment.isArabic(getActivity())) {
			if (((MonabihActivity) getActivity()).rtlizer == null) {
				((MonabihActivity) getActivity()).rtlizer = new ActionBarRtlizer(getActivity());
				((MonabihActivity) getActivity()).rtlizer.rtlize(getActivity());
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_khatmah_action:
			Intent addKhatmahIntent = new Intent(getActivity(), KhatmahActivity.class);
			addKhatmahIntent.putExtra(Referances.KHATMAH_STATUES, Referances.KHATMAH_STATUES_NEW);
			startActivity(addKhatmahIntent);
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.card_overflow:
			PopupMenu itemMenu = new PopupMenu(getActivity(), v);
			itemMenu.setOnMenuItemClickListener(this);
			itemMenu.getMenuInflater().inflate(R.menu.popup_menu_khatmat, itemMenu.getMenu());
			itemMenu.show();
			ovPosition = (Integer) v.getTag();
			break;
		case R.id.card_selected:
			KhatmahEntity selectedKhatmah = savedKhatmat.get((Integer) v.getTag());
			if (((CheckBox) v).isChecked()) {
				if (selectedKhatmat.size() == 0)
					mMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionModeCallback);
				selectedKhatmat.add(selectedKhatmah);
			} else {
				selectedKhatmat.remove(selectedKhatmah);
				if (selectedKhatmat.size() == 0) {
					mMode.finish();
				}
			}
			break;
		}

	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		KhatmahEntity mKhatmah = savedKhatmat.get(ovPosition);
		switch (arg0.getItemId()) {
		case R.id.done_khatmah:

			try {
				DataContext.updateKhatmah(getActivity(), mKhatmah.tag);
				Toast.makeText(getActivity(), R.string.khatmah_update_toast, Toast.LENGTH_SHORT).show();
				invalidate();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.edit_khatmah:
			if (mKhatmah == null)
				break;
			Intent editKhatmah = new Intent(getActivity(), KhatmahActivity.class);
			String khatmahGson = (new Gson()).toJson(mKhatmah);
			editKhatmah.putExtra(Referances.KHATMAH_STATUES, Referances.KHATMAH_STATUES_EDIT);
			editKhatmah.putExtra(Referances.KHATMAH_EDIT_OBJECT, khatmahGson);
			startActivity(editKhatmah);
			return true;
		case R.id.read_werd:
			int page = QuranInfo.getPageFromSuraAyah(mKhatmah.sura, mKhatmah.ayah);
			Intent readPage = new Intent(getActivity(), PagerActivity.class);
			readPage.putExtra(Referances.QURAN_START_TAG, page);
			startActivity(readPage);
			return true;
		case R.id.delete_khatmah:
			DeleteConfirmDialog mDialog = new DeleteConfirmDialog(mKhatmah);
			mDialog.setTargetFragment(KhatmahFragment.this, Referances.DELETE_CONFIRM_DIALOG_REQ);
			mDialog.show(getFragmentManager(), "Delete confirm dialog");
			return true;
		}
		return false;
	}

	private void invalidate() {
		Fragment frg = null;
		frg = getFragmentManager().findFragmentByTag(String.valueOf(PlaceholderFragment.KHATMA_SECTION + 1));
		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.detach(frg);
		ft.attach(frg);
		ft.commit();
	}

	public boolean deleteKhatmah(KhatmahEntity deleteObject) {
		if (DataContext.removeKhatmah(getActivity(), deleteObject)) {
			savedKhatmat.remove(deleteObject);
			invalidate();
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Referances.DELETE_CONFIRM_DIALOG_REQ) {
			mMode.finish();
			String message = getActivity().getString(R.string.khatmah_delete_done);
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			invalidate();
		}
	}

	class ReadKhatmat extends AsyncTask<Context, Void, List<KhatmahEntity>> {

		@Override
		protected List<KhatmahEntity> doInBackground(Context... params) {

			savedKhatmat = DataContext.getAllKhatmat(params[0], false);
			return savedKhatmat;
		}

		@Override
		protected void onPostExecute(List<KhatmahEntity> result) {
			loading.setVisibility(View.GONE);
			if (result == null || result.size() == 0) {
				noKhatmat.setVisibility(View.VISIBLE);
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
				boolean learned = sp.getBoolean(Referances.KEY_LEARNED_ADD_KHATMAH, false);
				if (!learned) {
					Intent hintActivity = new Intent(getActivity(), HelpActivity.class);
					hintActivity.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ID_KHATMA);
					startActivity(hintActivity);
				}
				return;
			}
			int numKhatmat = savedKhatmat.size();
			Collections.sort(savedKhatmat);

			String[] names = new String[numKhatmat], suraAyah = new String[numKhatmat], reminder = new String[numKhatmat];

			for (int i = 0; i < numKhatmat; i++) {
				KhatmahEntity mKhatmah = savedKhatmat.get(i);
				names[i] = mKhatmah.name;
				String curSuraAyah = QuranInfo.getSuraAyahString(getActivity(), mKhatmah.sura, mKhatmah.ayah);
				String nextSuraAyah = QuranInfo.getSuraAyahString(getActivity(), mKhatmah.nextWerd()[0],
						mKhatmah.nextWerd()[1]);

				suraAyah[i] = getActivity().getString(R.string.khatmah_card_werd, curSuraAyah, nextSuraAyah);
				if (!mKhatmah.remind)
					reminder[i] = (String) getString(R.string.no_reminder);
				else {
					String timeString = Referances.getTimeString(getActivity(), mKhatmah.mTime);
					reminder[i] = getString(R.string.khatmah_card_reminder, timeString);
				}
			}

			if (SettingsFragment.isArabic(getActivity())) {
				reminder = Referances.ArabtizeArrayDigits(getActivity(), reminder);
				suraAyah = Referances.ArabtizeArrayDigits(getActivity(), suraAyah);
				names = Referances.ArabtizeArrayDigits(getActivity(), names);
			}

			CardAdapter mAdapter = new CardAdapter(names, reminder, suraAyah, KhatmahFragment.this);
			khatmahList.setAdapter(mAdapter);
			khatmahList.setItemAnimator(new DefaultItemAnimator());

			khatmahList.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			noKhatmat.setVisibility(View.GONE);
			khatmahList.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

	}

}
