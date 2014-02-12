package com.sun.tweetfiltrr.application;

import android.content.ContentResolver;

import com.sun.tweetfiltrr.activity.activities.TwitterFilttrLoggedInUserHome;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordFriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.FriendKeywordDao;
import com.sun.tweetfiltrr.database.dao.KeywordGroupDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.fragment.api.ATimeLineFragment;
import com.sun.tweetfiltrr.fragment.api.AUsersTab;
import com.sun.tweetfiltrr.fragment.fragments.CustomKeywordTimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTabA;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTabA;
import com.sun.tweetfiltrr.fragment.fragments.MentionsTab;
import com.sun.tweetfiltrr.fragment.fragments.TimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimeLineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserTimeLineTab;

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
                UserDetailsTimeLineTab.class,
                AUsersTab.class,
                FriendsTabA.class,
                FollowersTabA.class,
                FriendDao.class,
                TimelineDao.class,
                FriendKeywordDao.class,
                KeywordGroupDao.class
        },
        complete = false
)
public class DaoProvider {

    private ContentResolver _contentResolver;

    public DaoProvider(ContentResolver contentResolver_){
         _contentResolver = contentResolver_;
    }

    private static final String TAG = DaoProvider.class.getName();


    @Provides @Singleton ContentResolver provideExecutorService(){
        return _contentResolver;
    }

    @Provides  @Singleton FriendToParcelable provideFriendToParcelable() {
        return new FriendToParcelable();
    }

    @Provides  @Singleton KeywordToParcelable provideKeywordToParcelable() {
        return new KeywordToParcelable();
    }


    @Provides  @Singleton TimelineToParcelable provideTimelineToParcelable() {
        return new TimelineToParcelable();
    }

    @Provides  @Singleton
    KeywordFriendToParcelable provideKeywordFriendToParcelable() {
        return new  KeywordFriendToParcelable(provideFriendToParcelable(), provideKeywordToParcelable());
    }


}
