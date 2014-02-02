package com.sun.tweetfiltrr.twitter.retrievers;

import com.sun.tweetfiltrr.twitter.retrievers.api.ATimeLineRetriever;
import com.sun.tweetfiltrr.twitter.retrievers.api.ITwitterParameter;
import com.sun.tweetfiltrr.twitter.retrievers.api.ITwitterResponse;
import com.sun.tweetfiltrr.twitter.retrievers.twitterresponse.TwitterTimelineResponse;
import com.sun.tweetfiltrr.twitter.tweetprocessor.api.ITweetProcessor;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 12/01/14.
 */
public class TimeLineRetriever extends ATimeLineRetriever<Paging>  {


    private static final String TAG = TimeLineRetriever.class.getName();

    /**
     *
     * Implementation of a {@link com.sun.tweetfiltrr.twitter.retrievers.api.ATweetRetiever} to get the authenticating user's
     * mentions
     *
     * @param tweetProcessor_
     */
    public TimeLineRetriever(ITweetProcessor tweetProcessor_, ITwitterParameter<Paging> twitterParameter_, boolean shouldRunOnce_,
                             boolean shouldLookForOldTweetsOnly_) {
        super(tweetProcessor_, twitterParameter_ ,shouldRunOnce_,shouldLookForOldTweetsOnly_);
    }

    @Override
    protected ITwitterResponse<Paging> getTweets(Twitter twitter_, long friendID_, Paging page_) throws TwitterException {
        final ResponseList<Status> responseList= twitter_.getUserTimeline(friendID_, page_);
        return  new TwitterTimelineResponse(responseList, page_);
    }

}