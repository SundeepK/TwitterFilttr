package com.sun.tweetfiltrr.imageprocessor;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Sundeep on 21/12/13.
 */
public interface IImageProcessor {


    public Bitmap processImage(Bitmap inputBitmap_,  Context context_);

}
