package com.sun.tweetfiltrr.tweetoperations.api;

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public interface ITweetResponse {

    public boolean wasSuccessful();
    public Throwable getFailReason();

}
