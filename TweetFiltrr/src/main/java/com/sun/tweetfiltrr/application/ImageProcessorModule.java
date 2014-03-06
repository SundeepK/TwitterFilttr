package com.sun.tweetfiltrr.application;

import com.sun.tweetfiltrr.activity.activities.TweetConversation;
import com.sun.tweetfiltrr.imageprocessor.BlurredImageGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sundeep on 11/02/14.
 */
@Module(
        addsTo = ApplicationProvider.class,
        injects = {
                TweetConversation.class
        },
        complete=false
)
public class ImageProcessorModule {

    private static final String TAG = ImageProcessorModule.class.getName();

    public ImageProcessorModule() {
    }


    @Provides @Singleton  BlurredImageGenerator provideBlurredImageProcessor() {
        return new BlurredImageGenerator();
    }


}
