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

        Bitmap bmp = null;

        if (Build.VERSION.SDK_INT > 16) {
            Log.v(TAG, "Generatoruing blurred image");
            bmp = inputBitmap_.copy(inputBitmap_.getConfig(), true);

            final RenderScript rs = RenderScript.create(_context);
            final Allocation input = Allocation.createFromBitmap(rs, inputBitmap_, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(_radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bmp);
        }else{
            bmp = inputBitmap_.copy(inputBitmap_.getConfig(), true);
            fastblur(bmp, 4);
        }


        return bmp;

    }

    // taken from http://incubator.quasimondo.com/processing/superfast_blur.php which shows a fast implementation of blur
    //TODO have a look optimising this fuher and using gaussian blur rather than box blur
    private void fastblur(Bitmap img, int radius){

        if (radius<1){
            return;
        }
        int w= img.getWidth();
        int h=img.getHeight();
        int wm=w-1;
        int hm=h-1;
        int wh=w*h;
        int div=radius+radius+1;
        int r[]=new int[wh];
        int g[]=new int[wh];
        int b[]=new int[wh];
        int rsum,gsum,bsum,x,y,i,p,p1,p2,yp,yi,yw;
        int vmin[] = new int[Math.max(w, h)];
        int vmax[] = new int[Math.max(w, h)];
        int[] pix= new  int[w*h];

        img.getPixels(pix, 0, w, 0,0,w, h);

        int dv[]=new int[256*div];
        for (i=0;i<256*div;i++){
            dv[i]=(i/div);
        }

        yw=yi=0;

        for (y=0;y<h;y++){
            rsum=gsum=bsum=0;
            for(i=-radius;i<=radius;i++){
                int ind = yi+Math.min(wm,Math.max(i,0));
                p=pix[ind];
                rsum+=(p & 0xff0000)>>16;
                gsum+=(p & 0x00ff00)>>8;
                bsum+= p & 0x0000ff;
            }
            for (x=0;x<w;x++){

                r[yi]=dv[rsum];
                g[yi]=dv[gsum];
                b[yi]=dv[bsum];

                if(y==0){
                    vmin[x]=Math.min(x + radius + 1, wm);
                    vmax[x]=Math.max(x - radius, 0);
                }
                p1=pix[yw+vmin[x]];
                p2=pix[yw+vmax[x]];

                rsum+=((p1 & 0xff0000)-(p2 & 0xff0000))>>16;
                gsum+=((p1 & 0x00ff00)-(p2 & 0x00ff00))>>8;
                bsum+= (p1 & 0x0000ff)-(p2 & 0x0000ff);
                yi++;
            }
            yw+=w;
        }

        for (x=0;x<w;x++){
            rsum=gsum=bsum=0;
            yp=-radius*w;
            for(i=-radius;i<=radius;i++){
                yi=Math.max(0, yp)+x;
                rsum+=r[yi];
                gsum+=g[yi];
                bsum+=b[yi];
                yp+=w;
            }
            yi=x;
            for (y=0;y<h;y++){
                pix[yi]=0xff000000 | (dv[rsum]<<16) | (dv[gsum]<<8) | dv[bsum];
                if(x==0){
                    vmin[y]=Math.min(y + radius + 1, hm)*w;
                    vmax[y]=Math.max(y - radius, 0)*w;
                }
                p1=x+vmin[y];
                p2=x+vmax[y];

                rsum+=r[p1]-r[p2];
                gsum+=g[p1]-g[p2];
                bsum+=b[p1]-b[p2];

                yi+=w;
            }
        }

        img.setPixels(pix,0, w,0,0,w,h);
    }

}
