package com.sun.tweetfiltrr.asyncretriever.twitterresponse;

import com.sun.tweetfiltrr.asyncretriever.api.ITwitterResponse;

import java.util.Iterator;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;

/**
 * Created by Sundeep on 25/01/14.
 */
public class TwitterQueryResponse  implements ITwitterResponse<Query>{

    private QueryResult _queryResult;

    public TwitterQueryResponse(QueryResult queryResult_){
        _queryResult = queryResult_;
    }

    @Override
    public Iterator<Status> getTwitterResult() {
        return _queryResult.getTweets().iterator();
    }

    @Override
    public Query getNextTwitterParameter() {
        return _queryResult.nextQuery();
    }

    @Override
    public boolean hasNext() {
        return _queryResult.hasNext();
    }
}
