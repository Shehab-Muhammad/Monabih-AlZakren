package com.islamic.monabihalzakren.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.islamic.monabihalzakren.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView about1 = (TextView) findViewById(R.id.copyrights_about);
		TextView about2 = (TextView) findViewById(R.id.contact_about);

		about1.setText(Html.fromHtml(getString(R.string.about_copyrights)));
		about2.setText(Html.fromHtml(getString(R.string.about_contact)));
		about2.setMovementMethod(LinkMovementMethod.getInstance());
	}
}
