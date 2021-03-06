package com.sun.tweetfiltrr.application;

import android.content.ContentResolver;

import com.sun.tweetfiltrr.activity.activities.EditKeywordGroupActivity;
import com.sun.tweetfiltrr.activity.activities.UserHomeActivity;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordFriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dao.impl.FriendKeywordDao;
import com.sun.tweetfiltrr.database.dao.impl.KeywordGroupDao;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.fragment.api.ASignInFragment;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.fragment.api.AUsersFragment;
import com.sun.tweetfiltrr.fragment.fragments.AutoSignInFragmentI;
import com.sun.tweetfiltrr.fragment.fragments.ConversationFragment;
import com.sun.tweetfiltrr.fragment.fragments.CustomKeywordTimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTab;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.fragment.fragments.MentionsTab;
import com.sun.tweetfiltrr.fragment.fragments.OAuthSignInFragment;
import com.sun.tweetfiltrr.fragment.fragments.TimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.UserTimelineTab;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.KeywordTweetUpdateRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AccessTokenRetrieverFromPref;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.ConversationRetrieverFromDB;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sundeep on 11/02/14.
 *
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
                UserDetailsTimelineTab.class,
                AUsersFragment.class,
                FriendsTab.class,
                FollowersTab.class,
                FriendDao.class,
                TimelineDao.class,
                FriendKeywordDao.class,
                KeywordGroupDao.class,
                EditKeywordGroupActivity.class,
                AccessTokenRetrieverFromPref.class,
                AutoSignInFragmentI.class,
                OAuthSignInFragment.class,
                ASignInFragment.class,
                KeywordTweetUpdateRetriever.class,
                ConversationFragment.class,
                ConversationRetrieverFromDB.class
//                TweetConversationActivity.class
        },
        complete = false
)
public class DaoProviderModule {

    private ContentResolver _contentResolver;

    public DaoProviderModule(ContentResolver contentResolver_){
         _contentResolver = contentResolver_;
    }

    private static final String TAG = DaoProviderModule.class.getName();


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
