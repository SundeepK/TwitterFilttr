package com.sun.tweetfiltrr.database.dbupdater.impl;

import android.util.Log;

import com.sun.tweetfiltrr.concurrent.RetryingCallable;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Sundeep on 07/12/13.
 */

/**
 * This class is deprecated must move away from usign it
 * @param <T>
 */
@Deprecated
public class BufferedDBUpdater<T extends IParcelableTwitter>  {
    
    private IDBDao<T> _dao;
    private LinkedBlockingDeque<T> _twitterParcelableDeque;
    private static final int MAX_CAP = 50;
    private ConcurrentLinkedQueue<Future<Void>> _flushingThreads;
    private String[] _columns;
    private ExecutorService _executorService;
    private static final String TAG = BufferedDBUpdater.class.getName();


    public BufferedDBUpdater(IDBDao<T> dao_, String[] columns_, ExecutorService executorService_){
        _dao = dao_;
        _columns = columns_;
        _twitterParcelableDeque = new LinkedBlockingDeque<T>();
        _flushingThreads = new ConcurrentLinkedQueue<Future<Void>>();
        _executorService = executorService_;
    }

    public void put(T element_) {
        _twitterParcelableDeque.offerFirst(element_);
        if(_twitterParcelableDeque.size() >= MAX_CAP){
            queueNewDBFlushTask();
        }
    }


    public void addAll(Collection<T> entries_) {
        _twitterParcelableDeque.addAll(entries_);
    }

    public void flushToDB() throws ExecutionException, InterruptedException {
        flushRemainingEntries();
        flushRemainingTasks();
    }

    private void flushRemainingTasks() throws InterruptedException, ExecutionException {
        while(!_flushingThreads.isEmpty()){
            Future<Void> future = _flushingThreads.remove();
            try {
                future.get();
            } catch (InterruptedException interruptedE) {
                throw new  InterruptedException(interruptedE.getMessage());
            } catch (ExecutionException executionE) {
                throw new  ExecutionException(executionE);
            }

        }
    }


    private void queueNewDBFlushTask(){
            Queue<T> parcelablesToFlush = new LinkedList<T>();
            int currentCount = MAX_CAP;
        Log.v(TAG, "Size before flush is " + _twitterParcelableDeque.size());
        while(currentCount > 0 && !_twitterParcelableDeque.isEmpty()){
                T parcelable = _twitterParcelableDeque.removeLast();
                parcelablesToFlush.offer(parcelable);
                currentCount--;
                Log.v(TAG, "Added + new parcelable to be updated via DBBUffer " + parcelable.toString());
            }
            _flushingThreads.offer(_executorService.submit(getNewFlusherTask(parcelablesToFlush)));
    }

    protected Callable<Void> getNewFlusherTask(Collection<T> entriesToFlush_){
        Log.v(TAG, Arrays.toString(entriesToFlush_.toArray()));
        Callable<Void> task = new DBFlusherTask<T>(_dao,_columns, entriesToFlush_);
        return new RetryingCallable<Void>(_executorService, task, 3);
    }

    private void flushRemainingEntries(){
        while(!_twitterParcelableDeque.isEmpty()){
            queueNewDBFlushTask();
        }
    }



}
