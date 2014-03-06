package com.sun.tweetfiltrr.application;

import com.sun.tweetfiltrr.activity.activities.TweetConversation;
import com.sun.tweetfiltrr.imageprocessor.BlurredImageGenerator;
import com.sun.tweetfiltrr.imageprocessor.IImageProcessor;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sundeep on 11/02/14.
 */
@Module(
        addsTo = ApplicationProvider.class,
       complete=false,
        injects = {
                TweetConversation.class
        }
)
public class ImageProcessorModule {

    private static final String TAG = ImageProcessorModule.class.getName();

       @Provides @Named("blurred") public IImageProcessor provideBlurredImageProcessor() {
            return new BlurredImageGenerator();
    }

}
