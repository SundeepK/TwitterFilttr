package com.sun.tweetfiltrr.screencap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.sun.tweetfiltrr.imageprocessor.IProcessScreenShot;

/**
 * Created by Sundeep on 21/12/13.
 */
public class ScreenCapGenerator implements IScreenCapGenerator{

    private Activity _rootView;
    private IProcessScreenShot _screensotProcessor;
    public ScreenCapGenerator(Activity rootView_, IProcessScreenShot screensotProcessor_){
            _rootView = rootView_;
        _screensotProcessor = screensotProcessor_;
    }


    @Override
    public Bitmap generateScreenCap() {
       View view =  _rootView.getWindow().getDecorView().getRootView();

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(50);
        Bitmap screenCap =view.getDrawingCache(true);
        return  _screensotProcessor.processScreenShot(screenCap);
    }
}
