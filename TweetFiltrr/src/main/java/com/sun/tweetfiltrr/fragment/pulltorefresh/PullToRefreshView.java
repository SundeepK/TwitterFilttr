package com.sun.tweetfiltrr.fragment.pulltorefresh;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.concurrent.api.OnAsyncTaskPostExecute;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.fragment.api.IFragmentCallback;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Sundeep on 12/01/14.
 */
public class PullToRefreshView<T> implements IFragmentCallback, OnRefreshListener,
        OnAsyncTaskPostExecute<T>{

    private static final String TAG = PullToRefreshView.class.getName();
    protected PullToRefreshLayout _pullToRefreshView;
    protected ListView _pullToRefreshListView;
    protected Activity _activity;
    protected AdapterView.OnItemClickListener _onItemClick;
    protected SimpleCursorAdapter _cursorAdapter;
    protected AbsListView.OnScrollListener _onscOnScrollListener;
    protected ParcelableUser _currentUser;
    protected IDBDao<ParcelableTimeLineEntry> _timelineDao;
    protected IDBDao<ParcelableUser> _friendDao;
    protected ThreadPoolExecutor _threadExecutor;
    protected IDBUpdater<ParcelableTimeLineEntry> _timelineBufferedDBUpdater;
    protected int _timelineCount = 50;
    protected OnNewTweetRefreshListener _pullToRefreshLis;
    private int _headerLayout;

    public interface OnNewTweetRefreshListener<T> {
        public void OnRefreshComplete(T twitterParcelable);
        public Collection<Callable<T>> getTweetRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets);

    }

    public PullToRefreshView(Activity activity_, ParcelableUser currentUser_,
                             AdapterView.OnItemClickListener onItemClick_,
                             SimpleCursorAdapter cursorAdapter_,
                             OnNewTweetRefreshListener pullToRefreshLis_,
                             LoadMoreOnScrollListener.LoadMoreListener<T> loadMoreLis_, int headerLayout_){
       this(activity_, currentUser_,onItemClick_, cursorAdapter_,  pullToRefreshLis_,
               loadMoreLis_);
        _headerLayout =headerLayout_;
    }

    public PullToRefreshView(Activity activity_, ParcelableUser currentUser_,
                             AdapterView.OnItemClickListener onItemClick_,
                             SimpleCursorAdapter cursorAdapter_,
                             OnNewTweetRefreshListener pullToRefreshLis_,
                             LoadMoreOnScrollListener.LoadMoreListener<T> loadMoreLis_){
        _activity = activity_;
        _currentUser = currentUser_;
        _onItemClick = onItemClick_;
        _cursorAdapter = cursorAdapter_;
        DaoFlyWeightFactory flyWeight = DaoFlyWeightFactory
                .getInstance(_activity.getContentResolver());
        //init the Dao object using the flyweight so that we can share the Dao's between different fragments
        _timelineDao = new TimelineDao(_activity.getContentResolver(), new TimelineToParcelable());
        _friendDao = (IDBDao<ParcelableUser>) flyWeight.getDao(
                DaoFlyWeightFactory.DaoFactory.FRIEND_DAO, _currentUser);
        _timelineBufferedDBUpdater =
                new SimpleDBUpdater<ParcelableTimeLineEntry>();
        _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();
        _pullToRefreshLis = pullToRefreshLis_;
        _onscOnScrollListener =   new LoadMoreOnScrollListener<T>(_threadExecutor,
        _pullToRefreshLis, loadMoreLis_, 5);

        Log.v(TAG, "Current user passed in constructor is: " + currentUser_.toString());
    }

    /**
     * No-op
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


        _pullToRefreshListView = (ListView) rootView.findViewById(R.id.refreshable_listview);
        _pullToRefreshListView.setOnItemClickListener(_onItemClick);

        if(_headerLayout > 0){
            View headerView = inflater.inflate(_headerLayout, null);
            if(headerView != null){
                _pullToRefreshListView.addHeaderView(headerView);
            }
        }

        _pullToRefreshListView.setAdapter(_cursorAdapter);
        _pullToRefreshListView.setOnScrollListener(_onscOnScrollListener);
        return rootView;
    }

    @Override
    public void onRefreshStarted(View view) {
        Log.v(TAG, "We are looking for friends tweets because pull to refresh was done");
        Collection<ParcelableUser> users = new ArrayList<ParcelableUser>();
        users.add(_currentUser);
        Log.v(TAG, "is friend : " + _currentUser.isFriend());
        Collection<Future<T>> futures = new ArrayList<Future<T>>();

        Collection<Callable<T>> callables = _pullToRefreshLis.getTweetRetriever(true, false);

        for(Callable<T> callabe : callables){
            futures.add(_threadExecutor.submit(callabe));
        }

        Collection<IDBDao<ParcelableTimeLineEntry>> daos = new ArrayList<IDBDao<ParcelableTimeLineEntry>>();
        daos.add(_timelineDao);
        AsyncUserDBUpdateTask< Integer> asyncTask =
                new AsyncUserDBUpdateTask<Integer>(3 , TimeUnit.MINUTES ,daos,
                        _timelineBufferedDBUpdater, this);


        asyncTask.execute(futures.toArray(new Future[futures.size()]));
    }



    @Override
    public void onPreExecute() {
        _pullToRefreshView.setRefreshing(true);
    }

    @Override
    public void onPostExecute(T postExecute_) {
//        Log.v(TAG, "size of timeline onpostexecute " +  postExecute_.getUserTimeLine().size());
//        _timelineCount += postExecute_.getUserTimeLine().size();
        Log.v(TAG, "in post execute");
        _pullToRefreshView.setRefreshComplete();
        _pullToRefreshLis.OnRefreshComplete(postExecute_);
    }

    protected ListView getPullToRefreshListView() {
        return _pullToRefreshListView;
    }

    public int getRowCount() {
        return _timelineCount;
    }


}
