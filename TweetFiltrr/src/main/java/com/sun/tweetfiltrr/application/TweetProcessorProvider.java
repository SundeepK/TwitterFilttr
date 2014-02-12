package com.sun.tweetfiltrr.application;

import com.sun.tweetfiltrr.activity.activities.TwitterFilttrLoggedInUserHome;
import com.sun.tweetfiltrr.fragment.api.ATimeLineFragment;
import com.sun.tweetfiltrr.fragment.fragments.CustomKeywordTimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.MentionsTab;
import com.sun.tweetfiltrr.fragment.fragments.TimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserTimeLineTab;
import com.sun.tweetfiltrr.twitter.retrievers.api.TweetRetrieverWrapper;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sundeep on 11/02/14.
 */
@Module(
        injects = {
                ATimeLineFragment.class,
                TwitterFilttrLoggedInUserHome.class,
                TweetFiltrrApplication.class,
                CustomKeywordTimeLineTab.class,
                MentionsTab.class,
                TimeLineTab.class,
                UserTimeLineTab.class,
                TweetRetrieverWrapper.class,
                UserDetailsTimeLineTab.class
        },
        complete = false
)
public class TweetProcessorProvider {


    private static final String TAG = TweetProcessorProvider.class.getName();

    @Provides @Singleton ThreadLocal<SimpleDateFormat> provideThreadLocal(){
        return TwitterUtil.getInstance().getSimpleDateFormatThreadLocal();
    }

    @Provides @Singleton ExecutorService provideExecutorService(){
        return TwitterUtil.getInstance().getGlobalExecutor();
    }

//    @Provides @Singleton TweetRetrieverWrapper provideTweetRetriever(){
//        Log.v(TAG, "im creating wrapper");
//        return new TweetRetrieverWrapper(provideExecutorService());
//    }

}
