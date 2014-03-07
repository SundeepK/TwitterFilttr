package com.sun.tweetfiltrr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sun.tweetfiltrr.imageprocessor.IImageProcessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Sundeep.Kahlon on 05/03/14.
 */
public class FileImageProcessorUtils {

    private static final String TAG = FileImageProcessorUtils.class.getName();

    public static Bitmap loadImage(String filePath_, Bitmap bitmap, IImageProcessor imageProcessor_,
                               Bitmap.CompressFormat format_, int quality_, Context context_) {
        File blurredImage = new File(filePath_);
        Bitmap bmp;
        if(!blurredImage.exists()){
            bmp =  processImage(blurredImage,bitmap, imageProcessor_, format_, quality_, context_);
        }else{
            bmp = BitmapFactory.decodeFile(filePath_);
        }
        return bmp;
    }


    private static Bitmap processImage(File blurredImage, Bitmap bitmap, IImageProcessor imageProcessor_,
                                Bitmap.CompressFormat format_, int quality_, Context context_){
        OutputStream out = null;
        Bitmap bmp = null;
        try {
            Log.v(TAG, "Bitmap null so attempting to generate a new one");
            bmp = imageProcessor_.processImage(bitmap, context_);
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
