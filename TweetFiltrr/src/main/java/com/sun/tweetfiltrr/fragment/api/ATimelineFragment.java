package com.sun.tweetfiltrr.fragment.api;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Window;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.TweetConversation;
import com.sun.tweetfiltrr.activity.adapter.UserTimelineCursorAdapter;
import com.sun.tweetfiltrr.asyncretriever.api.TweetRetrieverWrapper;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.cursorToParcelable.FriendTimeLineToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.api.IUserUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.TimelineUserUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.UserUpdater;
import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;
import com.sun.tweetfiltrr.imageprocessor.IProcessScreenShot;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory.DaoFactory;
import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public abstract class ATimelineFragment extends SherlockFragment implements LoaderCallbacks<Cursor>, TabListener,
        IProcessScreenShot, AdapterView.OnItemClickListener, PullToRefreshView.OnNewTweetRefreshListener<Collection<ParcelableUser>>,
        LoadMoreOnScrollListener.LoadMoreListener<Collection<ParcelableUser>> {

    private static final String TAG = ATimelineFragment.class.getName();
    private SimpleCursorAdapter _dataAdapter;
    private static final int TUTORIAL_LIST_LOADER = 0x04;
    private int _currentLimitCount = 50;
    private IDBDao<ParcelableTimeLineEntry> _timelineDao;
    private IDBDao<ParcelableUser> _friendDao;
    private ThreadPoolExecutor _threadExecutor;
    private TweetRetrieverWrapper _tweetRetriver;
    private IDBUpdater<ParcelableTimeLineEntry> _timelineBufferedDBUpdater;
    private UrlImageLoader _sicImageLoader;
    private PullToRefreshView _pullToRefreshHandler;
    private boolean _isFinishedLoading = false;
    private ParcelableUser _currentUser ;
    private Collection<IUserUpdater> _userDaoUpdaters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControl();
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = _pullToRefreshHandler.onCreateViewCallback(inflater, container, savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);
        return rootView;
    }

    public TweetRetrieverWrapper getTweetRetriver() {
        return _tweetRetriver;
    }

    protected void initControl() {
        DaoFlyWeightFactory flyWeight = DaoFlyWeightFactory
                .getInstance(getActivity().getContentResolver());
        _currentUser = UserRetrieverUtils.getCurrentLoggedInUser(getActivity());

        _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();
        _sicImageLoader = TwitterUtil.getInstance().getGlobalImageLoader(getActivity());

        //init the Dao object using the flyweight so that we can share the Dao's between different fragments
        _timelineDao = new TimelineDao(getActivity().getContentResolver(), new TimelineToParcelable());
        _friendDao = (IDBDao<ParcelableUser>) flyWeight.getDao(
                DaoFactory.FRIEND_DAO, _currentUser);

        ThreadLocal<SimpleDateFormat> simpleDateFormatLocal = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal();

        _timelineBufferedDBUpdater =
                new SimpleDBUpdater<ParcelableTimeLineEntry>();

        //initialise TweetRetrieverWrapper to easy tweet retrieval
        _tweetRetriver = new TweetRetrieverWrapper(_threadExecutor, simpleDateFormatLocal);
        _userDaoUpdaters = new ArrayList<IUserUpdater>();
        _userDaoUpdaters.add(new TimelineUserUpdater(_timelineDao));
        _userDaoUpdaters.add(new UserUpdater(_friendDao));


    }


    protected void initAdapter() {

            String[] columns = new String[]{
                    "_id",
                    TimelineColumn.TIMELINE_TEXT.a(),
                    TimelineColumn.IN_REPLY_SCREENNAME.a(),
                    TimelineColumn.IN_REPLY_TWEETID.a(),
                    TimelineColumn.IN_REPLY_USERID.a()
            };

            // the XML defined views which the data will be bound to
            int[] to = new int[]{
                    R.id.timeline_friend_name,
                    R.id.timeline_entry,

            };

            FriendTimeLineToParcelable friendTimeLineToParcelable = new FriendTimeLineToParcelable(new FriendToParcelable(),
                    new TimelineToParcelable());

            _dataAdapter = new UserTimelineCursorAdapter(getActivity(), R.layout.user_timeline_list_view_row,
                    null, columns, to, 0, friendTimeLineToParcelable, _sicImageLoader);

            _pullToRefreshHandler = getPullToRefreshView(_dataAdapter, _currentUser);


    }

    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_, ParcelableUser currentUser_){
        return new PullToRefreshView(getActivity(), currentUser_, this, adapter_ ,this, this);
    }

    @Override
    public boolean shouldLoad(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int rowCount = _currentUser.getTotalTweetCount() - _currentLimitCount;
        Log.v(TAG, "current rowcount " + rowCount + " current user tweets " + _currentUser.getTotalTweetCount() + "with current rowlimit" + _currentLimitCount);

        if(rowCount > 0){
            Log.v(TAG, "increasing limit because we have enough tweets with total: " + _currentUser.getTotalTweetCount() + " and current limit: " +_currentLimitCount );
            _currentLimitCount += 50;
            restartCursor();
            return false;
        }else if(_isFinishedLoading){

            Log.v(TAG, "not looing fro tweets onscroll, new limit count: " + _currentLimitCount);
            return false;
        }else{
            Log.v(TAG, "looking for tweets now with row count: " + _currentLimitCount);
            return true; //TODO need to make sure we only search when we need to, maybe prevent too much searching
        }
    }


    @Override
    public void onLoad(Collection<Future<Collection<ParcelableUser>>> futureTask_) {
        AsyncUserDBUpdateTask<Integer> _userDBUpdater =  new AsyncUserDBUpdateTask<Integer>(3 , TimeUnit.MINUTES ,
                _userDaoUpdaters,  _pullToRefreshHandler);
        _userDBUpdater.execute(futureTask_.toArray(new Future[futureTask_.size()]));
    }

    private void restartCursor() {
        getActivity().getSupportLoaderManager().restartLoader(TUTORIAL_LIST_LOADER, null, this);
        _dataAdapter.notifyDataSetChanged();
    }



    @Override
    public void onLoadFinished(Loader<Cursor> arg, Cursor cursor) {
        Log.v(TAG, "loadfinished" + cursor.getCount());
        _dataAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        Log.v(TAG, "loaderreset" );
        _dataAdapter.swapCursor(null);

    }

    @Override
    public Bitmap processScreenShot(Bitmap input_) {

        int contentViewTop = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop(); /* skip status bar in screenshot */
        return Bitmap.createBitmap(input_, 0, contentViewTop, input_.getWidth(), input_.getHeight() - contentViewTop, null, true);

    }

   @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        Log.v(TAG, "col names from cursor: " + Arrays.toString(cursor.getColumnNames())) ;
        int rowId =
                cursor.getInt(cursor.getColumnIndexOrThrow(FriendColumn._ID.s()));
        int tweetID =
                cursor.getInt(cursor.getColumnIndexOrThrow(TimelineColumn._ID.a()));
        Collection<ParcelableUser> friends = _friendDao.getEntry(rowId);
        Collection<ParcelableTimeLineEntry> tweets = _timelineDao.getEntry(tweetID);
        //we should only retrieve 1 friend since rowId is unique, so we iterate once
        ParcelableUser user = friends.iterator().next();
        ParcelableTimeLineEntry tweet = tweets.iterator().next();
        user.setRowId(rowId);
        user.addTimeLineEntry(tweet);
        Log.v(TAG, "user queroed is :" + user.toString());
        Log.v(TAG, "user's timeline size is :" + user.getUserTimeLine().size());

        Intent tweetConvo = new Intent(getActivity(), TweetConversation.class);
        tweetConvo.putExtra(TwitterConstants.FRIENDS_BUNDLE, user);
        getActivity().startActivity(tweetConvo);
    }

    @Override
    public void OnRefreshComplete(Collection<ParcelableUser> twitterParcelable) {
        int totalNewTweets =0 ;

        for(ParcelableUser user : twitterParcelable){
            if(_currentUser.getUserId() == user.getUserId()){
                _currentUser = user;
                Log.v(TAG, "user passed for switch  " + user.toString());

                Log.v(TAG, "current user switch to  " + _currentUser.toString());

            }
            totalNewTweets = user.getUserTimeLine().size();
        }

        Log.v(TAG, "on refresh completed timeline frag qith size " + totalNewTweets);

        _isFinishedLoading = totalNewTweets <= 1;
        _currentLimitCount += totalNewTweets;
        restartCursor();
    }

    protected int getTimeLineCount(){
        return _currentLimitCount;
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.v(TAG, "Tab has been selected");
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    protected ParcelableUser getCurrentUser(){
        return _currentUser;
    }
}
