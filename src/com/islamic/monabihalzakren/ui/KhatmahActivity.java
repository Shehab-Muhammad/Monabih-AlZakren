package com.islamic.monabihalzakren.ui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.KhatmahEntity;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.quran.labs.androidquran.data.QuranInfo;
import com.quran.labs.androidquran.util.QuranUtils;

public class KhatmahActivity extends ActionBarActivity implements OnClickListener {

	public static final int PAGES_MAX = 604;
	private static final int DAYS_MAX = 31;
	private static final int MONTHES_MAX = 12;
	private static String[] juzesS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
			"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30" };
	private static final int NOTIFY_TIME_DIALOG = 1000;
	private static final int DISCARD_DIALOG = 1001;
	// private static final int SLEEP_MODE_DIALOG = 1002;

	private ArrayAdapter<CharSequence> ayahAdapter;
	private int khatmahPages = PAGES_MAX;
	private String activityMode;
	private KhatmahEntity KhatmahEdit;

	private Button save, cancel, notifyTimeButton;
	private NumberPicker durationMonthesNp, durationDaysNp, durationWerdNp;
	private TextView startHeaderTv, durationHeaderTv, notifyHeaderTv, startJuz2, startSura, startAyah, durationMonth,
			durationDay, durationWerd, notifyAt, nameTv;
	private SwitchCompat notifySwitch;
	private EditText khatmahName;
	private Spinner startJuz2Sp, startSuraSp, startAyahSp;
	private Date notifyTime;
	DrawerLayout layout;
	ProgressBar mProgressBar;

	private boolean isDialog = false;

	private TimePickerDialog.OnTimeSetListener notifyTimeSetter = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			notifyTime.setHours(hourOfDay);
			notifyTime.setMinutes(minute);
			setTimeButton(notifyTime);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		((MonabihApplication) getApplication()).refreshLocale(false);
		setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_khatma_activity);

		Toolbar mBar = (Toolbar) findViewById(R.id.khatmah_toolbar);
		setSupportActionBar(mBar);

		DrawerLayout holder = (DrawerLayout) findViewById(R.id.new_khatmah_layout);

		save = (Button) findViewById(R.id.save_khatmah);
		cancel = (Button) findViewById(R.id.cancel_khatmah);
		startJuz2Sp = (Spinner) findViewById(R.id.start_juz2_spinner);
		startSuraSp = (Spinner) findViewById(R.id.start_sura_spinner);
		startAyahSp = (Spinner) findViewById(R.id.start_ayah_spinner);
		durationMonthesNp = (NumberPicker) findViewById(R.id.monthes_picker);
		durationDaysNp = (NumberPicker) findViewById(R.id.days_picker);
		durationWerdNp = (NumberPicker) findViewById(R.id.werd_picker);
		notifySwitch = (SwitchCompat) findViewById(R.id.reminder_header_switch);
		notifyTimeButton = (Button) findViewById(R.id.reminder_time_button);
		khatmahName = (EditText) findViewById(R.id.khatmah_name);
		notifyAt = (TextView) findViewById(R.id.reminder_time);
		nameTv = (TextView) findViewById(R.id.reminder_id);

		save.setOnClickListener(this);
		cancel.setOnClickListener(this);

		// set default notification time button
		notifyTime = Calendar.getInstance().getTime();
		int oldDay = notifyTime.getDate();
		if (notifyTime.getMinutes() < 30)
			notifyTime.setMinutes(30);
		else {
			notifyTime.setMinutes(0);
			notifyTime.setHours(notifyTime.getHours() + 1);
		}
		notifyTime.setDate(oldDay);
		setTimeButton(notifyTime);
		notifyTimeButton.setOnClickListener(this);

		String[] suraList = getResources().getStringArray(R.array.sura_names);
		for (int i = 0; i < suraList.length; i++)
			suraList[i] = QuranUtils.getLocalizedNumber(KhatmahActivity.this, (i + 1)) + " - " + suraList[i];

		if (SettingsFragment.isArabic(KhatmahActivity.this)) {
			juzesS = Referances.ArabtizeArrayDigits(this, juzesS);
			suraList = Referances.ArabtizeArrayDigits(this, suraList);
		}

		final ArrayAdapter<CharSequence> juzAdapter = new ArrayAdapter<CharSequence>(KhatmahActivity.this,
				android.R.layout.simple_spinner_item, juzesS);
		final ArrayAdapter<CharSequence> suraAdapter = new ArrayAdapter<CharSequence>(KhatmahActivity.this,
				android.R.layout.simple_spinner_item, suraList);
		juzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		suraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		startSuraSp.setAdapter(suraAdapter);
		startJuz2Sp.setAdapter(juzAdapter);

		ayahAdapter = new ArrayAdapter<CharSequence>(KhatmahActivity.this, android.R.layout.simple_spinner_item);
		ayahAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startAyahSp.setAdapter(ayahAdapter);
		setAyahSpinner(1);

		startJuz2Sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
				int selectedJuz = QuranInfo.getJuzFromSuraAyah(startSuraSp.getSelectedItemPosition() + 1,
						startAyahSp.getSelectedItemPosition() + 1);

				if (selectedJuz != position + 1) {
					int fristSura = QuranInfo.JUZ_SURA_START[position];
					setAyahSpinner(fristSura);
					int ayah = QuranInfo.QUARTERS[position * 8][1];
					ayahAdapter.notifyDataSetChanged();
					startAyahSp.setSelection(ayah - 1);
					startSuraSp.setSelection(fristSura - 1);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		startSuraSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
				int ayah = startAyahSp.getSelectedItemPosition() + 1;
				int juz = QuranInfo.getJuzFromSuraAyah(position + 1, ayah);
				int ayahCount = QuranInfo.getNumAyahs(position + 1);
				setAyahSpinner(position + 1);
				if (juz != startJuz2Sp.getSelectedItemPosition() + 1 || ayah > ayahCount) {

					startJuz2Sp.setSelection(juz - 1);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		startAyahSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int sura = startSuraSp.getSelectedItemPosition() + 1;
				int juz = QuranInfo.getJuzFromSuraAyah(sura, arg2 + 1);
				if (juz != startJuz2Sp.getSelectedItemPosition() + 1)
					startJuz2Sp.setSelection(juz - 1);
				khatmahPages = PAGES_MAX - QuranInfo.getPageFromSuraAyah(sura, arg2 + 1) + 1;
				setMaxMin();
				setWerdPicker(durationDaysNp.getValue(), durationMonthesNp.getValue());

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// setup numpickers and its dependances

		durationDaysNp.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				int monthes = durationMonthesNp.getValue();
				setWerdPicker(newVal, monthes);
			}
		});

		durationMonthesNp.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (newVal == 0) {
					durationDaysNp.setMinValue(1);
					durationDaysNp.setWrapSelectorWheel(false);
				} else {
					durationDaysNp.setMinValue(0);
					durationDaysNp.setWrapSelectorWheel(false);
				}
				if (newVal == durationMonthesNp.getMaxValue()) {
					int daysMax = (khatmahPages / durationWerdNp.getMinValue()) % newVal;
					durationDaysNp.setMaxValue(daysMax);
					durationDaysNp.setWrapSelectorWheel(false);
				} else {
					durationDaysNp.setMaxValue(31);
					durationDaysNp.setWrapSelectorWheel(false);
				}
				int days = durationDaysNp.getValue();
				setWerdPicker(days, newVal);
			}
		});

		durationWerdNp.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				int totalDays = khatmahPages / newVal;
				int monthes = totalDays / 30;
				int days = totalDays % 30;
				durationDaysNp.setValue(days);
				durationMonthesNp.setValue(monthes);
			}
		});

		// disable buttons and edittext with switch off
		notifySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				notifyTimeButton.setEnabled(isChecked);
				khatmahName.setEnabled(isChecked);
				notifyAt.setEnabled(isChecked);
				nameTv.setEnabled(isChecked);
			}
		});

		activityMode = Referances.KHATMAH_STATUES_NEW;

		if (getIntent().getStringExtra(Referances.KHATMAH_STATUES).equals(Referances.KHATMAH_STATUES_EDIT)) {
			activityMode = Referances.KHATMAH_STATUES_EDIT;
			String khatmahGson = getIntent().getStringExtra(Referances.KHATMAH_EDIT_OBJECT);
			KhatmahEntity editObject = (new Gson()).fromJson(khatmahGson, KhatmahEntity.class);
			if (editObject == null) {
				Toast.makeText(KhatmahActivity.this, "Error during parsing object", Toast.LENGTH_SHORT).show();
				return;
			}
			startSuraSp.setSelection(editObject.sura - 1);
			startAyahSp.setSelection(editObject.ayah - 1);
			khatmahPages = PAGES_MAX - QuranInfo.getPageFromSuraAyah(editObject.sura, editObject.ayah) + 1;
			notifySwitch.setChecked(editObject.remind);
			if (editObject.remind) {
				setTimeButton(editObject.mTime);
				khatmahName.setText(editObject.name);
				notifyTime = editObject.mTime;
			}
			KhatmahEdit = editObject;
		}
		setMaxMin();

		if (activityMode.equals(Referances.KHATMAH_STATUES_EDIT)) {
			durationWerdNp.setValue(KhatmahEdit.werd_page);
			int totalDays = khatmahPages / KhatmahEdit.werd_page;
			int monthes = totalDays / 30;
			int days = totalDays % 30;
			durationDaysNp.setValue(days);
			setAyahSpinner(KhatmahEdit.sura);
			durationMonthesNp.setValue(monthes);
		} else {
			durationMonthesNp.setValue(1);
			setWerdPicker(0, 1);
		}

		holder.requestFocus();

	}

	@Override
	public void onBackPressed() {
		if (!isDialog) {
			showDialog(DISCARD_DIALOG);
			return;
		}
		super.onBackPressed();
	}

	private void setAyahSpinner(int sura) {
		int ayahCount = QuranInfo.getNumAyahs(sura);
		CharSequence[] ayahs = new String[ayahCount];
		for (int i = 0; i < ayahCount; i++) {
			ayahs[i] = String.valueOf(i + 1);
		}

		if (SettingsFragment.isArabic(KhatmahActivity.this))
			ayahs = Referances.ArabtizeArrayDigits(this, (String[]) ayahs);

		ayahAdapter.clear();

		for (int i = 0; i < ayahCount; i++) {
			ayahAdapter.add(ayahs[i]);
		}

		khatmahPages = PAGES_MAX - QuranInfo.getPageFromSuraAyah(sura, startAyahSp.getSelectedItemPosition() + 1) + 1;
		setMaxMin();
		setWerdPicker(durationDaysNp.getValue(), durationMonthesNp.getValue());
	}

	public void setTimeButton(Date time) {
		String timeString = (String) DateFormat.format(Referances.TIME_FORMAT, time);

		if (SettingsFragment.isArabic(this)) {
			timeString = Referances.ArabtizeDigits(this, timeString);
			if (timeString.contains("pm"))
				timeString = timeString.replace("pm", "م");
			else if (timeString.contains("am"))
				timeString = timeString.replace("am", "ص");
		}
		notifyTimeButton.setText(timeString);
	}

	public void setWerdPicker(int days, int monthes) {
		int totalDays = 30 * monthes + days;
		if (totalDays != 0) {
			int ayat = khatmahPages / totalDays;
			durationWerdNp.setValue(ayat);
		}
	}

	public void setMaxMin() {
		int werdMax = (PAGES_MAX < khatmahPages) ? PAGES_MAX : khatmahPages;
		int werdMin = (int) Math.ceil((double) khatmahPages / 391d);
		int monthesMax = (khatmahPages / werdMin) / 30;
		durationMonthesNp.setMaxValue(monthesMax);
		durationMonthesNp.setWrapSelectorWheel(false);
		durationWerdNp.setMinValue(werdMin);
		durationWerdNp.setMaxValue(werdMax);
		durationWerdNp.setWrapSelectorWheel(false);
		int daysMax = (durationMonthesNp.getValue() == monthesMax) ? (khatmahPages / werdMin) % monthesMax : 31;
		int daysMin = (durationMonthesNp.getValue() == 0) ? 1 : 0;
		durationDaysNp.setMaxValue(daysMax);
		durationDaysNp.setMinValue(daysMin);
		durationDaysNp.setWrapSelectorWheel(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_khatmah:
			if (activityMode == Referances.KHATMAH_STATUES_NEW)
				saveKhatmah();
			else if (activityMode == Referances.KHATMAH_STATUES_EDIT) {
				if (KhatmahEdit == null) {
					saveKhatmah();
					return;
				}

				KhatmahEdit.sura = startSuraSp.getSelectedItemPosition() + 1;
				KhatmahEdit.ayah = startAyahSp.getSelectedItemPosition() + 1;
				KhatmahEdit.werd_page = durationWerdNp.getValue();
				KhatmahEdit.remind = notifySwitch.isChecked();
				if (notifySwitch.isChecked()) {
					KhatmahEdit.mTime = KhatmahEdit.remind_time = notifyTime;
					KhatmahEdit.name = khatmahName.getText().toString();
					KhatmahEdit.autoName = false;
					if (KhatmahEdit.name.equals("")) {
						List<KhatmahEntity> savedKhatmat = DataContext.getAllKhatmat(this, false);
						Integer nameTag = 1;
						for (KhatmahEntity mKhatmah : savedKhatmat) {
							if (mKhatmah.autoName)
								nameTag++;
						}
						KhatmahEdit.name = getResources().getString(R.string.khatmah_name) + " " + nameTag.toString();
						for (KhatmahEntity mKhatmah : savedKhatmat) {
							if (mKhatmah.name.equals(KhatmahEdit.name)) {
								nameTag++;
								KhatmahEdit.name = getResources().getString(R.string.khatmah_name) + " "
										+ nameTag.toString();
							}
						}
						KhatmahEdit.autoName = true;
					}
				}
				DataContext.editKhatmah(this, KhatmahEdit);
				finish();
			}
			break;
		case R.id.cancel_khatmah:
			showDialog(DISCARD_DIALOG);
			break;
		case R.id.reminder_time_button:
			showDialog(NOTIFY_TIME_DIALOG);
			break;
		}

	}

	private void saveKhatmah() {
		int sura, ayah, werd, code;
		Integer tag;
		String name;
		KhatmahEntity saveObject;
		sura = startSuraSp.getSelectedItemPosition() + 1;
		ayah = startAyahSp.getSelectedItemPosition() + 1;
		werd = durationWerdNp.getValue();
		tag = DataContext.lastKhatmahTag(this) + 1;
		code = tag + Referances.NOTIFICATION_ID_KHATMA;
		List<KhatmahEntity> savedKhatmat = DataContext.getAllKhatmat(this, false);
		Integer nameTag = 1;
		for (KhatmahEntity mKhatmah : savedKhatmat) {
			if (mKhatmah.autoName)
				nameTag++;
		}
		name = getResources().getString(R.string.khatmah_name) + " " + nameTag.toString();
		for (KhatmahEntity mKhatmah : savedKhatmat) {
			if (mKhatmah.name.equals(name)) {
				nameTag++;
				name = getResources().getString(R.string.khatmah_name) + " " + nameTag.toString();
			}
		}
		if (notifySwitch.isChecked()) {
			saveObject = new KhatmahEntity(sura, ayah, werd, (int) tag, notifyTime, name, code);
			if (!khatmahName.getText().toString().matches("")) {
				saveObject.name = khatmahName.getText().toString();
				saveObject.autoName = false;
			} else
				saveObject.autoName = true;

		} else {
			saveObject = new KhatmahEntity(sura, ayah, werd, (int) tag, name);
			saveObject.autoName = true;
		}
		try {
			DataContext.writeKhatmah(this, saveObject);
			Toast.makeText(this, this.getResources().getString(R.string.khatmah_save_done), Toast.LENGTH_SHORT).show();
			this.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case NOTIFY_TIME_DIALOG:
			return new TimePickerDialog(this, notifyTimeSetter, notifyTime.getHours(), notifyTime.getMinutes(), false);
		case DISCARD_DIALOG:
			AlertDialog.Builder discardDialog = new AlertDialog.Builder(this);
			if (activityMode == Referances.KHATMAH_STATUES_NEW)
				discardDialog.setMessage(R.string.discard_khatmah_message);
			else if (activityMode == Referances.KHATMAH_STATUES_EDIT)
				discardDialog.setMessage(R.string.discard_khatmah_changes);
			discardDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					isDialog = true;
					onBackPressed();
				}
			});
			discardDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			discardDialog.show();
			break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (dialog instanceof TimePickerDialog) {
			((TimePickerDialog) dialog).updateTime(notifyTime.getHours(), notifyTime.getMinutes());
		}
		super.onPrepareDialog(id, dialog);
	}

}
