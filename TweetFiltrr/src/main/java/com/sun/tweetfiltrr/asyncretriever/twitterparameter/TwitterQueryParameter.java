package com.sun.tweetfiltrr.asyncretriever.twitterparameter;

import android.util.Log;

import com.sun.tweetfiltrr.asyncretriever.api.ITwitterParameter;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import twitter4j.Query;

/**
 * Created by Sundeep on 24/01/14.
 */
public class TwitterQueryParameter implements ITwitterParameter<Query> {

    private static final String FROM = "from:@";
    private static final String TAG = TwitterQueryParameter.class.getName();
    private static final int TOTAL_TWEETS_TO_SEARCH = 50; // add a limit of 50 tweets to search fot TODO look into whether this needs changing

    @Override
    public Query getTwitterParameter(final ICachedUser user_, final boolean shouldLookForOldTweets_) {
        final ParcelableUser user = user_.getUser();
        final long maxID = user.getMaxId() <= 0  ? 1 :  user.getMaxId() ;
        final long sinceID = user.getSinceId();
        final String[] keywords = user.getKeywordGroup().getGroupKeywords().split("\\s");
        final String queryS = searchQuery(user, keywords);
        Log.v(TAG, "Query string passed :" + queryS);

        final Query keywordSearchQuery = new Query(queryS);
        keywordSearchQuery.setCount(TOTAL_TWEETS_TO_SEARCH);

        if(shouldLookForOldTweets_){
            if(maxID > 1){
                Log.v(TAG, "Setting max ID to: " + maxID);
                keywordSearchQuery.setMaxId(maxID);
            }
        }else if(sinceID > 1){
            Log.v(TAG, "Setting since ID to: " + sinceID);
            keywordSearchQuery.setSinceId(sinceID);
        }

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
