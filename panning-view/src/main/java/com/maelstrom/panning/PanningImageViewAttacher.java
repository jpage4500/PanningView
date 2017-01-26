package com.maelstrom.panning;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;


public class PanningImageViewAttacher {

	public static final int DEFAULT_PANNING_DURATION_IN_MS = 5000;

	private static final String TAG = "PanningViewAttacher";

	private enum panningDirection {
		R2L,
		L2R,
		T2B,
		B2T
	}

	private panningDirection mPanningDirection;

	private long mDuration,
				mCurrentPlayTime,
				mTotalTime;

	private ImageView mImageView;

	private Matrix mMatrix;

	private RectF mDisplayRect = new RectF();

	private ValueAnimator mCurrentAnimator;

	private boolean mIsPortrait,
					mIsPanning;

	public PanningImageViewAttacher (ImageView imageView, long duration) {

		if(!hasDrawable(imageView))
			throw new IllegalArgumentException ("Please define the src attribute of the ImageView");

		mDuration = duration;

		mCurrentAnimator = new ValueAnimator ();

		mCurrentAnimator.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {
			@Override
			public void onAnimationUpdate (ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue ();
				mMatrix.reset ();
				applyScaleOnMatrix ();

				if (mIsPortrait) mMatrix.postTranslate (value, 0);
				else mMatrix.postTranslate (0, value);

				refreshDisplayRect ();
				mCurrentPlayTime = animation.getCurrentPlayTime ();
				setCurrentImageMatrix ();
			}
		});

