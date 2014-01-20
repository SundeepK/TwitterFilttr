package com.sun.tweetfiltrr.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.api.ImageTaskListener;

import java.net.URISyntaxException;

/**
 * Created by Sundeep on 11/01/14.
 */
public final class ImageLoaderUtils {

    private static final String TAG = ImageLoaderUtils.class.getName();

    public static void attemptLoadImage(ImageView imageView_, UrlImageLoader loader_
            , String url_, int sampleSize_, ImageTaskListener listender) {
        try {
            if (!TextUtils.isEmpty(url_)) {
                loader_.displayImage(url_, imageView_, sampleSize_, listender);
            } else {
                imageView_.setImageDrawable(new ColorDrawable(Color.BLACK));
            }
        } catch (NullPointerException nu) {
            Log.e(TAG, "Null pointer detected while loading image, using default");
            setDefaultDrawable(imageView_);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error in background image display");
            setDefaultDrawable(imageView_);
        }
    }

    private static void setDefaultDrawable(ImageView imageView_) {
        imageView_.setImageDrawable(new ColorDrawable(Color.BLACK));
    }


}
