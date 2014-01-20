package com.sun.tweetfiltrr.imageprocessor;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

/**
 * Created by Sundeep on 21/12/13.
 */
public class BlurredImageGenerator implements IImageProcessor {

    private static final String TAG = BlurredImageGenerator.class.getName();
    private final float _radius = 10;
    private Context _context;

    public BlurredImageGenerator(Context context_){
        _context = context_;
    }

    @Override
    public Bitmap processImage(Bitmap inputBitmap_) {

        if (Build.VERSION.SDK_INT > 16) {
            Log.v(TAG, "Generatoruing blurred image");
            Bitmap bitmap = inputBitmap_.copy(inputBitmap_.getConfig(), true);

            final RenderScript rs = RenderScript.create(_context);
            final Allocation input = Allocation.createFromBitmap(rs, inputBitmap_, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(_radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }

        return inputBitmap_;

    }

}
