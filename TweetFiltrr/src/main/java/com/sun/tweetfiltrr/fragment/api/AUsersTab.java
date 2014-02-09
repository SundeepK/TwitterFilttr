package com.sun.tweetfiltrr.fragment.api;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.TwitterUserProfileHome;
import com.sun.tweetfiltrr.activity.adapter.FriendsCursorAdapter;
import com.sun.tweetfiltrr.twitter.retrievers.api.ITwitterRetriever;
import com.sun.tweetfiltrr.twitter.retrievers.api.UsersFriendRetriever;
import com.sun.tweetfiltrr.twitter.callables.FriendsRetriever;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.customviews.ZoomListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory.DaoFactory;
import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;


public abstract class AUsersTab extends ATwitterFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        TabListener,  AdapterView.OnItemClickListener,
        PullToRefreshView.OnNewTweetRefreshListener<Collection<ParcelableUser>>,
        LoadMoreOnScrollListener.LoadMoreListener<Collection<ParcelableUser>> {

    private int _currentLimitCount = 50;

    private static final String TAG = AUsersTab.class.getName();
    private SimpleCursorAdapter _dataAdapter;
    private static final int LIST_LOADER = 0x05;
    private IDBDao<ParcelableUser> _friendDao;
    private IDBDao<ParcelableUser> _usersToFriendDao;
    private UrlImageLoader _sicImageLoader;
    private ThreadPoolExecutor _threadExecutor;
    private long _currentLoggedInUserId;
    private boolean _isCurrentFriendDisplayed = true;
    private SimpleDBUpdater<ParcelableUser> _userUpdater;
    private ITwitterRetriever<Collection<ParcelableUser>>  _userRetriever;
    private PullToRefreshView _pullToRefreshHandler;
    private boolean _isFinishedLoading;
    private Collection<IDatabaseUpdater> _updaters = new ArrayList<IDatabaseUpdater>();
    private boolean _tabHasBeenSelected = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControl();
        initAdapter();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d(TAG, "Not visible anymore");
            }else{
                Log.d(TAG, "Visible now!");

                if(!_tabHasBeenSelected){
                    _pullToRefreshHandler.startRefresh();
                }
               _tabHasBeenSelected = true;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = _pullToRefreshHandler.onCreateViewCallback(inflater, container, savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LIST_LOADER, null, this);
        return rootView;
    }

    private ITwitterRetriever<Collection<ParcelableUser>> getRetriever(){
        if (getCurrentUser().getUserId() == _currentLoggedInUserId) {
            return new UsersFriendRetriever( true);
        } else {
            return new UsersFriendRetriever( true);
        }
    }

