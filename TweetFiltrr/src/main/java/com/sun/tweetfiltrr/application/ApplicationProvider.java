package com.sun.tweetfiltrr.application;

import android.content.Context;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.activity.activities.EditKeywordGroupActivity;
import com.sun.tweetfiltrr.activity.activities.MainActivity;
import com.sun.tweetfiltrr.activity.activities.UserHomeActivity;
import com.sun.tweetfiltrr.activity.activities.UserProfileHomeActivity;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.fragment.api.ASignInFragment;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.fragment.api.AUsersFragment;
import com.sun.tweetfiltrr.fragment.fragments.CustomKeywordTimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.MentionsTab;
import com.sun.tweetfiltrr.fragment.fragments.TimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserProfileFragment;
import com.sun.tweetfiltrr.fragment.fragments.UserTimelineTab;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.TweetRetrieverWrapper;
import com.sun.tweetfiltrr.twitter.twitterretrievers.twitterparameter.TwitterPageParameter;
import com.sun.tweetfiltrr.twitter.twitterretrievers.twitterparameter.TwitterQueryParameter;
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
                ATimelineFragment.class,
                UserHomeActivity.class,
                TweetFiltrrApplication.class,
                CustomKeywordTimelineTab.class,
                MentionsTab.class,
                TimelineTab.class,
                UserTimelineTab.class,
                TweetRetrieverWrapper.class,
                UserDetailsTimelineTab.class,
                AUsersFragment.class,
                UserProfileFragment.class,
                TwitterPageParameter.class,
                TwitterQueryParameter.class,
                MainActivity.class,
                EditKeywordGroupActivity.class,
                ASignInFragment.class,
                ATwitterActivity.class,
                UserProfileHomeActivity.class
        },
        complete = false
)
//TODO have a look at breaking this up into different modules if necessary
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
