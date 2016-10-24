package com.islamic.monabihalzakren.ui;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.Utilities.ReminderEntity;
import com.islamic.monabihalzakren.ui.widgets.DairyAdapter;

public class DiaryFragment extends PlaceholderFragment {

	private ActionBarRtlizer rtlizer;
	private FloatingActionButton addZekr, addKhatmah;
	private List<ReminderEntity> mFiles = new ArrayList<ReminderEntity>();
	private ProgressBar loadAll;
	private TextView noReminders;
	private ListView allReminders;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_diary, container, false);
		addZekr = (FloatingActionButton) mView.findViewById(R.id.floating_add_zekr);
		addKhatmah = (FloatingActionButton) mView.findViewById(R.id.floating_add_khatmah);
		loadAll = (ProgressBar) mView.findViewById(R.id.load_all);
		noReminders = (TextView) mView.findViewById(R.id.no_notifications);
		allReminders = (ListView) mView.findViewById(R.id.all_reminders_list);

		addZekr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent newZekr = new Intent(getActivity(), NewZekrActivity.class);
				startActivity(newZekr);
			}
		});

		addKhatmah.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent newKhatmah = new Intent(getActivity(), KhatmahActivity.class);
				newKhatmah.putExtra(Referances.KHATMAH_STATUES, Referances.KHATMAH_STATUES_NEW);
				startActivity(newKhatmah);
			}
		});

		new LoadAllFiles().execute(getActivity());
		allReminders.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DiaryDialogFrament mDialog = new DiaryDialogFrament(mFiles.get(arg2), DiaryFragment.this);
				mDialog.setTargetFragment(DiaryFragment.this, Referances.DIARY_DIALOG_REQ);
				mDialog.show(getFragmentManager(), "Diary Dialog");
			}

		});

		return mView;
	}

	protected void invalidateView() {
		Fragment frg = null;
		frg = getFragmentManager().findFragmentByTag(String.valueOf(PlaceholderFragment.DIARY_SECTION + 1));
		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.detach(frg);
		ft.attach(frg);
		ft.commit();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (SettingsFragment.isArabic(getActivity())) {
			if (rtlizer == null) {
				rtlizer = new ActionBarRtlizer(getActivity());
				rtlizer.rtlize(getActivity());
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (rtlizer != null)
			rtlizer.rtlize(getActivity());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
			invalidateView();
		super.onActivityResult(requestCode, resultCode, data);
	}

	class LoadAllFiles extends AsyncTask<Context, Void, List<ReminderEntity>> {

		private Context mContext;

		@Override
		protected List<ReminderEntity> doInBackground(Context... params) {
			mFiles = new ArrayList<ReminderEntity>();
			mContext = params[0];
			List<AzkarEntity> azkar = DataContext.getAllAzkar(params[0], false, true);
			List<KhatmahEntity> khatmat = DataContext.getAllKhatmat(params[0], true);
			mFiles.addAll(khatmat);
			mFiles.addAll(azkar);
			Collections.sort(mFiles);
			return mFiles;
		}

		@Override
		protected void onPostExecute(List<ReminderEntity> result) {
			loadAll.setVisibility(View.GONE);
			if (result.size() == 0) {
				noReminders.setVisibility(View.VISIBLE);
				return;
			}
			allReminders.setAdapter(null);
			String[] title = new String[result.size()], subtitle = new String[result.size()], time = new String[result
					.size()];
			int[] imgRec = new int[result.size()];

			for (ReminderEntity entityObject : result) {
				int index = result.indexOf(entityObject);
				time[index] = Referances.getTimeString(mContext, entityObject.getRemindTime());
				if (entityObject instanceof AzkarEntity) {
					AzkarEntity mZekr = (AzkarEntity) entityObject;
					title[index] = Referances.getZekrName(mContext, mZekr.zekr_id);
					subtitle[index] = mContext.getString(R.string.zekr_type);
					imgRec[index] = R.drawable.ic_dairy_azkar;
				} else if (entityObject instanceof KhatmahEntity) {
					KhatmahEntity mKhatmah = (KhatmahEntity) entityObject;
					title[index] = mKhatmah.name;
					if (SettingsFragment.isArabic(mContext))
						subtitle[index] = mContext.getString(R.string.khatmah_status,
								Referances.ArabtizeDigits(mContext, String.valueOf(mKhatmah.werd_page)));
					else
						subtitle[index] = mContext.getString(R.string.khatmah_status, mKhatmah.werd_page);
					imgRec[index] = R.drawable.ic_dairy_quran;
				}
			}
			allReminders.setVisibility(View.VISIBLE);
			DairyAdapter mAdapter = new DairyAdapter(mContext, imgRec, title, subtitle, time);
			allReminders.setAdapter(mAdapter);

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			allReminders.setVisibility(View.GONE);
			noReminders.setVisibility(View.GONE);
			loadAll.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

	}

}
