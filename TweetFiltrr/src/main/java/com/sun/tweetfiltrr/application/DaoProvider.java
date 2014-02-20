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
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.fragment.api.AUsersFragment;
import com.sun.tweetfiltrr.fragment.fragments.CustomKeywordTimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.EditKeywordGroupTab;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTab;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.fragment.fragments.MentionsTab;
import com.sun.tweetfiltrr.fragment.fragments.TimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserTimelineTab;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AccessTokenRetrieverFromPref;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sundeep on 11/02/14.
 */
@Module(
        injects = {
                ATimelineFragment.class,
                TwitterFilttrLoggedInUserHome.class,
                TweetFiltrrApplication.class,
                CustomKeywordTimelineTab.class,
                MentionsTab.class,
                TimelineTab.class,
                UserTimelineTab.class,
                UserDetailsTimelineTab.class,
                AUsersFragment.class,
                FriendsTab.class,
                FollowersTab.class,
                FriendDao.class,
                TimelineDao.class,
                FriendKeywordDao.class,
                KeywordGroupDao.class,
                EditKeywordGroupTab.class,
                AccessTokenRetrieverFromPref.class
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
