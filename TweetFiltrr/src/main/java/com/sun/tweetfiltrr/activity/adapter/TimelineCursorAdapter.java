package com.sun.tweetfiltrr.activity.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.ConversationRetriever;
import com.sun.tweetfiltrr.database.dao.api.IDBDao;
import com.sun.tweetfiltrr.imageprocessor.IImageProcessor;
import com.sun.tweetfiltrr.imageprocessor.IOnImageProcessCallback;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.screencap.IScreenCapGenerator;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.concurrent.ThreadPoolExecutor;

import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class TimelineCursorAdapter extends SimpleCursorAdapter  {

	
    private static final String TAG = TimelineCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private UrlImageLoader _imageLoadermine;
    private ParcelableUser _currentFriend;
    private IDBDao<ParcelableTweet> _timelineDao;
	private ThreadPoolExecutor _executor;
	private ConversationRetriever.OnConvoLoadListener _convoLoadLis;
	private Handler _currentHandler;
	private BroadcastReceiver _broadCastReceiver;
	private int _layout;
    private IImageProcessor _blurredImageProcessor;
    private IScreenCapGenerator _screenCapGenerator;
    private IOnImageProcessCallback _imageProcessCallback;

	public TimelineCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags, ParcelableUser currentFriend_,
			ConversationRetriever.OnConvoLoadListener convoLoadLis_,	Handler currentHandler_,
            IDBDao<ParcelableTweet> timelineDao_,
            IImageProcessor blurredImageProcessor_, IScreenCapGenerator screenCapGenerator_,
            IOnImageProcessCallback imageProcessCallback_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _currentFriend = currentFriend_;
        _timelineDao = timelineDao_;
        _executor = TwitterUtil.getInstance().getGlobalExecutor();
        _convoLoadLis = convoLoadLis_;
        _currentHandler = currentHandler_;
        _layout = layout;
        _blurredImageProcessor =blurredImageProcessor_;
        _screenCapGenerator = screenCapGenerator_;
        _imageProcessCallback = imageProcessCallback_;
			_broadCastReceiver = new BroadcastReceiver() {
			  @Override
			  public void onReceive(Context context, Intent intent) {
			    // Get extra data included in the Intent
				  onBroadCastReceive(intent);
			  }
			};

			  LocalBroadcastManager.getInstance(context).registerReceiver(_broadCastReceiver,
				      new IntentFilter(TwitterConstants.ON_NEW_FRIEND_BROADCAST));

	}

    public TimelineCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to, int flags, ParcelableUser currentFriend_,
                                 ConversationRetriever.OnConvoLoadListener convoLoadLis_,	Handler currentHandler_,
                                 IDBDao<ParcelableTweet> timelineDao_,
                                 IImageProcessor blurredImageProcessor_) {
        super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _currentFriend = currentFriend_;
        _timelineDao = timelineDao_;
        _executor = TwitterUtil.getInstance().getGlobalExecutor();
        _convoLoadLis = convoLoadLis_;
        _currentHandler = currentHandler_;
        _layout = layout;
        _blurredImageProcessor =blurredImageProcessor_;
        _broadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                onBroadCastReceive(intent);
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(_broadCastReceiver,
                new IntentFilter(TwitterConstants.ON_NEW_FRIEND_BROADCAST));

    }


	protected void onBroadCastReceive(Intent intent_) {
		_currentFriend = intent_.getExtras().getParcelable(TwitterConstants.FRIENDS_BUNDLE);
	}


	public void setCurrentUser(ParcelableUser user_){
		_currentFriend   = user_;
	}

	@Override
	public void bindView(View view_, Context context, Cursor cursor_) {

		ParcelableTweet tweet = getParcelable(cursor_);
		TextView friendName=(TextView)view_.findViewById(R.id.timeline_friend_name);

		TextView dateTime=(TextView)view_.findViewById(R.id.timeline_date_time);
		dateTime.setText(tweet.getTweetDate());

		if( tweet.getFriendID() ==_currentFriend.getUserId()){
			friendName.setText(_currentFriend.getUserName() + " @" + _currentFriend.getScreenName());
		}

        TextView tweetTextView =(TextView)view_.findViewById(R.id.timeline_entry);
        tweetTextView.setText(tweet.getTweetText());

        if(TextUtils.isEmpty(tweet.getTweetText())){
        	Log.v(TAG, " desc is null");
        }


//        if(tweet.getInReplyToUserId() > 0 && !TextUtils.isEmpty(tweet.getInReplyToScreenName()) && tweet.getInReplyToTweetId() > 0){
//            Button conversation =(Button)view_.findViewById(R.id.show_conversation);
//            conversation.setVisibility(View.VISIBLE);
//            ParcelableUser friend = new ParcelableUser(_currentFriend.getUserId(), _currentFriend.getUserName(), _currentFriend.getScreenName());
//            friend.addTimeLineEntry(tweet);
//            conversation.setOnClickListener(getConversationLis(friend, _timelineDao, _executor, _convoLoadLis, _currentHandler));
//        }else{
//            Button conversation =(Button)view_.findViewById(R.id.show_conversation);
//            conversation.setVisibility(View.INVISIBLE);
//        }
//
	}


	private OnClickListener getConversationLis(final ParcelableUser currentFriend_,
			final IDBDao<ParcelableTweet> timelineDao_, final ThreadPoolExecutor executor_, final ConversationRetriever.OnConvoLoadListener listener_, final Handler currentHandler_){
		return new OnClickListener() {

			@Override
			public void onClick(View v) {

				ConversationRetriever convoRetriever = new ConversationRetriever(currentFriend_, timelineDao_, listener_, currentHandler_);
				executor_.execute(convoRetriever);
//                Log.v(TAG, "Current android version is" + Build.VERSION.SDK_INT);
//                if (Build.VERSION.SDK_INT > 16) {
//                    _currentHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Bitmap bmp = _blurredImageProcessor.processImage(_screenCapGenerator.generateScreenCap());
//                            _imageProcessCallback.OnImageProcessFinish(bmp);
//                        }
//                    });
//                }

			}
		};
	}

	protected ParcelableTweet getParcelable(Cursor cursorTimeline_) {
        long friendID = cursorTimeline_.getLong(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.FRIEND_ID.a()));
        long tweetID = cursorTimeline_.getLong(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.TWEET_ID.a()));
        String tweetDate = cursorTimeline_.getString(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.DATETIME_INSERTED.a()));
        String tweetText = cursorTimeline_.getString(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.TIMELINE_TEXT.a()));
        long inReplyToUserId = cursorTimeline_.getLong(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IN_REPLY_USERID.a()));
        long inReplyToTweetId = cursorTimeline_.getLong(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IN_REPLY_TWEETID.a()));
        String inReplyToScreenName = cursorTimeline_.getString(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IN_REPLY_SCREENNAME.a()));
        String photoUrl = cursorTimeline_.getString(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.PHOTO_URL.a()));
        boolean isFav = cursorTimeline_.getInt(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IS_FAVOURITE.a())) == 1 ;
        boolean isRetweeted = cursorTimeline_.getInt(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IS_RETWEETED.a())) == 1 ;
        boolean ismention = cursorTimeline_.getInt(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IS_MENTION.a())) == 1 ;

        return new ParcelableTweet(tweetText, tweetDate, tweetID, friendID,
                inReplyToScreenName,inReplyToTweetId,inReplyToUserId, photoUrl, isFav, isRetweeted , ismention);
	}


	
	@Override
	public View newView(Context context, Cursor  cursor, ViewGroup parent) {
		final View view=_inflater.inflate(_layout,parent,false); 
        return view;
	}




}
