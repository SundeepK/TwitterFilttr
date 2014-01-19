package com.sun.tweetfiltrr.fragment.fragments;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.TwitterFilttrUserHome;
import com.sun.tweetfiltrr.activity.adapter.FriendsCursorAdapter;
import com.sun.tweetfiltrr.alarm.TwitterUpdateReceiver;
import com.sun.tweetfiltrr.asyncretriever.FriendsRetriever;
import com.sun.tweetfiltrr.asyncretriever.api.IUserRetriever;
import com.sun.tweetfiltrr.asyncretriever.api.UsersFriendRetriever;
import com.sun.tweetfiltrr.concurrent.AsyncFutureDBUpdatetask;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.UserFriendsDao;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.fragment.api.APullToRefreshListFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory.DaoFactory;
import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFriendsTable.UsersToFriendsColumn;


public class FriendsTab extends APullToRefreshListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        TabListener, LoadMoreOnScrollListener.LoadMoreListener<ParcelableUser> {
    private static final String TAG = FriendsTab.class.getName();
    private CursorAdapter _dataAdapter;
    private static final int LIST_LOADER = 0x01;
    private IDBDao<ParcelableUser> _friendDao;
    private IDBDao<ParcelableUser> _usersToFriendDao;
    private UrlImageLoader _sicImageLoader;
    private ThreadPoolExecutor _threadExecutor;
    private BroadcastReceiver _broadCastReceiver;
    private ListView _listView;
    private long _currentLoggedInUserId;
    private boolean _isCurrentFriendDisplayed = true;
    private int _totalFriendCount = 50;
    private SimpleDBUpdater<ParcelableUser> _userUpdater;
    private IUserRetriever _userRetriever;

    public void addUser(View view) {

        //scheduleAlarmReceiver();

        //Collection<ParcelableUser> friends = _friendDao.getEntries(null,null, FriendColumn.COLUMN_LAST_DATETIME_SYNC + " ASC, " + FriendColumn.COLUMN_PAGE_NO + " DESC " );
        Collection<ParcelableUser> friends = _friendDao.getEntries(null, null, FriendColumn.COLUMN_MAXID + " DESC, " + FriendColumn.COLUMN_LAST_DATETIME_SYNC + " ASC ");

        for (ParcelableUser friend : friends) {
            Log.v(TAG, friend.toString() + " with date: " + friend.getLastUpadateDate() + " with pageNo: " + friend.getMaxId());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControl();
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        callables.add(new FriendsRetriever(getCurrentUser(), _userRetriever));

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        _pullToRefreshListView.setAdapter(_dataAdapter);
        _pullToRefreshListView.setOnScrollListener(new LoadMoreOnScrollListener<Collection<ParcelableUser>>(_threadExecutor,
                callables , this, 5));
        getActivity().getSupportLoaderManager().initLoader(LIST_LOADER, null, this);
        return rootView;
    }

    private IUserRetriever getRetriever(){
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
//        Collection<IDBDao<ParcelableUser>> daos = new ArrayList<IDBDao<ParcelableUser>>();
//        daos.add(_usersToFriendDao);
//        daos.add(_friendDao);
//
//
//        AsyncFutureDBUpdatetask<ParcelableUser, Integer> updatetask =
//                new AsyncFutureDBUpdatetask<ParcelableUser, Integer>(1, TimeUnit.MINUTES, daos, _userUpdater);
//        Future<Collection<ParcelableUser>> future = _threadExecutor.submit(callable);
//        updatetask.execute(new Future[]{future});
//
//
//    }

    private void restartCursor() {
        if (_listView != null) {
            _listView.invalidate();
        }
        getActivity().getSupportLoaderManager().restartLoader(LIST_LOADER, null, this);
        _dataAdapter.notifyDataSetChanged();

    }


    private void broadcastNewUser(ParcelableUser _user, long rowID_) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _user);
        bundle.putLong(TwitterConstants.FRIENDS_ROWID, rowID_);
        Intent intent = new Intent(TwitterConstants.ON_NEW_FRIEND_BROADCAST);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void scheduleAlarmReceiver() {
        Log.v(TAG, "Setting up alarm receiver");

        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getActivity(), 0, new Intent(getActivity().getApplicationContext(), TwitterUpdateReceiver.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        // Use inexact repeating which is easier on battery (system can phase events and not wake at exact times)
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60, pendingIntent);
//		      setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, TwitterConstants.ALARM_TRIGGER_AT_TIME,
//		    		  TwitterConstants.ALARM_INTERVAL, pendingIntent);
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

        _dataAdapter = new FriendsCursorAdapter(getActivity(), R.layout.listview_for_twitter,
                null, columns, to, 0, _sicImageLoader);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        String[] pro = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, UserFriendsDao.FULLY_QUALIFIED_PROJECTIONS);
        Log.v(TAG, "on loader create user is: " + getCurrentUser().toString());
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_USER_TO_FRIEND, pro,
                UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s() + "." + UsersToFriendsColumn.USER_ID.s() + "=?",
                new String[]{"" + getCurrentUser().getUserId()}, FriendColumn._ID.s() + " ASC " + " LIMIT " + _totalFriendCount);
        return cursorLoader;
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
    public boolean shouldLoad(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (_totalFriendCount < getCurrentUser().getCurrentFriendCount()) {
            Log.v(TAG, "_totalFriendCount: " + _totalFriendCount);
            _totalFriendCount += totalItemCount;
            restartCursor();
            return false;
        } else {
            Log.v(TAG, "going to load more friends for:" + getCurrentUser());
            return true;
        }
    }

    @Override
    public void onLoad(Collection<Future<ParcelableUser>> futureTask_) {
        Log.v(TAG, "On load startyed with future size:" + futureTask_.size());

        Collection<IDBDao<ParcelableUser>> daos = new ArrayList<IDBDao<ParcelableUser>>();
        daos.add(_usersToFriendDao);
        daos.add(_friendDao);

        AsyncFutureDBUpdatetask<ParcelableUser, Integer> updatetask =
                new AsyncFutureDBUpdatetask<ParcelableUser, Integer>(1, TimeUnit.MINUTES, daos, _userUpdater);
        updatetask.execute(futureTask_.toArray(new Future[futureTask_.size()]) );
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

        Intent i = new Intent(getActivity(), TwitterFilttrUserHome.class);
        i.putExtra(TwitterConstants.FRIENDS_BUNDLE, newFriend);
        startActivity(i);

        //broadcastNewUser(getCurrentUser(), rowId);
    }

    @Override
    public void onRefreshStarted(View view) {

    }
}

