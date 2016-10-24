package com.islamic.monabihalzakren.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.AzkarEntity;
import com.islamic.monabihalzakren.Utilities.DataContext;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.background.AlarmReceiver;
import com.islamic.monabihalzakren.ui.widgets.ArabicSpinnerAdapter;

public class NewZekrActivity extends ActionBarActivity implements OnClickListener {

	private int TIME_PICKER_ID = 111;
	private static int INVALID_TIME = 112;
	private TextView header1, zekrIdTv, header2, notifyStartTv, notifyRepeatTv, notifyTypeTv;
	private Spinner zekrIdSpinner, notifyRepeatSpinner, notifyTypeSpinner, azkarCategory;
	private Button saveButton, startTimeButton, save, cancel;
	private SwitchCompat notifySwitch;
	private Date displayTime;
	private List<TextView> subViews = new ArrayList<TextView>(), headerViews = new ArrayList<TextView>();
	private List<String> azkarList = new ArrayList<String>(), repeatsList, notificationsList;
	private ArrayAdapter<String> idSpinnerAdapter = null, repeatSpinnerAdapter = null, typeSpinnerAdapter = null;
	RelativeLayout startRaw, repeatingRaw;
	AlarmReceiver zekrAlarm;

	private boolean isDialog = false;

	private TimePickerDialog.OnTimeSetListener tPListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Date now = new Date();
			if (hourOfDay < now.getHours()
					&& !((azkarCategory.getSelectedItemPosition() == 0) && zekrIdSpinner.getSelectedItemPosition() == 1)) {
				showDialog(INVALID_TIME);
				return;
			}
			displayTime.setHours(hourOfDay);
			displayTime.setMinutes(minute);
			setTimeButton();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		((MonabihApplication) getApplication()).refreshLocale(false);
		setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_zekr);
		Toolbar mBar = (Toolbar) findViewById(R.id.azkar_toolbar);
		setSupportActionBar(mBar);

		save = (Button) findViewById(R.id.save_zekr);
		cancel = (Button) findViewById(R.id.cancel_zekr);

		header1 = (TextView) findViewById(R.id.zekr_header1);
		zekrIdTv = (TextView) findViewById(R.id.zekr_id_text);
		header2 = (TextView) findViewById(R.id.zekr_header2);
		notifyStartTv = (TextView) findViewById(R.id.zekr_start);
		notifyRepeatTv = (TextView) findViewById(R.id.zekr_repeat_every);
		notifyTypeTv = (TextView) findViewById(R.id.notify_type);
		azkarCategory = (Spinner) findViewById(R.id.selected_zekr_type);
		zekrIdSpinner = (Spinner) findViewById(R.id.selected_zekr);
		notifyRepeatSpinner = (Spinner) findViewById(R.id.repeat_every);
		notifyTypeSpinner = (Spinner) findViewById(R.id.notification_type);
		startTimeButton = (Button) findViewById(R.id.start_clock);
		notifySwitch = (SwitchCompat) findViewById(R.id.notify_switch);
		startRaw = (RelativeLayout) findViewById(R.id.raw3);
		repeatingRaw = (RelativeLayout) findViewById(R.id.raw4);
		ArrayAdapter<String> categorySpinnerAdapter = null;
		List<String> azkarCategories = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.azkar_types)));
		save.setOnClickListener(this);
		cancel.setOnClickListener(this);

		final List<String> allAzkar = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.zekr_ids)));
		repeatsList = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.notification_repeats)));
		notificationsList = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.notification_types)));

		restDisplayTime();
		setTimeButton();
		for (int i = 10; i < 13; i++)
			azkarList.add(allAzkar.get(i));

		if (!SettingsFragment.isArabic(this)) {
			categorySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
					azkarCategories);
			idSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, azkarList);
			repeatSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, repeatsList);
			typeSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notificationsList);
		} else {
			String[] arabicRepeats = Referances.ArabtizeArrayDigits(this,
					getResources().getStringArray(R.array.notification_repeats));
			repeatsList = Arrays.asList(arabicRepeats);
			categorySpinnerAdapter = new ArabicSpinnerAdapter(this, R.layout.spinner_item, azkarCategories);
			idSpinnerAdapter = new ArabicSpinnerAdapter(this, R.layout.spinner_item, azkarList);
			repeatSpinnerAdapter = new ArabicSpinnerAdapter(this, R.layout.spinner_item, repeatsList);
			typeSpinnerAdapter = new ArabicSpinnerAdapter(this, R.layout.spinner_item, notificationsList);
		}
		categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		idSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		repeatSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		azkarCategory.setAdapter(categorySpinnerAdapter);
		zekrIdSpinner.setAdapter(idSpinnerAdapter);
		notifyRepeatSpinner.setAdapter(repeatSpinnerAdapter);
		notifyTypeSpinner.setAdapter(typeSpinnerAdapter);

		startTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(TIME_PICKER_ID);
			}
		});

		azkarCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 0) {
					if (startRaw.getVisibility() == View.VISIBLE || repeatingRaw.getVisibility() == View.VISIBLE) {
						startRaw.setVisibility(View.GONE);
						repeatingRaw.setVisibility(View.GONE);
					}
					if (azkarList.size() > 3) {
						azkarList.clear();
						for (int i = 10; i < 13; i++) {
							azkarList.add(allAzkar.get(i));
						}
					}

				} else if (arg2 == 1) {
					if (azkarList.size() < 10) {
						azkarList.clear();
						for (int i = 0; i < 10; i++)
							azkarList.add(allAzkar.get(i));
					}
					if (startRaw.getVisibility() == View.GONE || repeatingRaw.getVisibility() == View.GONE) {
						startRaw.setVisibility(View.VISIBLE);
						repeatingRaw.setVisibility(View.VISIBLE);
						restDisplayTime();
						setTimeButton();
					}
					if (notificationsList.size() > 2) {
						notificationsList.remove(2);
						typeSpinnerAdapter.notifyDataSetChanged();
					}
				}
				idSpinnerAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		zekrIdSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (azkarCategory.getSelectedItemPosition() == 0) {
					if (notificationsList.size() < 3 && position != 2) {
						notificationsList.add(Arrays.asList(getResources().getStringArray(R.array.notification_types))
								.get(2));
						typeSpinnerAdapter.notifyDataSetChanged();
					} else if (notificationsList.size() >= 3 && position == 2) {
						notificationsList.remove(2);
						typeSpinnerAdapter.notifyDataSetChanged();
					}
					if (position == 1) {
						startRaw.setVisibility(View.VISIBLE);
						displayTime = SettingsFragment.getSleepTime(NewZekrActivity.this);
						setTimeButton();
					} else
						startRaw.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		notifySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				notifyRepeatSpinner.setEnabled(isChecked);
				notifyTypeSpinner.setEnabled(isChecked);
				notifyRepeatSpinner.setClickable(isChecked);
				notifyTypeSpinner.setClickable(isChecked);
				startTimeButton.setEnabled(isChecked);
				startTimeButton.setClickable(isChecked);
				notifyRepeatTv.setEnabled(isChecked);
				notifyStartTv.setEnabled(isChecked);
				notifyTypeTv.setEnabled(isChecked);

			}
		});

	}

	@Override
	public void onBackPressed() {
		if (!isDialog) {
			showDiscard();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == TIME_PICKER_ID) {
			return new TimePickerDialog(this, tPListener, displayTime.getHours(), displayTime.getMinutes(), false);
		} else if (id == INVALID_TIME) {
			Builder invalid = new Builder(this);
			invalid.setTitle(R.string.warning);
			invalid.setMessage(R.string.invalid_time_message);
			invalid.setPositiveButton(android.R.string.ok, null);
			return invalid.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == TIME_PICKER_ID) {
			((TimePickerDialog) dialog).updateTime(displayTime.getHours(), displayTime.getMinutes());
		}
		super.onPrepareDialog(id, dialog);
	}

	public void setTimeButton() {
		String timeString = (String) DateFormat.format(Referances.TIME_FORMAT, displayTime);
		if (SettingsFragment.isArabic(this)) {
			timeString = Referances.ArabtizeDigits(this, timeString);
			if (timeString.contains("pm"))
				timeString = timeString.replace("pm", "م");
			else if (timeString.contains("am"))
				timeString = timeString.replace("am", "ص");
		}
		startTimeButton.setText(timeString);
	}

	public void restDisplayTime() {
		displayTime = Calendar.getInstance().getTime();
		if (displayTime.getMinutes() < 30)
			displayTime.setMinutes(30);
		else {
			displayTime.setMinutes(0);
			displayTime.setHours(displayTime.getHours() + 1);
		}
	}

	public static int getObjectRepeatId(int spinnerPos) {
		int repeat;
		switch (spinnerPos) {
		case 0:
			repeat = Referances.NOTIFICATION_REPEAT_QUARTER;
			break;
		case 1:
			repeat = Referances.NOTIFICATION_REPEAT_HALF;
			break;
		case 2:
			repeat = Referances.NOTIFICATION_REPEAT_HOUR;
			break;
		case 3:
			repeat = Referances.NOTIFICATION_REPEAT_2HOURS;
			break;
		case 4:
			repeat = Referances.NOTIFICATION_REPEAT_3HOURS;
			break;
		case 5:
			repeat = Referances.NOTIFICATION_REPEAT_4HOURS;
			break;
		case 6:
			repeat = Referances.NOTIFICATION_REPEAT_6HOURS;
			break;
		case 7:
			repeat = Referances.NOTIFICATION_REPEAT_12HOURS;
			break;
		case 8:
			repeat = Referances.NOTIFICATION_REPEAT_DAY;
			break;
		default:
			repeat = Referances.NOTIFICATION_REPEAT_QUARTER;
		}
		return repeat;
	}

	public static int getObjectRepeatType(int spinnerPos) {
		int type;
		switch (spinnerPos) {
		case 1:
			type = Referances.NOTIFY_TYPE_NOTICATION;
			break;
		case 0:
			type = Referances.NOTIFY_TYPE_ZEKR_SOUND;
			break;
		case 2:
			type = Referances.NOTIFY_TYPE_OPEN_APP;
			break;
		default:
			type = Referances.NOTIFY_TYPE_NOTICATION;
		}
		return type;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.save_zekr) {
			int id = -1, repeat, type;
			repeat = getObjectRepeatId(notifyRepeatSpinner.getSelectedItemPosition());
			type = getObjectRepeatType(notifyTypeSpinner.getSelectedItemPosition());

			if (azkarCategory.getSelectedItemId() == 1)
				switch (zekrIdSpinner.getSelectedItemPosition()) {
				case 0:
					id = Referances.ZEKR_TYPE_TASBEH;
					break;
				case 1:
					id = Referances.ZEKR_TYPE_HAMD;
					break;
				case 2:
					id = Referances.ZEKR_TYPE_TAKBER;
					break;
				case 3:
					id = Referances.ZEKR_TYPE_TAWHED;
					break;
				case 4:
					id = Referances.ZEKR_TYPE_SUBHAN_WBHAMDO;
					break;
				case 5:
					id = Referances.ZEKR_TYPE_HWQALA;
					break;
				case 6:
					id = Referances.ZEKR_TYPE_SYED_EST5FAR;
					break;
				case 7:
					id = Referances.ZEKR_TYPE_SALAH_3NABI;
					break;
				case 8:
					id = Referances.ZEKR_TYPE_EST3FAR;
					break;
				case 9:
					id = Referances.ZEKR_TYPE_VARIED;
					break;

				default:
					id = Referances.ZEKR_TYPE_TASBEH;
				}
			else if (azkarCategory.getSelectedItemPosition() == 0) {
				switch (zekrIdSpinner.getSelectedItemPosition()) {
				case 0:
					id = Referances.ZEKR_TYPE_SBAH_MASA2;
					break;
				case 1:
					id = Referances.ZEKR_TYPE_SLEEP;
					break;
				case 2:
					id = Referances.ZEKR_TYPE_DOHA;
					break;
				}
			}

			try {
				int tag = DataContext.lastZekrTag(this) + 1;
				if (!notifySwitch.isChecked()) {
					type = repeat = -1;
					AzkarEntity newZekr = new AzkarEntity(id, displayTime, repeat, type, tag,
							Referances.NOTIFICATION_ID_ZEKR + tag);
					DataContext.writeZekr(this, newZekr);
				} else {
					if (id == Referances.ZEKR_TYPE_SBAH_MASA2) {
						repeat = Referances.NOTIFICATION_REPEAT_DAY;
						Date sbahTime = SettingsFragment.getSabahTime(this);
						Date masa2Time = SettingsFragment.getMasa2Time(this);
						AzkarEntity sabahObject = new AzkarEntity(id, sbahTime, repeat, type, tag,
								Referances.NOTIFICATION_ID_ZEKR + tag);
						DataContext.writeZekr(this, sabahObject);
						AzkarEntity masa2ZObject = new AzkarEntity(Referances.ZEKR_TYPE_MASA2, masa2Time, repeat, type,
								tag + 1, Referances.NOTIFICATION_ID_ZEKR + tag + 1);
						DataContext.writeZekr(this, masa2ZObject);
					} else {
						if (id == Referances.ZEKR_TYPE_DOHA) {
							displayTime = SettingsFragment.getDohahTime(this);
							repeat = Referances.NOTIFICATION_REPEAT_DAY;
						} else if (id == Referances.ZEKR_TYPE_SLEEP) {
							repeat = Referances.NOTIFICATION_REPEAT_DAY;
						}
						AzkarEntity newZekr = new AzkarEntity(id, displayTime, repeat, type, tag,
								Referances.NOTIFICATION_ID_ZEKR + tag);
						DataContext.writeZekr(this, newZekr);
					}
				}

				Toast.makeText(NewZekrActivity.this,
						NewZekrActivity.this.getResources().getString(R.string.save_zekr_done), Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				e.printStackTrace();
			}

			NewZekrActivity.this.finish();
		} else if (v.getId() == R.id.cancel_zekr) {
			showDiscard();
		}
	}

	void showDiscard() {
		AlertDialog.Builder discardDialog = new AlertDialog.Builder(this);
		discardDialog.setMessage(R.string.discard_zekr_message);
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
	}

}
