package com.islamic.monabihalzakren.ui.widgets;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.islamic.monabihalzakren.Utilities.Referances;
import com.islamic.monabihalzakren.ui.AzkarFragment;
import com.islamic.monabihalzakren.ui.KhatmahFragment;
import com.islamic.monabihalzakren.ui.PlaceholderFragment;
import com.islamic.monabihalzakren.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
	private String[] mTitles, mSubtitles2, mSubtitles1;
	private PlaceholderFragment mFragment;

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public TextView title, subtitle1, subtitle2;
		public ImageView cardImage, overflow;
		public CheckBox selected;

		public ViewHolder(CardView v) {
			super(v);
			title = (TextView) v.findViewById(R.id.card_title);
			subtitle1 = (TextView) v.findViewById(R.id.card_type);
			subtitle2 = (TextView) v.findViewById(R.id.card_reminder);
			cardImage = (ImageView) v.findViewById(R.id.card_image);
			overflow = (ImageView) v.findViewById(R.id.card_overflow);
			selected = (CheckBox) v.findViewById(R.id.card_selected);
		}
	}

	// Provide a suitable constructor (depends on the kind of dataset)
	public CardAdapter(String[] titles, String[] subtitles2, String[] subtitle1,
			PlaceholderFragment fragment) {
		mTitles = titles;
		mSubtitles2 = subtitles2;
		mSubtitles1 = subtitle1;
		mFragment = fragment;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		View lv = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent,
				false);
		// set the view's size, margins, paddings and layout parameters
		CardView v = (CardView) lv.findViewById(R.id.zekr_card);
		v.setCardElevation(5);
		v.setUseCompatPadding(true);
		v.setShadowPadding(0, 0, 5, 6);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		holder.title.setText(mTitles[position]);

		if (mSubtitles1[position].equals(Referances.ZEKR_TYPE)) {
			holder.cardImage.setImageResource(R.drawable.ic_stat_azkar);
			holder.subtitle1.setText(mFragment.getResources().getString(R.string.zekr_type));
		} else {
			holder.subtitle1.setText(mSubtitles1[position]);
			holder.cardImage.setImageResource(R.drawable.ic_stat_quran);

		}
		holder.subtitle2.setText(mSubtitles2[position]);
		holder.overflow.setTag(position);
		holder.selected.setTag(position);
		if (mFragment instanceof AzkarFragment || mFragment instanceof KhatmahFragment) {
			holder.overflow.setOnClickListener((OnClickListener) mFragment);
			holder.selected.setOnClickListener((OnClickListener) mFragment);
		}
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mTitles.length;
	}
}