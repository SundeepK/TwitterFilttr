package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.ATimeLineRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.ITwitterParameter;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.ITwitterResponse;
import com.sun.tweetfiltrr.twitter.twitterretrievers.twitterresponse.TwitterQueryResponse;
import com.sun.tweetfiltrr.twitter.tweetprocessor.api.ITweetProcessor;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class KeywordTweetRetriever extends ATimeLineRetriever<Query> {
	
	private static final String TAG = KeywordTweetRetriever.class.getName();

    public KeywordTweetRetriever(ITweetProcessor tweetProcessor_,  ITwitterParameter<Query> twitterParameter_, boolean shouldRunOnce,
                                 boolean shouldLookForOldTweetsOnly_) {
        super(tweetProcessor_, twitterParameter_, shouldRunOnce,  shouldLookForOldTweetsOnly_);
    }
	
    @Override
    protected ITwitterResponse<Query> getTweets(Twitter twitter_, long friendID_, Query page_) throws TwitterException {
        QueryResult  timeLine = twitter_.search(page_);
        return  new  TwitterQueryResponse(timeLine);
    }

    @Override
    public ITwitterAPICall.TwitterAPICallType getTweetOperationType() {
        return TwitterAPICallType.GET_TIMELINE;
    }
}
