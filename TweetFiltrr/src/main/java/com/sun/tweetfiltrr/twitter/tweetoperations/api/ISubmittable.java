package com.sun.tweetfiltrr.twitter.tweetoperations.api;

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public interface ISubmittable<V> {

    public boolean isComplete();
    public boolean isFailed();
    public boolean isRunning();

}
