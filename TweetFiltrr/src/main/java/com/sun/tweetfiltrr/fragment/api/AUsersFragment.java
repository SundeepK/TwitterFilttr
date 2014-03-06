package com.sun.tweetfiltrr.fragment.api;

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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.UserProfileHomeActivity;
import com.sun.tweetfiltrr.activity.adapter.FriendsCursorAdapter;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.customviews.views.ZoomListView;
import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.callables.FriendsRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.UsersFriendRetriever;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import twitter4j.TwitterException;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;


public abstract class AUsersFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        PullToRefreshView.OnNewTweetRefreshListener<Collection<ParcelableUser>>,
        LoadMoreOnScrollListener.LoadMoreListener<Collection<ParcelableUser>>,ITwitterAPICallStatus {

    private int _currentFriendLimit = 50;
    private static final String TAG = AUsersFragment.class.getName();
    private SimpleCursorAdapter _dataAdapter;
    private long _currentLoggedInUserId;
    private ITwitterAPICall<Collection<ParcelableUser>>  _userRetriever;
    private PullToRefreshView _pullToRefreshHandler;
    private boolean _isFinishedLoading;
    private Collection<IDatabaseUpdater> _updaters = new ArrayList<IDatabaseUpdater>();
    private boolean _tabHasBeenSelected = false;
    private ArrayList<ParcelableUser> _userQueue; // not a queue but going to use it like one
    private ParcelableUser _currentUser;
    private boolean _isCursorReady;
    @Inject FriendDao _friendDao;
    @Inject UrlImageLoader _sicImageLoader;


    protected abstract int getLoaderID();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControl();
        initAdapter();
    }

    protected ParcelableUser getCurrentUser()
    {
        return _currentUser;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Make sure that we are currently visible
        //do a long check before we are sure that we can automatically search for friends
        if (this.isVisible()) {
            // about to become visible
            if (!isVisibleToUser) {
                Log.d(TAG, "Not visible anymore");
            }else{
                Log.d(TAG, "Visible now!");
                if(_tabHasBeenSelected == false){
                    _tabHasBeenSelected = true;
                    if(_currentUser != null){
                        if((_currentUser.getCurrentFriendCount() <= 0)){
                            _pullToRefreshHandler.startRefresh();
                        }
                    }
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = _pullToRefreshHandler.onCreateViewCallback(inflater, container, savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(getLoaderID(), null, this);
        return rootView;
    }

    //Get correct UsersFriendRetriever based on if the user is looking at their profile
    private ITwitterAPICall<Collection<ParcelableUser>> getRetriever(){
        if (_currentUser.getUserId() == _currentLoggedInUserId) {
            //current logged in user
            return new UsersFriendRetriever( true);
        } else {
            return new UsersFriendRetriever(false);
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
        getActivity().getSupportLoaderManager().restartLoader(getLoaderID(), null, this);
        _dataAdapter.notifyDataSetChanged();
    }

    protected void initControl() {
        ((TweetFiltrrApplication) getActivity().getApplication()).getObjectGraph().inject(this);

        _currentLoggedInUserId = TwitterUtil.getInstance().getCurrentLoggedInUserId(getActivity());
        _userQueue = UserRetrieverUtils.getUserQueue(getActivity());

        if(_userQueue.isEmpty()){
            Log.v(TAG, "user queue is empty");
            _currentUser = UserRetrieverUtils.getCurrentFocusedUser(getActivity());
        }else{
            _currentUser = _userQueue.get(_userQueue.size()-1);
            Log.v(TAG, "user queue contains user" + _currentUser.getScreenName());
        }

        Log.v(TAG, "Current user is :" + _currentUser);
        _userRetriever = getRetriever();

        _updaters = getDBUpdaters();

        Collection<ParcelableUser> users = UserRetrieverUtils.getUserFromDB(_friendDao, _currentUser);
        if(!users.isEmpty()){
            _currentUser = users.iterator().next();
            Log.v(TAG, "user taken from db is :" + _currentUser);
            _currentFriendLimit = (_currentUser.getCurrentFriendCount()  > 0 && _currentUser.getCurrentFriendCount() <=  100 )? _currentUser.getCurrentFriendCount(): 100;
        }
    }

    protected abstract Collection<IDatabaseUpdater> getDBUpdaters();


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
        _pullToRefreshHandler = getPullToRefreshView(_dataAdapter, _currentUser, listener, _updaters);

    }

    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_,
                                                     ParcelableUser currentUser_,
                                                     ZoomListView.OnItemFocused listener,Collection<IDatabaseUpdater> updaters_ ){
        return new PullToRefreshView<Collection<ParcelableUser>>
                (getActivity(), currentUser_, this, adapter_ ,this, this, listener, updaters_, _sicImageLoader, R.layout.generic_empty_custom_timeline_layout);
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
        _isCursorReady = true;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        _dataAdapter.swapCursor(null);
    }

    @Override
    public boolean shouldLoadMoreOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if(_isCursorReady){
            if (_currentFriendLimit < _currentUser.getCurrentFriendCount()) {
                Log.v(TAG, "_currentFriendLimit: " + _currentFriendLimit + " current friend acount " +  _currentUser.getCurrentFriendCount());
                int diff = _currentUser.getCurrentFriendCount()-  _currentFriendLimit;
                if(diff > 100){
                    _currentFriendLimit += 100;
                }else{
                    _currentFriendLimit += diff;
                }
                _isCursorReady = false;
                restartCursor();
                return false;
            } else if (_isFinishedLoading){
                Log.v(TAG, "not looing fro tweets onscroll, new limit count: " + _currentFriendLimit);
                return false;
            }else{
                Log.v(TAG, "going to load more friends for:" + _currentUser);
                return true;
            }
        }else{
            Log.v(TAG, "_tabHasBeenSelected is false");
            return false;
        }


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
        //start new activity for the clicked user
        Intent i = new Intent(getActivity(), UserProfileHomeActivity.class);
        i.putExtra(TwitterConstants.FRIENDS_BUNDLE, newFriend);
        _userQueue.add(newFriend);
        i.putExtra(TwitterConstants.PARCELABLE_USER_QUEUE, _userQueue);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    @Override
    public void OnRefreshComplete(Collection<ParcelableUser> twitterParcelable) {
        Log.v(TAG, "on refresh completed with count:" + twitterParcelable.size());
        int totalFriendsReturned = twitterParcelable.size();
        _isFinishedLoading = (totalFriendsReturned <= 1);
        _currentFriendLimit += totalFriendsReturned;
        if(this.getActivity() != null){
            Log.v(TAG, "activity not null so restarting");
            restartCursor();
        }
    }

    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getUsersRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        callables.add(new FriendsRetriever(_currentUser, _userRetriever, this));
        return callables;
    }

    protected int getTimeLineCount(){
        return _currentFriendLimit;
    }

    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_, ITwitterAPICall apiCallType_) {
    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall apiCallType_) {
        //add generic error
        Toast.makeText(getActivity(), "Problem connecting to twitter", 2).show();
    }
}

