package com.sun.tweetfiltrr.animation;

import android.view.animation.Interpolator;

/**
 * Created by Sundeep on 01/02/14.
 */
public class DampeningCycleInterpolator implements Interpolator {

    private int _cycles = 3;
    private float _dampeningFactor = 0.5f;

    public DampeningCycleInterpolator(){}

    public DampeningCycleInterpolator(int cycles_, float dampeningFactor_){
        _cycles = cycles_;
        _dampeningFactor = dampeningFactor_;

    }

    @Override
    public float getInterpolation(float input) {
        float result = (1.0f - (1.0f - input) * (1.0f - input)) * _dampeningFactor;
        return (float)(Math.sin(2 * _cycles * Math.PI * result));

    }
}
