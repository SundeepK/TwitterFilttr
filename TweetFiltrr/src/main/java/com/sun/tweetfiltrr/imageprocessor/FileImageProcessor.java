package com.sun.tweetfiltrr.imageprocessor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Sundeep.Kahlon on 05/03/14.
 */
public class FileImageProcessor {

    private static final String TAG = FileImageProcessor.class.getName();

    public Bitmap loadImage(String filePath_, Bitmap bitmap, IImageProcessor imageProcessor_,
                               Bitmap.CompressFormat format_, int quality_) {
        File blurredImage = new File(filePath_);
        Bitmap bmp = null;
        if(!blurredImage.exists()){
            bmp =  processImage(blurredImage,bitmap, imageProcessor_, format_, quality_);
        }else{
            bmp = BitmapFactory.decodeFile(filePath_);
        }
        return bmp;
    }


    private Bitmap processImage(File blurredImage, Bitmap bitmap, IImageProcessor imageProcessor_,
                                Bitmap.CompressFormat format_, int quality_){
        OutputStream out = null;
        Bitmap bmp = null;
        try {
            Log.v(TAG, "Bitmap null so attempting to generate a new one");
            bmp = imageProcessor_.processImage(bitmap);
            out =  new BufferedOutputStream(new FileOutputStream(blurredImage));
            bmp.compress(format_, quality_, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bmp;
    }
}
