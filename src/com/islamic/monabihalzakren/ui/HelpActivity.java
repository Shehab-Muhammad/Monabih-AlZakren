package com.islamic.monabihalzakren.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.Referances;

public class HelpActivity extends Activity {
	private int hintId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		TextView addHint = (TextView) findViewById(R.id.text_add_hint);
		Button gotIt = (Button) findViewById(R.id.go_it_button);
		hintId = getIntent().getIntExtra(Referances.ALARM_ID, Referances.NOTIFICATION_ID_ZEKR);
		if (hintId == Referances.NOTIFICATION_ID_ZEKR)
			addHint.setText(Html.fromHtml(getString(R.string.add_azkar_hint)));
		else if (hintId == Referances.NOTIFICATION_ID_KHATMA)
			addHint.setText(Html.fromHtml(getString(R.string.add_khatmah_hint)));
		gotIt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				learnedIt();
				HelpActivity.this.finish();
			}
		});
	}

	protected void learnedIt() {
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (hintId == Referances.NOTIFICATION_ID_ZEKR)
			mSharedPreferences.edit().putBoolean(Referances.KEY_LEARNED_ADD_ZEKR, true).commit();
		else if (hintId == Referances.NOTIFICATION_ID_KHATMA)
			mSharedPreferences.edit().putBoolean(Referances.KEY_LEARNED_ADD_KHATMAH, true).commit();
	}

	@Override
	public void onBackPressed() {
		learnedIt();
		super.onBackPressed();
	}
}
