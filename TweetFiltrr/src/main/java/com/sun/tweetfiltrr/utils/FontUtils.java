package com.sun.tweetfiltrr.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by Sundeep on 06/02/14.
 */
public class FontUtils {

    private static Typeface _normal;
    private static Typeface _thin;
    private static Typeface _light;

//    private static Typeface _bold;


    public static Typeface thinTypeFace(AssetManager assetsManager){
        if(_thin == null){
            _thin = Typeface.createFromAsset(assetsManager, "fonts/Roboto-Thin.ttf");
        }
        return _thin;
    }

    public static Typeface lightTypeFace(AssetManager assetsManager){
        if(_light == null){
            _light = Typeface.createFromAsset(assetsManager, "fonts/Roboto-Light.ttf");
        }
        return _light;
    }

    public static Typeface normalTypeFace(AssetManager assetsManager){
        if(_normal == null){
            _normal = Typeface.createFromAsset(assetsManager, "fonts/Roboto-Regular.ttf");
        }
        return _normal;
    }


}
