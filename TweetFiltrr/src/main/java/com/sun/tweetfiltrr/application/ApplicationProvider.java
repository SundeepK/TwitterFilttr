package com.sun.tweetfiltrr.application;

import android.content.Context;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.activity.activities.TwitterFilttrLoggedInUserHome;
import com.sun.tweetfiltrr.fragment.api.ATimeLineFragment;
import com.sun.tweetfiltrr.fragment.api.AUsersFragment;
import com.sun.tweetfiltrr.fragment.fragments.CustomKeywordTimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.MentionsTab;
import com.sun.tweetfiltrr.fragment.fragments.TimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserProfileFragment;
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
                UserDetailsTimeLineTab.class,
                AUsersFragment.class,
                UserProfileFragment.class
        },
        complete = false
)
public class ApplicationProvider {
    private Context _context;
    public ApplicationProvider(Context context_){
        _context = context_;
    }

    private static final String TAG = ApplicationProvider.class.getName();

    @Provides @Singleton ThreadLocal<SimpleDateFormat> provideThreadLocal(){
        return TwitterUtil.getInstance().getSimpleDateFormatThreadLocal();
    }

    @Provides @Singleton ExecutorService provideExecutorService(){
        return TwitterUtil.getInstance().getGlobalExecutor();
    }

    @Provides @Singleton UrlImageLoader provideUrlImageLoader(){
        return TwitterUtil.getInstance().getGlobalImageLoader(_context);
    }

//    @Provides @Singleton TweetRetrieverWrapper provideTweetRetriever(){
//        Log.v(TAG, "im creating wrapper");
//        return new TweetRetrieverWrapper(provideExecutorService());
//    }

}
