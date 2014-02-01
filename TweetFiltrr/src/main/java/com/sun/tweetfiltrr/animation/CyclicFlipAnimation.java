package com.sun.tweetfiltrr.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Sundeep on 01/02/14.
 */
public class CyclicFlipAnimation extends Animation {

    private static final String TAG = CyclicFlipAnimation.class.getName();
    private float _angleToRotateBy;
    private Camera _camera;
    public CyclicFlipAnimation(float angleToRotateBy_) {
        _angleToRotateBy = angleToRotateBy_;
    }


    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        _camera = new Camera();
        setInterpolator(new DampeningCycleInterpolator());

    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float zDegrees = _angleToRotateBy * interpolatedTime;
         final Matrix matrix = t.getMatrix();
        _camera.save();
        _camera.rotateX(zDegrees);
        _camera.getMatrix(matrix);
        _camera.restore();

    }
}
