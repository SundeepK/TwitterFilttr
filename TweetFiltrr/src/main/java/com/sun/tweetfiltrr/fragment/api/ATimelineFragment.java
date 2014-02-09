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
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Window;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.PostTweetActivity;
import com.sun.tweetfiltrr.activity.activities.TweetConversation;
import com.sun.tweetfiltrr.activity.adapter.UserTimelineCursorAdapter;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.twitter.retrievers.api.TweetRetrieverWrapper;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.cursorToParcelable.FriendTimeLineToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.TimelineDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;
import com.sun.tweetfiltrr.imageprocessor.IProcessScreenShot;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.twitter.tweetoperations.TweetOperationHandler;
import com.sun.tweetfiltrr.twitter.tweetoperations.TweetOperationTask;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITweetOperation;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;
import com.sun.tweetfiltrr.customviews.ZoomListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import twitter4j.TwitterException;

import static com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory.DaoFactory;
import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public abstract class ATimelineFragment extends SherlockFragment implements LoaderCallbacks<Cursor>, TabListener,
        IProcessScreenShot, AdapterView.OnItemClickListener, PullToRefreshView.OnNewTweetRefreshListener<Collection<ParcelableUser>>,
        LoadMoreOnScrollListener.LoadMoreListener<Collection<ParcelableUser>>,SingleTweetAdapter.OnTweetOperation, TweetOperationTask.TwitterTaskListener {

    private static final String TAG = ATimelineFragment.class.getName();
    private SimpleCursorAdapter _dataAdapter;
    private static final int TUTORIAL_LIST_LOADER = 0x04;
    private int _currentLimitCount = 50;
    private IDBDao<ParcelableTweet> _timelineDao;
    private IDBDao<ParcelableUser> _friendDao;
    private ThreadPoolExecutor _threadExecutor;
    private TweetRetrieverWrapper _tweetRetriver;
    private IDBUpdater<ParcelableTweet> _timelineBufferedDBUpdater;
    private UrlImageLoader _sicImageLoader;
    private PullToRefreshView _pullToRefreshHandler;
    private boolean _isFinishedLoading = false;
    private ParcelableUser _currentUser ;
    private Collection<IDatabaseUpdater> _userDaoUpdaters;
    private SingleTweetAdapter.OnTweetOperation _onTweetOperationLis;
    private boolean _tabHasBeenSelected = false;

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
                new SimpleDBUpdater<ParcelableTweet>();

        //initialise TweetRetrieverWrapper to easy tweet retrieval
        _tweetRetriver = new TweetRetrieverWrapper(_threadExecutor, simpleDateFormatLocal);
        _userDaoUpdaters = new ArrayList<IDatabaseUpdater>();
        _userDaoUpdaters.add(new TimelineDatabaseUpdater(_timelineDao));
        String[] cols = new String[]{FriendColumn.FRIEND_ID.s(),
                FriendColumn.TWEET_COUNT.s(), FriendColumn.COLUMN_MAXID.s(), FriendColumn.COLUMN_SINCEID.s(),
                FriendColumn.MAXID_FOR_MENTIONS.s(), FriendColumn.SINCEID_FOR_MENTIONS.s(), FriendColumn.FRIEND_COUNT.s()
                , FriendColumn.FRIEND_NAME.s(), FriendColumn.FRIEND_SCREENNAME.s(), FriendColumn.PROFILE_IMAGE_URL.s(),
                FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(), FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendColumn.DESCRIPTION.s()};
        _userDaoUpdaters.add(new DatabaseUpdater(_friendDao,cols ));


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

            UserTimelineCursorAdapter timelineCursorAdapter = new UserTimelineCursorAdapter(getActivity(), R.layout.user_timeline_list_view_row,
                    null, columns, to, 0, friendTimeLineToParcelable, _sicImageLoader, this);
            _dataAdapter = timelineCursorAdapter;
            ZoomListView.OnItemFocused listener = timelineCursorAdapter;
            _pullToRefreshHandler = getPullToRefreshView(_dataAdapter, _currentUser,listener, _userDaoUpdaters);
            _onTweetOperationLis = new TweetOperationHandler(_pullToRefreshHandler, _timelineDao, this);

    }

    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_, ParcelableUser currentUser_,
                                                     ZoomListView.OnItemFocused listener_, Collection<IDatabaseUpdater> userDaoUpdaters_){
        return new PullToRefreshView(getActivity(), currentUser_, this, adapter_ ,this, this,listener_, userDaoUpdaters_ );
    }

    @Override
    public boolean shouldLoadMoreOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
        Collection<ParcelableTweet> tweets = _timelineDao.getEntry(tweetID);
        //we should only retrieve 1 friend since rowId is unique, so we iterate once
        ParcelableUser user = friends.iterator().next();
        ParcelableTweet tweet = tweets.iterator().next();
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
            Log.v(TAG, "size of timeline " + user.getUserTimeLine().size() + " for user " + user.getScreenName());
            totalNewTweets = totalNewTweets + user.getUserTimeLine().size();
        }

        Log.v(TAG, "on refresh completed timeline frag qith size " + totalNewTweets);

        _isFinishedLoading = (totalNewTweets <= 1 && !_tabHasBeenSelected);
        _currentLimitCount += totalNewTweets;
        restartCursor();
    }

    protected int getTimeLineCount(){
        return _currentLimitCount;
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.v(TAG, "TimeLine tab has been selected");
        if(!_tabHasBeenSelected){
            _tabHasBeenSelected = true; //the tab has been selected, so lets allow network calls
        }
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

    @Override
    public void onTweetFav(View view_, ParcelableUser user_) {
        _onTweetOperationLis.onTweetFav(view_, user_);
    }

    @Override
    public void onReTweet(View view_, ParcelableUser user_) {
        _onTweetOperationLis.onReTweet(view_, user_);

    }

    @Override
    public void onReplyTweet(View view_, ParcelableUser user_) {
        Intent tweetConvo = new Intent(getActivity(), PostTweetActivity.class);
        tweetConvo.putExtra(TwitterConstants.PARCELABLE_FRIEND_WITH_TIMELINE, user_);
        tweetConvo.putExtra(TwitterConstants.IS_QUOTE_REPLY, false);
        getActivity().startActivity(tweetConvo);
    }

    @Override
    public void onQuoteTweet(View view_, ParcelableUser user_) {
        Intent tweetConvo = new Intent(getActivity(), PostTweetActivity.class);
        tweetConvo.putExtra(TwitterConstants.PARCELABLE_FRIEND_WITH_TIMELINE, user_);
        tweetConvo.putExtra(TwitterConstants.IS_QUOTE_REPLY, true);
        getActivity().startActivity(tweetConvo);
    }

    @Override
    public void onTaskSuccessfulComplete(ParcelableTweet tweet_) {
        String message  = "Tweet successful";


        Toast.makeText(getActivity(), message, 2).show();
    }

    @Override
    public void onTaskFail(ParcelableTweet failedTweet_, TwitterException exception_, ITweetOperation operation_) {
        String message ;
        if(operation_.getTweetOperationType() == ITweetOperation.TweetOperationType.RETWEET){
            message = "Retweet failed";
        }else if(operation_.getTweetOperationType() == ITweetOperation.TweetOperationType.FAVOURITE) {
            message = "Favourite tweet failed";
        }else{
            message = "Tweet failed";
        }
        
        Toast.makeText(getActivity(), message, 2).show();
    }


}
