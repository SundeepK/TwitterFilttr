package com.sun.tweetfiltrr.customviews.views;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by Sundeep.Kahlon on 14/02/14.
 */
public class CircleCroppedDrawable extends Drawable {
    private Bitmap _bitmap;
    private BitmapShader _bitmapShader;
    private Paint _paint;
    private RectF _circleCroppedRect = new RectF();
    private Matrix _bitmapShaderMatrix = new Matrix();
    private int _bitmapWidth;
    private int _bitmapHeight;

    public CircleCroppedDrawable (Bitmap bitmap_){
        _bitmap = bitmap_;
        _bitmapWidth = _bitmap.getWidth();
        _bitmapHeight = _bitmap.getHeight();
        _bitmapShader =  new BitmapShader(_bitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        _paint = new Paint();
        _paint.setAntiAlias(true);
        _paint.setShader(_bitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        _circleCroppedRect.set(bounds);
        _bitmapShaderMatrix.set(null);
        _bitmapShaderMatrix.setScale((float) _circleCroppedRect.width() /_bitmapWidth ,
                (float) _circleCroppedRect.height() / _bitmapHeight);
        _bitmapShader.setLocalMatrix(_bitmapShaderMatrix);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(_circleCroppedRect, _paint);
    }



    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
