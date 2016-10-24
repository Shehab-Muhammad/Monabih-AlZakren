package com.islamic.monabihalzakren.ui.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.islamic.monabihalzakren.R;
import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.ui.SettingsFragment;

public class DairyAdapter extends ArrayAdapter<String> {

	private Context mContext;
	private int[] imgRec;
	private String[] title, subtitle, time;

	static class ViewHolder {
		ImageView typeIcon;
		TextView title, subtitle, time, order;
	}

	public DairyAdapter(Context context, int[] imgs, String[] titles, String[] subtitles, String[] sTime) {
		super(context, R.layout.dairy_item);
		this.mContext = context;
		this.imgRec = imgs;
		this.title = titles;
		this.subtitle = subtitles;
		this.time = sTime;
	}

	@Override
	public int getCount() {
		return imgRec.length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.dairy_item, parent, false);
			mHolder = new ViewHolder();
			mHolder.typeIcon = (ImageView) convertView.findViewById(R.id.reminder_icon);
			mHolder.title = (TextView) convertView.findViewById(R.id.reminder_identifier);
			mHolder.subtitle = (TextView) convertView.findViewById(R.id.reminder_status);
			mHolder.time = (TextView) convertView.findViewById(R.id.item_time);
			mHolder.order = (TextView) convertView.findViewById(R.id.item_order);
			convertView.setTag(mHolder);
		} else
			mHolder = (ViewHolder) convertView.getTag();

		mHolder.typeIcon.setImageResource(imgRec[position]);
		mHolder.title.setText(title[position]);
		mHolder.subtitle.setText(subtitle[position]);
		String orders = String.valueOf(position + 1);
		if (SettingsFragment.isArabic(mContext))
			orders = Referances.ArabtizeDigits(mContext, orders);
		mHolder.order.setText(orders);
		mHolder.time.setText(time[position]);

		return convertView;
	}

}
