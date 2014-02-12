package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.cursorToParcelable.FriendTimeLineToParcelable;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.ConversationRetriever;
import com.sun.tweetfiltrr.customviews.ZoomListView;

import java.net.URISyntaxException;
import java.util.concurrent.ThreadPoolExecutor;

public class UserTimelineCursorAdapter extends SimpleCursorAdapter implements ZoomListView.OnItemFocused {


    private static final String TAG = UserTimelineCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private UrlImageLoader  _imageLoader;
	private int _layout;
    private FriendTimeLineToParcelable _friendTimeLineToParcelable;
    private SparseArray<Boolean> _enabledItems;
    private SingleTweetAdapter.OnTweetOperation _onTweetOperationLis;
    private int _lastPosition = 0;
    private final Animation _scaleAnimation;
    public UserTimelineCursorAdapter(Context context, int layout, Cursor c,
                                     String[] from, int[] to, int flags,
                                     FriendTimeLineToParcelable friendTimeLineToParcelable_, UrlImageLoader imageLoader_,
                                     SingleTweetAdapter.OnTweetOperation onTweetOperationLis_ ) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _layout = layout;
        _friendTimeLineToParcelable = friendTimeLineToParcelable_;
        _imageLoader = imageLoader_;
        _enabledItems = new SparseArray<Boolean>();
        _onTweetOperationLis = onTweetOperationLis_;
        _scaleAnimation = getZoomAnimation(0.8f,1f,0.8f,1f);
	}


    @Override
	public void bindView(View view_, Context context, Cursor cursor_) {
		
		ParcelableUser user = getParcelable(cursor_);
        ParcelableTweet tweet = user.getUserTimeLine().get(0);
        ImageView profilePic =(ImageView)view_.findViewById(R.id.profile_image);
        ImageView mediaPhoto =(ImageView)view_.findViewById(R.id.media_photo);

        String photoUrl = tweet.getPhotoUrl();

        if(!TextUtils.isEmpty(photoUrl)){
            mediaPhoto.setVisibility(View.VISIBLE);
            Log.v(TAG, "Url for image " + photoUrl + "for user " + user.toString());
        }else{
            mediaPhoto.setVisibility(View.GONE);
        }

        loadImage(profilePic, user.getProfileImageUrl(), 1);
        loadImage(mediaPhoto, photoUrl, 1);

        TextView friendName=(TextView)view_.findViewById(R.id.timeline_friend_name);
		
		TextView dateTime=(TextView)view_.findViewById(R.id.timeline_date_time);
		dateTime.setText(tweet.getTweetDate());
		friendName.setText(user.getScreenName());

        TextView tweetTextView =(TextView)view_.findViewById(R.id.timeline_entry);
        tweetTextView.setText(tweet.getTweetText());

        ImageButton favBut = (ImageButton) view_.findViewById(R.id.favourite_but);
        favBut.setOnClickListener(getFavOnClick(user, _onTweetOperationLis));
        if(tweet.isFavourite()){
            favBut.setBackgroundColor(Color.rgb(71, 71, 71));
        }

        ImageButton reTweetBut = (ImageButton) view_.findViewById(R.id.retweet_but);

        if(tweet.isRetweeted()){
            reTweetBut.setEnabled(false);
            reTweetBut.setBackgroundColor(Color.rgb(71, 71, 71));
        }else{
            reTweetBut.setOnClickListener(getReTweetOnClick(user, _onTweetOperationLis));
        }

        ImageButton replyTweet = (ImageButton) view_.findViewById(R.id.reply_but);
        replyTweet.setOnClickListener(getReplyOnClick(user, _onTweetOperationLis));

        ImageButton quoteTweetBut = (ImageButton) view_.findViewById(R.id.copy_tweet_but);
        quoteTweetBut.setOnClickListener(getQuoteOnClick(user, _onTweetOperationLis));

	}

    @Override
    public boolean isEnabled(int position) {
     //   Boolean isenabled = _enabledItems.get(position, true);
     //   return isenabled;
        return true;
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        long id =  cursor.getLong(cursor.getColumnIndexOrThrow(TimelineTable.TimelineColumn.TWEET_ID.a()));
        Log.v(TAG, "item id in cursor adapter " + id);
        return id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadImage(ImageView view_,String url_, int size){
        if (!TextUtils.isEmpty(url_)) {
            try {
                _imageLoader.displayImage(url_, view_, size);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
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

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View v = super.getView(position, convertView, parent);
//
//        boolean isMeasuringGridViewItem = parent.getHeight() == 0;
//
//        if (position >= _lastPosition && !isMeasuringGridViewItem) {
//           v.startAnimation( getZoomAnimation(0.8f,1f,0.8f,1f));
//        }
//
//        _lastPosition = position;
//
//
//        return v;
//    }

    private Animation getZoomAnimation(float fromX_, float toX_, float fromY_, float toY_){
        Animation  scaleAnimation = new ScaleAnimation(
                fromX_, toX_,
                fromY_, toY_,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(500);
        return scaleAnimation;
    }

    protected ParcelableUser getParcelable(Cursor cursorTimeline_) {
     return _friendTimeLineToParcelable.getParcelable(cursorTimeline_);
	}


	
	@Override
	public View newView(Context context, Cursor  cursor, ViewGroup parent) {
		final View view=_inflater.inflate(_layout,parent,false);
        return view;
	}


    @Override
    public void onItemScaleOut(int position, View view, boolean status_) {
        view.findViewById(R.id.overlay_view);
        _enabledItems.put(position, status_);

    }

    @Override
    public void onItemRestore(int position, View view, boolean status_) {

    }

    @Override
    public View onItemFocused(View focusedView_, int listViewPosition_, long uniqueId_) {
        return focusedView_.findViewById(R.id.tweet_operation_buttons);
    }

    private View.OnClickListener getReplyOnClick(final ParcelableUser user_, final SingleTweetAdapter.OnTweetOperation onTweetOperationLis_ ){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTweetOperationLis_.onReplyTweet(v,user_);
            }
        };
    }

    private View.OnClickListener getQuoteOnClick(final  ParcelableUser user_, final SingleTweetAdapter.OnTweetOperation onTweetOperationLis_ ){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTweetOperationLis_.onQuoteTweet(v,user_);
            }
        };
    }

    private View.OnClickListener getReTweetOnClick(final  ParcelableUser user_, final SingleTweetAdapter.OnTweetOperation onTweetOperationLis_ ){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTweetOperationLis_.onReTweet(v,user_);
            }
        };
    }

    private View.OnClickListener getFavOnClick(final  ParcelableUser user_, final SingleTweetAdapter.OnTweetOperation onTweetOperationLis_ ){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // v.setVisibility(View.GONE);
                onTweetOperationLis_.onTweetFav(v,user_);
//               new FavouriteTweet(_smoothProgressBarWrapper, _timelineDao)
//                       .executeOnExecutor(TwitterUtil.getInstance().getGlobalExecutor(),
//                               new ParcelableTweet[]{tweetToFav_});
            }
        };
    }
}
