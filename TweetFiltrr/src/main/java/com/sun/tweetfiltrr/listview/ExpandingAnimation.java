package com.sun.tweetfiltrr.listview;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Animation that either expands or collapses a view by sliding it down to make
 * it visible. Or by sliding it up so it will hide. It will look like it slides
 * behind the view above.
 * 
 * @auther tjerk
 * @date 6/9/12 4:58 PM
 */
public class ExpandingAnimation extends Animation {
	private View mAnimatedView;
	private int mEndHeight;
	private int mType;
	int targetHeight;
	public final static int COLLAPSE = 1;
	public final static int EXPAND = 0;
	private LinearLayout.LayoutParams mLayoutParams;

	/**
	 * Initializes expand collapse animation, has two types, collapse (1) and
	 * expand (0).
	 * 
	 * @param view_
	 *            The view to animate
	 * @param type_
	 *            The type of animation: 0 will expand from gone and 0 size to
	 *            visible and layout size defined in xml. 1 will collapse view
	 *            and set to gone
	 */
	public ExpandingAnimation(View view_, int type_) {
		mAnimatedView = view_;
		// mEndHeight = mAnimatedView.getMeasuredHeight();
		mAnimatedView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		mEndHeight = mAnimatedView.getMeasuredHeight();
		targetHeight = mEndHeight;
		mLayoutParams = ((LinearLayout.LayoutParams) view_.getLayoutParams());
		mType = type_;
		if (mType == EXPAND) {
			//mAnimatedView.getLayoutParams().height = 0;
			mLayoutParams.bottomMargin = -mEndHeight;

		} else {
			//mAnimatedView.getLayoutParams().height = targetHeight;

			mLayoutParams.bottomMargin = 0;
		}
		mAnimatedView.setVisibility(View.VISIBLE);
		Log.v("height", mLayoutParams.bottomMargin + "");

	}
	
//	 @Override
//	    protected void applyTransformation(float interpolatedTime, Transformation t) {
//	        int newHeight;
//	        if (mType == EXPAND) {
//	            newHeight = (int) (targetHeight * interpolatedTime);
//	        } else {
//	            newHeight = (int) (targetHeight * (1 - interpolatedTime));
//	        }
//	        mAnimatedView.getLayoutParams().height = newHeight;
//	        mAnimatedView.requestLayout();
//	    }

//	    @Override
//	    public void initialize(int width, int height, int parentWidth,
//	            int parentHeight) {
//	        super.initialize(width, height, parentWidth, parentHeight);
//	    }
//
//	    @Override
//	    public boolean willChangeBounds() {
//	        return true;
//	    }

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			if(mType == EXPAND) {
				mLayoutParams.bottomMargin =  -mEndHeight + (int) (mEndHeight * interpolatedTime);
			} else {
				mLayoutParams.bottomMargin = - (int) (mEndHeight * interpolatedTime);
			}
			Log.d("ExpandCollapseAnimation", "anim height " + mLayoutParams.bottomMargin);
			mAnimatedView.requestLayout();
		} else {
			if(mType == EXPAND) {

				mLayoutParams.bottomMargin = 0;
				mAnimatedView.requestLayout();
			} else {
				mLayoutParams.bottomMargin = -mEndHeight;
				mAnimatedView.setVisibility(View.GONE);
				mAnimatedView.requestLayout();
			}
		}
	}
}