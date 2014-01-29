package com.sun.tweetfiltrr.tweetoperations;

import android.os.AsyncTask;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public class WaitingTweetOperationThread implements Callable<Void>{

    private Collection<AsyncTask<TwitterOperation, Void, ParcelableTweet>> _tasks;

    public  WaitingTweetOperationThread(ParcelableTweet tweet){
        _tasks = new ConcurrentLinkedQueue<AsyncTask<TwitterOperation, Void, ParcelableTweet>>;
    }

    public void submitNewTask(){

    }

    @Override
    public Void call() throws Exception {
        return null;
    }
}
