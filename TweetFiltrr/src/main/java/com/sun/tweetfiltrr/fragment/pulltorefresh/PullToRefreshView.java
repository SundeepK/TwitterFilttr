package com.sun.tweetfiltrr.fragment.pulltorefresh;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.concurrent.api.OnAsyncTaskPostExecute;
import com.sun.tweetfiltrr.customviews.views.ZoomListView;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.fragment.api.IFragmentCallback;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Sundeep on 12/01/14.
 */
public class PullToRefreshView<T> implements IFragmentCallback, OnRefreshListener,
        OnAsyncTaskPostExecute<T>, IProgress {

    private static final String TAG = PullToRefreshView.class.getName();
    private PullToRefreshLayout _pullToRefreshView;
    private ZoomListView _pullToRefreshListView;
    private Activity _activity;
    private AdapterView.OnItemClickListener _onItemClick;
    private SimpleCursorAdapter _cursorAdapter;
    private AbsListView.OnScrollListener _onscOnScrollListener;
    private ParcelableUser _currentUser;
    private ExecutorService _threadExecutor;
    private OnNewTweetRefreshListener<T> _pullToRefreshLis;
    private int _headerLayout;
    private Collection<IDatabaseUpdater> _updaters;
    private ZoomListView.OnItemFocused _itemDisabledLis;
    private View _emptyView;
    private int _emptyLayout;

    public interface OnNewTweetRefreshListener<T> {
        public void OnRefreshComplete(T twitterParcelable);
        public Collection<Callable<T>> getUsersRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets);
    }

    private PullToRefreshView (Builder<T> builder_){
        _activity = builder_._activity;
        _currentUser = builder_._currentUser;
        _onItemClick = builder_._onItemClick;
        _cursorAdapter = builder_._cursorAdapter;
        _threadExecutor = builder_._threadExecutor;
        _pullToRefreshLis = builder_._pullToRefreshLis;
        _onscOnScrollListener =   builder_._loadMoreScrollLis;
        _updaters = builder_._updaters;
        _itemDisabledLis = builder_._itemFocusListener;
        _emptyLayout = builder_._emptyLayout;
        _headerLayout = builder_._headerLayout;
    }

    /**
     * No-op
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreateCallback(Bundle savedInstanceState) {

    }

    @Override
    public View onCreateViewCallback(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pull_to_refresh_list_view, container, false);
        _pullToRefreshView = (PullToRefreshLayout) rootView.findViewById(R.id.pulls_refresh_layout);
        ActionBarPullToRefresh.from(_activity)
                // Mark All Children as pullable
                .allChildrenArePullable()
                .listener(this)
                .options(Options.create()
                        .scrollDistance(.50f)
                        .minimize()
                        .build())
                .setup(_pullToRefreshView);

        _pullToRefreshListView = (ZoomListView) rootView.findViewById(R.id.refreshable_listview);
        _pullToRefreshListView.setOnItemClickListener(_onItemClick);
        _pullToRefreshListView.setOnItemDisableListener(_itemDisabledLis);
        if (_headerLayout > 0) {
            View headerView = inflater.inflate(_headerLayout, null);
            if (headerView != null) {
                _pullToRefreshListView.addHeaderView(headerView);
            }
        }


        if(_emptyLayout > 0){
            _emptyView = inflater.inflate(_emptyLayout, null);
//            LinearLayout.LayoutParams layoutParams =
//                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.FILL_PARENT);
//            param.addRule(RelativeLayout.CENTER_IN_PARENT);
//            param.addRule(RelativeLayout.CENTER_HORIZONTAL);
//            param.addRule(RelativeLayout.CENTER_VERTICAL);
//            layoutParams.gravity= Gravity.CENTER;
            _emptyView.setVisibility(View.GONE);
//            ((ViewGroup) rootView).addView(_emptyView, layoutParams);
            _pullToRefreshListView.addFooterView(_emptyView);

        }

        _pullToRefreshListView.setAdapter(_cursorAdapter);
        _pullToRefreshListView.setOnScrollListener(_onscOnScrollListener);
        return rootView;
    }

    public void setEmptyView(Cursor cursor) {
        int totalCount = cursor.getCount();
        if(_emptyView != null){
            if (totalCount <= 0) {
                _emptyView.setVisibility(View.VISIBLE);
            }else{
                _emptyView.setVisibility(View.GONE);
            }
        }
    }


    public void startRefresh() {
        if(_pullToRefreshLis != null){
            Log.v(TAG, "We are looking for friends tweets because pull to refresh was done");
            Log.v(TAG, "is friend : " + _currentUser.isFriend());
            final Collection<Future<T>> futures = new ArrayList<Future<T>>();
            final Collection<Callable<T>> callables = _pullToRefreshLis.getUsersRetriever(true, false);
            for (Callable<T> callabe : callables) {
                futures.add(_threadExecutor.submit(callabe));
            }
            AsyncUserDBUpdateTask<Integer> _updaterTask;
            _updaterTask = new AsyncUserDBUpdateTask<Integer>(3, TimeUnit.MINUTES, _updaters, this);
            _updaterTask.execute(futures.toArray(new Future[futures.size()]));
        }else{
            Log.v(TAG, "no listner set, so not refreshing");
        }
    }


    @Override
    public void onRefreshStarted(View view) {
        startRefresh();
    }


    @Override
    public void onPreExecute() {
        startRefreshAnimation();
    }

    @Override
    public void onPostExecute(T postExecute_) {
        _pullToRefreshView.setRefreshComplete();
        int totalCount = _pullToRefreshListView.getAdapter().getCount();
//        if (totalCount <= 0) {
//            _emptyView.setVisibility(View.VISIBLE);
//        }
        if(_pullToRefreshLis != null){
            _pullToRefreshLis.OnRefreshComplete(postExecute_);
        }
    }

    @Override
    public void startRefreshAnimation() {
        _pullToRefreshView.setRefreshing(true);
    }

    @Override
    public void setRefreshAnimationFinish() {
        _pullToRefreshView.setRefreshComplete();

    }

    public static class Builder<T>{
        private Activity _activity;
        private AdapterView.OnItemClickListener _onItemClick;
        private SimpleCursorAdapter _cursorAdapter;
        private AbsListView.OnScrollListener _onScrollListener;
        private ParcelableUser _currentUser;
        private ExecutorService _threadExecutor;
        private OnNewTweetRefreshListener<T> _pullToRefreshLis;
        private int _headerLayout;
        private Collection<IDatabaseUpdater> _updaters;
        private ZoomListView.OnItemFocused _itemFocusListener;
        private int _emptyLayout;
        private LoadMoreOnScrollListener.LoadMoreListener<T> _loadMoreLis;
        private LoadMoreOnScrollListener<T> _loadMoreScrollLis;

        public Builder(Activity activity_, ParcelableUser parcelableUser_){
            _activity = activity_;
            _currentUser = parcelableUser_;
        }

        public Builder<T> setOnItemClick(AdapterView.OnItemClickListener onItemClick_){
            _onItemClick = onItemClick_;
            return this;
        }

        public Builder<T> setCursorAadapter(SimpleCursorAdapter cursorAdapter_){
            _cursorAdapter = cursorAdapter_;
            return this;
        }

        public Builder<T> setOnRefreshListener(OnNewTweetRefreshListener pullToRefreshLis_){
            _pullToRefreshLis = pullToRefreshLis_;
            return this;
        }

        public Builder<T> setLoadMoreListener(LoadMoreOnScrollListener.LoadMoreListener<T> loadMoreLis_){
            _loadMoreLis = loadMoreLis_;
            return this;
        }

        public Builder<T> setHeaderLayout(int headerLayout_){
            _headerLayout = headerLayout_;
            return this;
        }

        public Builder<T> setOnItemFocusedListener(ZoomListView.OnItemFocused itemDisabledLis_){
            _itemFocusListener = itemDisabledLis_;
            return this;
        }

        public Builder<T> setDBUpdaters(Collection<IDatabaseUpdater> dbUpdaters_){
            _updaters = dbUpdaters_;
            return this;
        }

        public Builder<T> setOnScrollListener(AbsListView.OnScrollListener scrollListener_){
            _onScrollListener = scrollListener_;
            return this;
        }

        public Builder<T> setEmptyLayout(int emptyLayout_){
            _emptyLayout = emptyLayout_;
            return this;
        }

        public Builder<T> setThreadPoolExecutor(ExecutorService executorService_){
            _threadExecutor = executorService_;
            return this;
        }

        public PullToRefreshView<T> build(){
            initBuilder();
            return new PullToRefreshView<T>(this);
        }

        private void initBuilder() {
            if( _pullToRefreshLis != null && _loadMoreLis != null && _onScrollListener != null){
                _loadMoreScrollLis =  new LoadMoreOnScrollListener<T>(_threadExecutor,
                        _pullToRefreshLis, _loadMoreLis, 5, _onScrollListener);
            }
            if(_threadExecutor == null){
                _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();
            }
        }
    }


}
