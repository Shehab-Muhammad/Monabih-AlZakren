package com.islamic.monabihalzakren.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.islamic.monabihalzakren.R;

public class ArabicTextView extends TextView {

	public ArabicTextView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(attrs);
	}

	public ArabicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	public ArabicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.ArabicTextView);
			String font = a.getString(R.styleable.ArabicTextView_font);
			if (font != null) {
				Typeface arabicTypeface = Typeface.createFromAsset(getContext()
						.getAssets(), "fonts/" + font);
				setTypeface(arabicTypeface);
			}
			a.recycle();

		}
	}

}
