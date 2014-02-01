package com.sun.tweetfiltrr.animation;

import android.view.animation.Interpolator;

/**
 * Created by Sundeep on 01/02/14.
 */
public class DampeningCycleInterpolator implements Interpolator {


    @Override
    public float getInterpolation(float input) {
        float result = (1.0f - (1.0f - input) * (1.0f - input)) * 0.5f;
        return (float)(Math.sin(2 * 2 * Math.PI * result));

    }
}
