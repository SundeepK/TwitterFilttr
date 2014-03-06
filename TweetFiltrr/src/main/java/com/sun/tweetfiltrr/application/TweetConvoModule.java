package com.sun.tweetfiltrr.application;

import com.sun.tweetfiltrr.activity.activities.TweetConversationActivity;
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
                TweetConversationActivity.class
        }
)
public class TweetConvoModule {

    private static final String TAG = TweetConvoModule.class.getName();

       @Provides @Named("blurred") public IImageProcessor provideBlurredImageProcessor() {
            return new BlurredImageGenerator();
    }

}
