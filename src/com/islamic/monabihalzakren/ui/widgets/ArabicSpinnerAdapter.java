package com.islamic.monabihalzakren.ui.widgets;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArabicSpinnerAdapter extends ArrayAdapter<String> {
	
	Typeface itemTf;
	
	public ArabicSpinnerAdapter(Context context, int resource,
			List<String> objects) {
		super(context, resource, objects);
		itemTf = Typeface.createFromAsset(context.getAssets(),
				"fonts/DroidSansArabic.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(itemTf);
        view.setSelected(true);
        return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getDropDownView(position, convertView, parent);
		view.setTypeface(itemTf);
		view.setSelected(true);
        return view;
	}

	
}
