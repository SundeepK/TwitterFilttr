package com.sun.tweetfiltrr.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.TextUtils;

/**
 * Created by Sundeep on 06/02/14.
 */
public class FontUtils {

    private final static int ROBOTO_THIN = 0;
    private final static int ROBOTO_LIGHT = 1;
    private final static int ROBOTO_NORMAL = 2;
    private static Typeface _normal;
    private static Typeface _thin;
    private static Typeface _light;

//    private static Typeface _bold;

    public static String getText(String text_) {
        if (!TextUtils.isEmpty(text_) && !text_.equals("null")) {
            return "\n" + text_;
        } else {
            return "";
        }
    }

    public static Typeface thinTypeFace(AssetManager assetsManager) {
        if (_thin == null) {
            _thin = Typeface.createFromAsset(assetsManager, "fonts/Roboto-Thin.ttf");
        }
        return _thin;
    }

    public static Typeface lightTypeFace(AssetManager assetsManager) {
        if (_light == null) {
            _light = Typeface.createFromAsset(assetsManager, "fonts/Roboto-Light.ttf");
        }
        return _light;
    }

    public static Typeface normalTypeFace(AssetManager assetsManager) {
        if (_normal == null) {
            _normal = Typeface.createFromAsset(assetsManager, "fonts/Roboto-Regular.ttf");
        }
        return _normal;
    }

    public static Typeface getTypeFace(int typeface, Context context_) {
        switch (typeface) {
            case ROBOTO_THIN:
            default:
                return FontUtils.thinTypeFace(context_.getAssets());
            case ROBOTO_LIGHT:
                return FontUtils.lightTypeFace(context_.getAssets());
            case ROBOTO_NORMAL:
                return (FontUtils.normalTypeFace(context_.getAssets()));
        }
    }


}