		mCurrentAnimator.addListener (new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd (Animator animation) {
				Log.d (TAG, "animation has finished, startPanning in the other way");
				changeWay ();
				final Runnable panningRunnable = new Runnable () {
					@Override
					public void run () {
						animationController ();
					}
				};
				getImageView ().post (panningRunnable);
			}

			@Override
			public void onAnimationCancel (Animator animation) {
				Log.d(TAG, "panning animation canceled");
			}

			@Override
			public void onAnimationStart(Animator animation) {
				Log.d(TAG, "panning animation started");
			}
		});

		mImageView = imageView;

		setMatrixScaleType (imageView);

		mMatrix = imageView.getImageMatrix ();
		if(mMatrix == null) mMatrix = new Matrix ();

		mIsPortrait = imageView.getResources ().getConfiguration ().orientation == Configuration.ORIENTATION_PORTRAIT;

		update ();
	}

	/**
	 *
	 */
	private void update () {
		mPanningDirection = null;
		mTotalTime = 0;
		mCurrentPlayTime = 0;
		getImageView ().post (new Runnable () {
			@Override
			public void run () {
				scale ();
				refreshDisplayRect ();
			}
		});
	}

	public boolean isPanning () { return mIsPanning; }

	/**
	 * scale and start to pan the image background
	 */
	public void startPanning () {
		if (mIsPanning) return;

		mIsPanning = true;
		final Runnable panningRunnable = new Runnable () {
			@Override
			public void run () {
				animationController ();
			}
		};
		getImageView ().post (panningRunnable);
	}

	/**
	 * stop current panning
	 */
	public void stopPanning () {
		if(!mIsPanning) return;

		mIsPanning = false;
		Log.d (TAG, "Panning Animation stopped");
		if (mCurrentAnimator != null) {
			mCurrentAnimator.removeAllListeners ();
			mCurrentAnimator.cancel ();
			mCurrentAnimator = null;
		}
		mTotalTime += mCurrentPlayTime;
		Log.d (TAG, "mTotalTime : " + mTotalTime);

		update();
	}

	/**
	 * Clean-up the resources attached to this object. This needs to be called
	 * when the ImageView is no longer used. A good example is from
	 * {@link android.view.View#onDetachedFromWindow()} or from {@link android.app.Activity#onDestroy()}.
	 * This is automatically called if you are using {@link PanningImageView}.
	 */
	public final void cleanup() {
		Log.d (TAG, "cleanup");

		stopPanning();

		//mImageView = null;
	}


	public final ImageView getImageView() {

		if (mImageView == null) {
			cleanup ();
			throw new IllegalStateException ("ImageView no longer exists. You should not use this PanningImageViewAttacher any more.");
		}

		return mImageView;
	}

	private int getDrawableIntrinsicHeight () {
		return getImageView ().getDrawable ().getIntrinsicHeight ();
	}

	private int getDrawableIntrinsicWidth () {
		return getImageView().getDrawable ().getIntrinsicWidth ();
	}

	private int getImageViewWidth () {
		return getImageView().getWidth ();
	}

	private int getImageViewHeight () {
		return getImageView().getHeight();
	}

	/**
	 * Sets the ImageView's ScaleType to Matrix.
	 */
	private static void setMatrixScaleType (ImageView imageView) {
		if (imageView != null && !(imageView instanceof PanningImageView))
			imageView.setScaleType (ImageView.ScaleType.MATRIX);
	}

	/**
	 * @return true if the ImageView and its Drawable exists
	 */
	private static boolean hasDrawable (ImageView imageView) {
		return imageView != null && imageView.getDrawable() != null;
	}

	private void animationController () {

		refreshDisplayRect ();

		if (mPanningDirection == null)
			mPanningDirection = mIsPortrait ? panningDirection.R2L : panningDirection.B2T;

		long remainingDuration = mDuration - mTotalTime;

		Log.d (TAG, String.format ("animationController: mPanningDirection : %s, mDisplayRect : %s, duration : %d", mPanningDirection, mDisplayRect, remainingDuration));

		if (mIsPortrait) {
			float end = mDisplayRect.left - (mDisplayRect.right - getImageViewWidth());
			if (mPanningDirection == panningDirection.R2L)
				animateImage(0, end, remainingDuration);
			else
				animateImage(end, 0, remainingDuration);
		} else {
			if (mPanningDirection == panningDirection.B2T)
				animateImage(mDisplayRect.top, mDisplayRect.top - (mDisplayRect.bottom - getImageViewHeight()), remainingDuration);
			else
				animateImage(mDisplayRect.top, 0.0f, remainingDuration);
		}
	}

	private void changeWay() {
		if(mPanningDirection == null) {
			Log.e (TAG, "mPanningDirection is null");
			return;
		}

		switch (mPanningDirection) {
			case R2L:
				mPanningDirection = panningDirection.L2R;
				break;
			case L2R:
				mPanningDirection = panningDirection.R2L;
				break;
			case T2B:
				mPanningDirection = panningDirection.B2T;
				break;
			case B2T:
				mPanningDirection = panningDirection.T2B;
				break;
		}
		mCurrentPlayTime = 0;
		mTotalTime = 0;
		Log.d (TAG, "changeWay: mPanningDirection is " + mPanningDirection);
	}

	private void animateImage (float start, float end, long duration) {
		Log.d (TAG, "startPanning : " + start + " to " + end + ", in " + duration + "ms");

		mCurrentAnimator.setFloatValues (start, end);
		mCurrentAnimator.setInterpolator (null);
		mCurrentAnimator.setDuration (duration);
		mCurrentAnimator.start ();
	}

	private void setCurrentImageMatrix () {
		getImageView ().setImageMatrix (mMatrix);
		getImageView ().invalidate ();
		getImageView ().requestLayout ();
	}

	private void refreshDisplayRect () {
		mDisplayRect.set (0, 0, getDrawableIntrinsicWidth(), getDrawableIntrinsicHeight());
		mMatrix.mapRect (mDisplayRect);
	}

	private void scale () {
		mMatrix.reset ();
		applyScaleOnMatrix ();
		setCurrentImageMatrix ();
	}

	private void applyScaleOnMatrix () {

		int drawableSize = mIsPortrait ? getDrawableIntrinsicHeight () : getDrawableIntrinsicWidth ();
		int imageViewSize = mIsPortrait ? getImageViewHeight () : getImageViewWidth ();
		float scaleFactor = (float)imageViewSize / (float)drawableSize;

		mMatrix.postScale (scaleFactor, scaleFactor);
	}


}
