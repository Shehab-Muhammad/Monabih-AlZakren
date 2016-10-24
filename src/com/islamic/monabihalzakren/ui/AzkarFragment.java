package com.islamic.monabihalzakren.ui;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;

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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
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

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.ui.widgets.CardAdapter;

public class AzkarFragment extends PlaceholderFragment implements OnClickListener, OnMenuItemClickListener {
	private RecyclerView azkarContainer;
	private int ovPosition;
	private ActionBarRtlizer rtlizer;
	private List<AzkarEntity> savedAzkar, selectedAzkar = new ArrayList<AzkarEntity>();
	private ViewGroup mContainer;
	private ActionMode mActionMode;
	private ProgressBar loadingAzkar;
	private TextView noAzkar;

	private ActionMode.Callback actionCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode arg0) {

			if (selectedAzkar.size() > 0) {
				for (AzkarEntity mObject : selectedAzkar) {
					int pos = savedAzkar.indexOf(mObject);
					CheckBox checkBox = (CheckBox) ((CardView) azkarContainer.getChildAt(pos))
							.findViewById(R.id.card_selected);
					checkBox.setChecked(false);
				}
				selectedAzkar.clear();
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
			MenuInflater mInflater = arg0.getMenuInflater();
			mInflater.inflate(R.menu.azkar_cab_menu, arg1);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
			final ActionMode mMode = arg0;
			if (arg1.getItemId() == R.id.zekr_delete) {
				if (selectedAzkar.size() > 1) {
					Builder deleteSelected = new Builder(getActivity());
					if (SettingsFragment.isArabic(getActivity()))
						deleteSelected.setMessage(getString(R.string.delete_zekr_confirm_selected_message,
								Referances.ArabtizeDigits(getActivity(), String.valueOf(selectedAzkar.size()))));
					else
						deleteSelected.setMessage(getString(R.string.delete_zekr_confirm_selected_message,
								selectedAzkar.size()));
					deleteSelected.setTitle(R.string.delete_confirm);
					deleteSelected.setPositiveButton(android.R.string.yes,
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									deleteSelected();
								}

								private void deleteSelected() {
									for (AzkarEntity mObject : selectedAzkar) {
										deleteZekr(mObject);
									}
									selectedAzkar.clear();
									invalidateView();
									mMode.finish();
								}
							});
					deleteSelected.setNegativeButton(android.R.string.no, null);
					deleteSelected.create().show();
				} else if (selectedAzkar.size() == 1) {
					DeleteConfirmDialog mDialog = new DeleteConfirmDialog(selectedAzkar.get(0));
					mDialog.setTargetFragment(AzkarFragment.this, Referances.DELETE_CONFIRM_DIALOG_REQ);
					mDialog.show(getFragmentManager(), "Delete confirm dialog");
				}
			}
			return true;
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContainer = container;
		View mView = inflater.inflate(R.layout.azkar_fragment, mContainer, false);
		loadingAzkar = (ProgressBar) mView.findViewById(R.id.loading_azkar);
		noAzkar = (TextView) mView.findViewById(R.id.no_azkar);
		azkarContainer = (RecyclerView) mView.findViewById(R.id.azkar_container);
		LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
		azkarContainer.setLayoutManager(mManager);
		NotificationManager alarmNotificationManager = (NotificationManager) getActivity().getSystemService(
				Activity.NOTIFICATION_SERVICE);
		alarmNotificationManager.cancel(Referances.NOTIFICATION_REQUEST_DHIKR);
		SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mPref.edit().putString(Referances.KEY_PREF_ISSUED_AZKAR, null).commit();
		new ReadAzkar().execute(getActivity());

		return mView;
	}

	protected void invalidateView() {
		Fragment frg = null;
		frg = getFragmentManager().findFragmentByTag(String.valueOf(PlaceholderFragment.AZKAR_SECTION + 1));
		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.detach(frg);
		ft.attach(frg);
		ft.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.card_overflow:
			PopupMenu itemMenu = new PopupMenu(getActivity(), v);
			itemMenu.setOnMenuItemClickListener(this);
			itemMenu.getMenuInflater().inflate(R.menu.popup_menu_azkar, itemMenu.getMenu());
			itemMenu.show();
			ovPosition = (Integer) v.getTag();
			break;
		case R.id.card_selected:
			AzkarEntity selectedZekr = savedAzkar.get((Integer) v.getTag());

			if (((CheckBox) v).isChecked()) {
				if (selectedAzkar.size() == 0) {
					mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(actionCallback);
				}
				selectedAzkar.add(selectedZekr);
			} else {
				selectedAzkar.remove(selectedZekr);
				if (selectedAzkar.size() == 0) {
					mActionMode.finish();
				}
			}

			break;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.azkar_menu, menu);
		if (SettingsFragment.isArabic(getActivity())) {
			if (rtlizer == null) {
				rtlizer = new ActionBarRtlizer(getActivity());
				rtlizer.rtlize(getActivity());
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (rtlizer != null)
			rtlizer.rtlize(getActivity());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_zekr:
			Intent activityIntent = new Intent(getActivity(), NewZekrActivity.class);
			startActivity(activityIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void editItem() {
		AzkarEntity item = savedAzkar.get(ovPosition);
		EditDialogFragment editFragment = new EditDialogFragment(item);
		editFragment.setTargetFragment(this, Referances.EDIT_DIALOG_REQ);
		editFragment.show(getFragmentManager(), "Edit dialog");
	}

	public boolean deleteZekr(AzkarEntity mObject) {
		savedAzkar.remove(mObject);
		if (mObject.zekr_id == Referances.ZEKR_TYPE_SBAH_MASA2) {
			return DataContext.removeZekr(getActivity(), mObject, true);
		} else
			return DataContext.removeZekr(getActivity(), mObject, false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Referances.EDIT_DIALOG_REQ) {
			invalidateView();
		} else if (requestCode == Referances.DELETE_CONFIRM_DIALOG_REQ) {
			mActionMode.finish();
			invalidateView();
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		switch (arg0.getItemId()) {
		case R.id.edit_item:
			editItem();
			break;
		case R.id.delete_item:
			AzkarEntity mObject = savedAzkar.get(ovPosition);
			DeleteConfirmDialog mDialog = new DeleteConfirmDialog(mObject);
			mDialog.setTargetFragment(AzkarFragment.this, Referances.DELETE_CONFIRM_DIALOG_REQ);
			mDialog.show(getFragmentManager(), "Delete confirm dialog");
			break;
		}
		return true;
	}

	class ReadAzkar extends AsyncTask<Context, Void, List<AzkarEntity>> {

		@Override
		protected List<AzkarEntity> doInBackground(Context... params) {
			savedAzkar = DataContext.getAllAzkar(params[0], false, false);
			return savedAzkar;
		}

		@Override
		protected void onPostExecute(List<AzkarEntity> result) {
			loadingAzkar.setVisibility(View.GONE);
			if (result == null || result.size() == 0) {
				noAzkar.setVisibility(View.VISIBLE);
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
				boolean learned = sp.getBoolean(Referances.KEY_LEARNED_ADD_ZEKR, false);
				if (!learned) {
					Intent hintActivity = new Intent(getActivity(), HelpActivity.class);
					hintActivity.putExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ID_ZEKR);
					startActivity(hintActivity);
				}
				return;
			}

			Collections.sort(savedAzkar);
			String[] names = new String[savedAzkar.size()], repeats = new String[savedAzkar.size()], types = new String[savedAzkar
					.size()];
			int i = 0;

			for (AzkarEntity mObject : savedAzkar) {
				names[i] = Referances.getZekrName(getActivity(), mObject.zekr_id);
				repeats[i] = Referances.getZekrReminder(getActivity(), mObject.repeat_id);
				types[i] = Referances.ZEKR_TYPE;
				i++;
			}
			CardAdapter mAdapter = new CardAdapter(names, repeats, types, AzkarFragment.this);
			azkarContainer.setItemAnimator(new DefaultItemAnimator());
			azkarContainer.setAdapter(mAdapter);

			azkarContainer.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			azkarContainer.setVisibility(View.GONE);
			noAzkar.setVisibility(View.GONE);
			loadingAzkar.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

	}

}
