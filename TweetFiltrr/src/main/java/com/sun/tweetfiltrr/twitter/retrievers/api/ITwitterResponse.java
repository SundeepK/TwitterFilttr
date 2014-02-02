package com.sun.tweetfiltrr.twitter.retrievers.api;

import java.util.Iterator;

import twitter4j.Status;

/**
 * Created by Sundeep on 25/01/14.
 */
public interface ITwitterResponse<T> {


    public Iterator<Status> getTwitterResult();

    public T getNextTwitterParameter();

    public boolean hasNext();


}
