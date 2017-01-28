package com.maelstrom.panning;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PanningImageView extends ImageView {

	private final PanningImageViewAttacher mAttacher;

	private int mPanningDurationInMs;

	public PanningImageView (Context context) {
		this (context, null);
	}

	public PanningImageView (Context context, AttributeSet attr) {
		this (context, attr, 0);
	}

	public PanningImageView (Context context, AttributeSet attr, int defStyle) {
		super (context, attr, defStyle);
		if (isInEditMode()) {
			// skip initialization in design/edit mode
			mAttacher = null;
			return;
		}
		readStyleParameters(context, attr);
		super.setScaleType (ScaleType.MATRIX);
		mAttacher = new PanningImageViewAttacher (this, mPanningDurationInMs);
	}


	private void readStyleParameters(Context context, AttributeSet attributeSet) {
		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PanningImageView);
		try {
			mPanningDurationInMs = typedArray.getInt (R.styleable.PanningImageView_panningDurationInMs, PanningImageViewAttacher.DEFAULT_PANNING_DURATION_IN_MS);
		} finally {
			typedArray.recycle ();
		}
	}

	// setImageBitmap calls through to this method
	@Override
	public void setImageDrawable (Drawable drawable) {
		super.setImageDrawable (drawable);
		stopUpdateStartIfNecessary ();
	}

	@Override
	public void setImageResource (int resId) {
		super.setImageResource (resId);
		stopUpdateStartIfNecessary ();
	}

	@Override
	public void setImageURI (Uri uri) {
		super.setImageURI (uri);
		stopUpdateStartIfNecessary ();
	}

	/**
	 *
	 */
	private void stopUpdateStartIfNecessary () {
		if (mAttacher != null) {
			boolean wasPanning = mAttacher.isPanning();
			mAttacher.stopPanning ();
			if(wasPanning) mAttacher.startPanning ();
		}
	}

	@Override
	public void setScaleType (ScaleType scaleType) {
		throw new UnsupportedOperationException ("Only ScaleType.MATRIX is supported");
	}


	@Override
	protected void onDetachedFromWindow () {
		mAttacher.cleanup ();
		super.onDetachedFromWindow ();
	}

	public void startPanning () {
		mAttacher.startPanning ();
	}

	public void stopPanning () {
		mAttacher.stopPanning ();
	}
}