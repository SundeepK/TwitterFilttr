package com.sun.tweetfiltrr.concurrent;

/**
 * Created by Sundeep on 07/12/13.
 */
public class RetryException extends Exception {
    public RetryException(String reason_) {
        super(reason_);
    }
}
