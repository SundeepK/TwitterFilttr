package com.sun.tweetfiltrr.asyncretriever.retrievers;

import com.sun.tweetfiltrr.asyncretriever.api.ATimeLineRetriever;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 12/01/14.
 */
public class MentionsRetriever extends ATimeLineRetriever  {


    private static final String TAG = MentionsRetriever.class.getName();
    private boolean _shouldLookForOldTweetsOnly;

    /**
     *
     * Implementation of a {@link com.sun.tweetfiltrr.asyncretriever.api.ATweetRetiever} to get the authenticating user's
     * mentions
     *
     * @param tweetProcessor_
     */
    public MentionsRetriever(ITweetProcessor tweetProcessor_, boolean shouldLookForOldTweetsOnly_) {
        super(tweetProcessor_, shouldLookForOldTweetsOnly_);
        _shouldLookForOldTweetsOnly = shouldLookForOldTweetsOnly_;
    }

    @Override
    protected ResponseList<Status> getTweets(Twitter twitter_, long friendID_, Paging page_) throws TwitterException {
        return twitter_.getMentionsTimeline(page_);
    }

}
