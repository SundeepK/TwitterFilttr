package com.sun.tweetfiltrr.fragment.api;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.PostTweetActivity;
import com.sun.tweetfiltrr.activity.activities.TweetConversation;
import com.sun.tweetfiltrr.activity.adapter.UserTimelineCursorAdapter;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.cursorToParcelable.FriendTimeLineToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.customviews.ZoomListView;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.TimeLineDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.TimelineDatabaseUpdater;
import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.scrolllisteners.LoadMoreOnScrollListener;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.TweetOperationController;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.TweetRetrieverWrapper;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import twitter4j.TwitterException;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public abstract class ATimeLineFragment extends SherlockFragment implements LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, PullToRefreshView.OnNewTweetRefreshListener<Collection<ParcelableUser>>,
        LoadMoreOnScrollListener.LoadMoreListener<Collection<ParcelableUser>>,SingleTweetAdapter.OnTweetOperation,
        ITwitterAPICallStatus {

    private static final String TAG = ATimeLineFragment.class.getName();
    private SimpleCursorAdapter _dataAdapter;
    private static final int TUTORIAL_LIST_LOADER = 0x04;
    private int _currentLimitCount = 50;
    private IDBUpdater<ParcelableTweet> _timeLineDBUpdater;
    private PullToRefreshView _pullToRefreshHandler;
    private boolean _isFinishedLoading = false;
    private ParcelableUser _currentUser ;
    private Collection<IDatabaseUpdater> _userDaoUpdaters;
    private SingleTweetAdapter.OnTweetOperation _onTweetOperationLis;
    private boolean _tabHasBeenSelected = false;
    private ArrayList<ParcelableUser> _userQueue; // not a queue but going to use it like one
    private boolean _isCursorReady;

    @Inject TweetRetrieverWrapper _tweetRetriver;
    @Inject FriendDao _friendDao;
    @Inject
    TimeLineDao _timeLineDao;
    @Inject UrlImageLoader _sicImageLoader;

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
      //  _currentUser = UserRetrieverUtils.getCurrentFocusedUser(getActivity());

        ((TweetFiltrrApplication) getActivity().getApplication()).getObjectGraph().inject(this);

        _userQueue = UserRetrieverUtils.getUserQueue(getActivity());

        if(_userQueue.isEmpty()){
            Log.v(TAG, "user queue is empty");
            _currentUser = UserRetrieverUtils.getCurrentFocusedUser(getActivity());
        }else{
            _currentUser = _userQueue.get(_userQueue.size()-1);
            Log.v(TAG, "user queue contains user" + _currentUser.getScreenName());
        }

        _timeLineDBUpdater =
                new SimpleDBUpdater<ParcelableTweet>();

        _userDaoUpdaters = new ArrayList<IDatabaseUpdater>();
        _userDaoUpdaters.add(new TimelineDatabaseUpdater(_timeLineDao));
        String[] cols = new String[]{FriendColumn.FRIEND_ID.s(),
                FriendColumn.TWEET_COUNT.s(), FriendColumn.COLUMN_MAXID.s(), FriendColumn.COLUMN_SINCEID.s(),
                FriendColumn.MAXID_FOR_MENTIONS.s(), FriendColumn.SINCEID_FOR_MENTIONS.s(), FriendColumn.FRIEND_COUNT.s()
                , FriendColumn.FRIEND_NAME.s(), FriendColumn.FRIEND_SCREENNAME.s(), FriendColumn.PROFILE_IMAGE_URL.s(),
                FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(), FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendColumn.DESCRIPTION.s()};
        _userDaoUpdaters.add(new DatabaseUpdater(_friendDao,cols ));

        Collection<ParcelableUser> users = UserRetrieverUtils.getUserFromDB(_friendDao, _currentUser);

        if(!users.isEmpty()){
            _currentUser = users.iterator().next();
            _currentLimitCount = _currentUser.getTotalTweetCount() > 0 ? _currentUser.getTotalTweetCount(): 50;
            Log.v(TAG, "user queried from db" + _currentUser.toString());
        }

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
            _onTweetOperationLis = new TweetOperationController(_pullToRefreshHandler, _timeLineDao, this);

    }

    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_, ParcelableUser currentUser_,
                                                     ZoomListView.OnItemFocused listener_, Collection<IDatabaseUpdater> userDaoUpdaters_){
        return new PullToRefreshView(getActivity(), currentUser_, this, adapter_ ,this, this,listener_, userDaoUpdaters_, _sicImageLoader );
    }

    @Override
    public boolean shouldLoadMoreOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int rowCount = _currentUser.getTotalTweetCount() - _currentLimitCount;
        Log.v(TAG, "current rowcount " + rowCount + " current user tweets " + _currentUser.getTotalTweetCount() + "with current rowlimit" + _currentLimitCount);

        if(!_isCursorReady){
            //make sure to check if our cursor is actually loaded
            return false;
        }else if(rowCount >= 0){
            Log.v(TAG, "increasing limit because we have enough tweets with total: " + _currentUser.getTotalTweetCount() + " and current limit: " +_currentLimitCount );
            _currentLimitCount += 50;
            restartCursor();
            return false;
        }else if(_isFinishedLoading){
            Log.v(TAG, "not looing fro tweets onscroll, new limit count: " + _currentLimitCount);
            return false;
        }else{
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
        _isCursorReady = true;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        Log.v(TAG, "loaderreset" );
        _dataAdapter.swapCursor(null);

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
        Collection<ParcelableTweet> tweets = _timeLineDao.getEntry(tweetID);
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
                totalNewTweets = totalNewTweets + user.getUserTimeLine().size();
            }
            Log.v(TAG, "size of timeline " + user.getUserTimeLine().size() + " for user " + user.getScreenName());
        }

        Log.v(TAG, "on refresh completed timeline frag qith size " + totalNewTweets);

        _isFinishedLoading = (totalNewTweets <= twitterParcelable.size());
        _currentLimitCount += totalNewTweets;

        if(this.getActivity() != null){
            restartCursor();
        }


    }

    protected int getTimeLineCount(){
        return _currentLimitCount;
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

    public void onTaskSuccessfulComplete(ParcelableTweet tweet_) {
        String message  = "Tweet successful";
        Toast.makeText(getActivity(), message, 2).show();
    }

    public void onTaskFail(ParcelableTweet failedTweet_, TwitterException exception_, ITwitterAPICall.TwitterAPICallType tweetType_) {
        String message ;


    }

    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_) {
//        ITwitterAPICall.TwitterAPICallType twitterApiCallType = tweetType_.getTweetOperationType();
//        String message;
//        switch (twitterApiCallType){
//            case POST_RETWEET:
//                message = "Retweet failed";
//                break;
//            case POST_FAVOURITE:
//                message = "Favourite tweet failed";
//                break;
//            case POST_TWEET:
//                message = "Failed to post tweet";
//                break;
//            case GET_TIMELINE:
//                message = "Failed to refreshed timeline";
//                break;
//            default:
//                message = "Tweet failed";
//                break;
//        }
        Toast.makeText(getActivity(), "Successfully posted tweet", 2).show();
    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall tweetType_) {
        ITwitterAPICall.TwitterAPICallType twitterApiCallType = tweetType_.getTweetOperationType();
        String message;
        switch (twitterApiCallType){
            case POST_RETWEET:
                message = "Retweet failed";
                break;
            case POST_FAVOURITE:
                message = "Favourite tweet failed";
                break;
            case POST_TWEET:
                message = "Failed to post tweet";
                break;
            case GET_TIMELINE:
                message = "Failed to refreshed timeline";
                break;
            default:
                message = "Tweet failed";
                break;
        }

        Toast.makeText(getActivity(), message, 2).show();
    }
}
