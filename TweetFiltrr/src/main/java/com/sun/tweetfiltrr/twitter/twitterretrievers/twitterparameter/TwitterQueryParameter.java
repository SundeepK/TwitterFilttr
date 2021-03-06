package com.sun.tweetfiltrr.twitter.twitterretrievers.twitterparameter;

import android.util.Log;

import com.sun.tweetfiltrr.twitter.twitterretrievers.api.ITwitterParameter;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Query;

/**
 * Created by Sundeep on 24/01/14.
 */
@Singleton
public class TwitterQueryParameter implements ITwitterParameter<Query> {

    private static final String FROM = "from:@";
    private static final String TAG = TwitterQueryParameter.class.getName();
    private static final int TOTAL_TWEETS_TO_SEARCH = 50; // add a limit of 50 tweets to search fot TODO look into whether this needs changing

    @Inject
    public TwitterQueryParameter(){}

    @Override
    public Query getTwitterParameter(final ICachedUser user_, final boolean shouldLookForOldTweets_) {
        final ParcelableUser user = user_.getUser();
        final long maxID = user.getKeywordMaxID() <= 0  ? 1 :  user.getKeywordMaxID() ;
        final long sinceID = user.getKeywordSinceID();
        final String[] keywords = user.getKeywordGroup().getGroupKeywords().split("\\s");
        final String queryS = searchQuery(user, keywords);

        final Query keywordSearchQuery = new Query(queryS);
        keywordSearchQuery.setCount(TOTAL_TWEETS_TO_SEARCH);

        if(shouldLookForOldTweets_){
            if(maxID > 1){
                keywordSearchQuery.setMaxId(maxID);
            }
        }else if(sinceID > 1){
            keywordSearchQuery.setSinceId(sinceID);
        }

        Log.v(TAG, "query " + keywordSearchQuery.toString());
        return keywordSearchQuery;
    }


    private void addQueryToken(final StringBuilder queryBuilder_, final String keyword_, final String username_){
        queryBuilder_.append(keyword_);
        queryBuilder_.append("+");
        queryBuilder_.append(FROM);
        queryBuilder_.append(username_);
    }

    private String searchQuery(final ParcelableUser user_, final String[] keywords){
        StringBuilder queryBuilder = new StringBuilder();
        String username = user_.getScreenName();
        for(int position = 0; position <  keywords.length; position++){

            if(position == keywords.length - 1){
                addQueryToken(queryBuilder, keywords[position], username);
            }else{
                addQueryToken(queryBuilder,keywords[position], username);
                queryBuilder.append(" OR ");
            }
        }

        return queryBuilder.toString();
    }
}
