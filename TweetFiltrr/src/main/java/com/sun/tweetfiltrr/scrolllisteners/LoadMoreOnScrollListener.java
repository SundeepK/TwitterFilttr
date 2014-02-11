package com.sun.tweetfiltrr.scrolllisteners;

import android.util.Log;
import android.widget.AbsListView;

import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by Sundeep on 09/01/14.
 */
public class LoadMoreOnScrollListener<T> implements AbsListView.OnScrollListener {

    private final ExecutorService _executorService;
    private final LoadMoreListener _loadMoreLitener;
    private final Collection<Future<T>>  _executingTasks;
    private Collection<Callable<T>> _callablesToExecute;
    private final int _itemThresHoldBeforeLoadingMore;
    private PullToRefreshView.OnNewTweetRefreshListener _refreshLis;
    private final static String TAG = LoadMoreOnScrollListener.class.getName();
    private AbsListView.OnScrollListener _scrollListener;
    public interface LoadMoreListener<T> {

        public boolean shouldLoadMoreOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
        public void onLoad(Collection<Future<T>> futureTask_);
    }

    public LoadMoreOnScrollListener(ExecutorService executorService_,Collection<Callable<T>> callablesToExectue_ ,
                                    LoadMoreListener loadMoreLitener_, int itemThresHoldBeforeLoadingMore_ ){
        _executorService = executorService_;
        _loadMoreLitener = loadMoreLitener_;
        _executingTasks = new ArrayList<Future<T>>();
        _callablesToExecute = callablesToExectue_;
        _itemThresHoldBeforeLoadingMore = itemThresHoldBeforeLoadingMore_;
    }

    public LoadMoreOnScrollListener(ExecutorService executorService_,PullToRefreshView.OnNewTweetRefreshListener refreshLis_ ,
                                    LoadMoreListener loadMoreLitener_, int itemThresHoldBeforeLoadingMore_, AbsListView.OnScrollListener scrollListener_ ){

        this(executorService_, refreshLis_, loadMoreLitener_, itemThresHoldBeforeLoadingMore_);
        _scrollListener = scrollListener_;
    }

    public LoadMoreOnScrollListener(ExecutorService executorService_,PullToRefreshView.OnNewTweetRefreshListener refreshLis_ ,
                                    LoadMoreListener loadMoreLitener_, int itemThresHoldBeforeLoadingMore_ ){
        _executorService = executorService_;
        _loadMoreLitener = loadMoreLitener_;
        _executingTasks = new ArrayList<Future<T>>();
        _refreshLis = refreshLis_;
        _itemThresHoldBeforeLoadingMore = itemThresHoldBeforeLoadingMore_;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(_scrollListener != null){
            _scrollListener.onScrollStateChanged(view,scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//        Log.v(TAG, "Scrollin details, firstVisibleItem: " + firstVisibleItem +
//                " visibleItemCount "  + visibleItemCount + " totalItemCount " + totalItemCount);
        if(_scrollListener != null){
            _scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (_executingTasks.size() > 0) {
            Iterator<Future<T>> itr = _executingTasks.iterator();
            while(itr.hasNext()){
                Future<T> future = itr.next();
                if(!future.isDone()){
                    //early out, we only want to proceed with new refreshes if all futures are done
                    Log.v(TAG, "future not done:" );
                    return;
                }else{
                    Log.v(TAG, "future done so removing:" );
                    itr.remove();
                }
            }
        }


        boolean isPassedItemThreshold =  totalItemCount - visibleItemCount   < _itemThresHoldBeforeLoadingMore  + firstVisibleItem;
//        Log.v(TAG, " visibleItemCount " + visibleItemCount);
//        Log.v(TAG, " firstVisibleItem " + firstVisibleItem);
//        Log.v(TAG, " firstVisibleItem " + totalItemCount);

        if (isPassedItemThreshold) {
            if (_loadMoreLitener.shouldLoadMoreOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)) {
              //  Log.v(TAG, "executiong task size is:" + _executingTasks.size());
                if (_executingTasks.size() < 1) {
//                    Log.v(TAG, "Attemting to load more items for listview");
                    Collection<Future<T>> future = executeCallables();
                    _executingTasks.addAll(future);
                    _loadMoreLitener.onLoad(future);
                }
            }else{
             //   Log.v(TAG, "not loading more on scroll" );
            }
        }
    }

    private Collection<Future<T>> executeCallables(){
        Collection<Future<T>> futures;
        if(_callablesToExecute != null){
            futures = executeCallables(_callablesToExecute);
        }else{
            futures =   executeCallables(_refreshLis.getTweetRetriever(true, true));
        }
        return futures;
    }

    private Collection<Future<T>> executeCallables(Collection<Callable<T> > callables_){
        final Collection<Future<T>> future = new ArrayList<Future<T>>();
        for(Callable<T> callable : callables_){
            future.add(_executorService.submit(callable));
        }
        return  future;
    }

}