//    private void retrieveFriends() {
//        Callable<Collection<ParcelableUser>> callable = null;
//        if (getCurrentUser().getUserId() == _currentLoggedInUserId) {
//            callable = new AUserRetriever(getCurrentUser(), true);
//        } else {
//            callable = new AUserRetriever(getCurrentUser(), false);
//        }
//
//        Collection<IDBDao<ParcelableUser>> _updaters = new ArrayList<IDBDao<ParcelableUser>>();
//        _updaters.add(_usersToFriendDao);
//        _updaters.add(_friendDao);
//
//
//        AsyncFutureDBUpdatetask<ParcelableUser, Integer> updatetask =
//                new AsyncFutureDBUpdatetask<ParcelableUser, Integer>(1, TimeUnit.MINUTES, _updaters, _userUpdater);
//        Future<Collection<ParcelableUser>> future = _threadExecutor.submit(callable);
//        updatetask.execute(new Future[]{future});
//
//
//    }

    private void restartCursor() {
        getActivity().getSupportLoaderManager().restartLoader(LIST_LOADER, null, this);
        _dataAdapter.notifyDataSetChanged();
    }

    protected void initControl() {
        _currentLoggedInUserId = TwitterUtil.getInstance().getCurrentLoggedInUserId(getActivity());

        DaoFlyWeightFactory flyWeight = DaoFlyWeightFactory.getInstance(getActivity().getContentResolver());
        _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();
        _sicImageLoader = TwitterUtil.getInstance().getGlobalImageLoader(getActivity());

        Log.v(TAG, "Current user is :" + getCurrentUser().toString());

        _usersToFriendDao = (IDBDao<ParcelableUser>)
                flyWeight.getDao(DaoFactory.USERS_FRIEND_DAO, getCurrentUser());
        _friendDao = (IDBDao<ParcelableUser>)
                flyWeight.getDao(DaoFactory.FRIEND_DAO, getCurrentUser());

        _userUpdater = new SimpleDBUpdater<ParcelableUser>();
        _userRetriever = getRetriever();

        _updaters.add(new DatabaseUpdater(_friendDao ));
        _updaters.add(new DatabaseUpdater(_usersToFriendDao));


    }


    protected void initAdapter() {

        String[] columns = new String[]{
                "_id",
                "friendTable_friendName",
                "friendTable_profileImageUrl"
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.friend_desc,
                R.id.friend_name,
                R.id.list_image
        };

        FriendsCursorAdapter friendsCursorAdapter = new FriendsCursorAdapter(getActivity(), R.layout.listview_for_twitter,
                null, columns, to, 0, _sicImageLoader);

        _dataAdapter = friendsCursorAdapter;
        ZoomListView.OnItemFocused listener = friendsCursorAdapter;
        _pullToRefreshHandler = getPullToRefreshView(_dataAdapter, getCurrentUser(), listener);


    }

    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_, ParcelableUser currentUser_, ZoomListView.OnItemFocused listener){
        return new PullToRefreshView<Collection<ParcelableUser>>
                (getActivity(), currentUser_, this, adapter_ ,this, this, listener);
    }


    @Override
    public void onLoad(Collection<Future<Collection<ParcelableUser>>>  futureTask_) {
        Log.v(TAG, "On load startyed with future size:" + futureTask_.size());
        AsyncUserDBUpdateTask<Integer> updatetask =
                new AsyncUserDBUpdateTask<Integer>(1, TimeUnit.MINUTES, _updaters, _pullToRefreshHandler);

        updatetask.execute(futureTask_.toArray(new Future[futureTask_.size()]));
    }


    @Override
    public void onLoadFinished(Loader<Cursor> arg, Cursor cursor) {
        _dataAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        _dataAdapter.swapCursor(null);

    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean shouldLoadMoreOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if(_tabHasBeenSelected){

            if (_currentLimitCount < getCurrentUser().getCurrentFriendCount()) {
                Log.v(TAG, "_currentLimitCount: " + _currentLimitCount + " current friend acount " +  getCurrentUser().getCurrentFriendCount());
                _currentLimitCount += 50;
                restartCursor();
                return false;
            } else if (_isFinishedLoading){
                Log.v(TAG, "not looing fro tweets onscroll, new limit count: " + _currentLimitCount);
                return false;
            }
            Log.v(TAG, "going to load more friends for:" + getCurrentUser());
            return true;
        }else{
            Log.v(TAG, "_tabHasBeenSelected is false");
        return false;
     }


    }


    private boolean getIsVis(){
        return _tabHasBeenSelected;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get the cursor, positioned to the corresponding row in the result set
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        int rowId =
                cursor.getInt(cursor.getColumnIndexOrThrow(FriendColumn._ID.s()));
        Collection<ParcelableUser> friends = _friendDao.getEntry(rowId);
        ParcelableUser newFriend = null;
        //we should only retrieve 1 friend since rowId is unique, so we iterate once
        for (ParcelableUser friend : friends) {
            newFriend = friend;
            newFriend.setRowId(rowId);
            Log.v(TAG, "User after rowID query" + friend.toString());
            break;
        }

        Intent i = new Intent(getActivity(), TwitterUserProfileHome.class);
        i.putExtra(TwitterConstants.FRIENDS_BUNDLE, newFriend);
        startActivity(i);

        //broadcastNewUser(getCurrentUser(), rowId);
    }


    @Override
    public void OnRefreshComplete(Collection<ParcelableUser> twitterParcelable) {
        Log.v(TAG, "on refresh completed with count:" + twitterParcelable.size());


        int totalNewTweets = twitterParcelable.size();
        Log.v(TAG, "on refresh completed timeline frag qith size " + totalNewTweets);

        _isFinishedLoading = (totalNewTweets <= 1);
        _currentLimitCount += totalNewTweets;
        restartCursor();

    }

    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getTweetRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        callables.add(new FriendsRetriever(getCurrentUser(), _userRetriever));
        return callables;
    }

    protected int getTimeLineCount(){
        return _currentLimitCount;
    }

}

