package com.sun.tweetfiltrr.tweetoperations;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;

import com.sun.tweetfiltrr.tweetoperations.KeywordTweetUpdateRetriever;

import java.util.concurrent.ExecutorService;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;

/**
 * Created by Sundeep on 31/12/13.
 */
public class PullToRefreshKeywordTweetUpdateRetriever extends KeywordTweetUpdateRetriever {
    
    private final PullToRefreshLayout _pullToRefresh;
    private final Handler _handler;
    
    public PullToRefreshKeywordTweetUpdateRetriever(ExecutorService taskExecutor_, ContentResolver resolver_,
                                                     PullToRefreshLayout pullToRefresh_, Handler handler_) {
        super(taskExecutor_, resolver_);
        _pullToRefresh = pullToRefresh_;
        _handler = handler_;
    }


    @Override
    public void searchForKeywordTweetUpdates(Context context_) {
        super.searchForKeywordTweetUpdates(context_);
        _handler.post(new Runnable() {
            @Override
            public void run() {
                _pullToRefresh.setRefreshComplete();
            }
        });
    }
}
