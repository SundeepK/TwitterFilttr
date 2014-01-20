package com.sun.tweetfiltrr.animation;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Sundeep.Kahlon on 16/01/14.
 */
public class ExpandingAnimation extends Animation{
    private static final String TAG = ExpandingAnimation.class.getName();
    private int _initialHeight;
    private int _targetHeight;
    private View _viewToAnimate;
    private boolean _isExpandable = false;
    private int lastHeight;

    public ExpandingAnimation(View viewToAnimate_, int initailHeight_, int padding){
        _viewToAnimate = viewToAnimate_;
        _isExpandable = true;
        _initialHeight = initailHeight_;

        viewToAnimate_.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        _targetHeight = viewToAnimate_.getMeasuredHeight();
                        Log.v(TAG, "height" + _targetHeight);
        _viewToAnimate.getLayoutParams().height = _initialHeight;
        _viewToAnimate.requestLayout();

    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int height = 0;
        Log.v(TAG,  "interpolatedTime: " + interpolatedTime);

        if( (interpolatedTime < 1)){

        if(_isExpandable){
            height = (int)( _targetHeight * interpolatedTime) + _initialHeight;
        }else{
            height = (int)( _targetHeight  * (1 - interpolatedTime)) + _initialHeight;
        }

     //   Log.v(TAG, "expanding height" + height + "interpolatedTime: " + interpolatedTime);

        _viewToAnimate.getLayoutParams().height = height;
        _viewToAnimate.requestLayout();
            lastHeight = height;
        }else{
            Log.v(TAG, "cached height " + lastHeight);

            if(lastHeight <= _initialHeight){
                _isExpandable = true;
            }else{
                _isExpandable = false;
            }

            Log.v(TAG, "value after interlatedtime is > 1 " + _isExpandable);

        }

    }

}
