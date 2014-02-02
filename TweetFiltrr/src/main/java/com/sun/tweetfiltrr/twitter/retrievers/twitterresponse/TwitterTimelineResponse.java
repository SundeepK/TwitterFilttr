package com.sun.tweetfiltrr.twitter.retrievers.twitterresponse;

import com.sun.tweetfiltrr.twitter.retrievers.api.ITwitterResponse;

import java.util.Iterator;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Created by Sundeep on 25/01/14.
 */
public class TwitterTimelineResponse implements ITwitterResponse<Paging> {
    private ResponseList<Status> _responseList;
    private Paging _paging;

    public  TwitterTimelineResponse(ResponseList<Status> responseList_, Paging paging_){
        _responseList = responseList_;
        _paging = paging_;
    }

    @Override
    public Iterator<Status> getTwitterResult() {
        return _responseList.iterator();
    }

    @Override
    public Paging getNextTwitterParameter() {
        return  new Paging(_paging.getPage()+1);//TODO verify this is correct by running some tests, do we only need to increment by 1
    }

    @Override
    public boolean hasNext() {
        return !_responseList.isEmpty();
    }
}
