package com.sun.tweetfiltrr.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Sundeep on 13/02/14.
 */
public class PulseImageView extends ImageView {


    public static final String TAG = PulseImageView.class.getName();


    private final RectF _bitmapRect = new RectF();
    private  BitmapShader _bitmapShader;
    private  Paint _bitmapPaint = new Paint();

    private  int _bitmapWidth;
    private  int _bitmapHeight;
    private  Matrix _matrix = new Matrix();


    public PulseImageView(Context context) {
        super(context);
    }

    public PulseImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        if(bitmap !=null){
        _bitmapWidth = bitmap.getWidth();
        _bitmapHeight = bitmap.getHeight();
        _bitmapRect.set(0, 0, _bitmapWidth, _bitmapHeight);

        _bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        _bitmapShader.setLocalMatrix(_matrix);

        _bitmapPaint.setStyle(Paint.Style.FILL);
        _bitmapPaint.setAntiAlias(true);
        _bitmapPaint.setShader(_bitmapShader);

        _matrix.set(null);
        float scale =0;
        float dx = 0;
       float  dy = 0;

        if (_bitmapWidth * _bitmapRect.height() > _bitmapRect.width() * _bitmapHeight) {
            scale = (float) _bitmapHeight / _bitmapRect.height() ;
            dx = (_bitmapRect.width() - _bitmapWidth * scale) * 0.5f;
        } else {
            scale =  (float) _bitmapWidth/ _bitmapRect.width() ;
            dy = ( _bitmapHeight - _bitmapRect.height() * scale) * 0.5f;
        }
        Log.v(TAG, "scale is " + scale);
        int top = getTop();
        int left = getLeft();
            int right = getRight();
            int bot = getBottom();
    //   _bitmapRect.set(0, 0, _bitmapWidth, _bitmapHeight);

       _matrix.setScale(2, 2);
//       _matrix.postTranslate((int) (50),
//                (int) (50));
        _bitmapShader.setLocalMatrix(_matrix);

        invalidate();
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
//        if(_bitmapWidth > 0){
            Matrix maxt = new Matrix();
            maxt.setTranslate((float)(_bitmapWidth/2),(float)(_bitmapHeight/2) );
         //   canvas.setMatrix(maxt);
            canvas.drawOval(_bitmapRect, _bitmapPaint);
//        }

    }
}
