package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
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
import com.sun.tweetfiltrr.customviews.views.ZoomListView;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.net.URISyntaxException;

public class UserTimelineCursorAdapter extends ResourceCursorAdapter implements ZoomListView.OnItemFocused {


    private static final String TAG = UserTimelineCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private UrlImageLoader  _imageLoader;
	private int _layout;
    private FriendTimeLineToParcelable _friendTimeLineToParcelable;
    private SparseBooleanArray _enabledItems;
    private SingleTweetAdapter.OnTweetOperation _onTweetOperationLis;
    public UserTimelineCursorAdapter(Context context, int layout, Cursor c,
                                     FriendTimeLineToParcelable friendTimeLineToParcelable_, UrlImageLoader imageLoader_,
                                     SingleTweetAdapter.OnTweetOperation onTweetOperationLis_ ) {
		super(context, layout, c, 0);
        _inflater = LayoutInflater.from(context);
        _layout = layout;
        _friendTimeLineToParcelable = friendTimeLineToParcelable_;
        _imageLoader = imageLoader_;
        _enabledItems = new SparseBooleanArray();
        _onTweetOperationLis = onTweetOperationLis_;
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
        ImageButton quoteTweetBut = (ImageButton) view_.findViewById(R.id.copy_tweet_but);
        ImageButton replyTweet = (ImageButton) view_.findViewById(R.id.reply_but);
        ImageButton favBut = (ImageButton) view_.findViewById(R.id.favourite_but);
        ImageButton reTweetBut = (ImageButton) view_.findViewById(R.id.retweet_but);

        if(tweet.isFavourite()){
            favBut.setEnabled(true);
            favBut.setBackgroundColor(Color.rgb(71, 71, 71));
        }else{
            favBut.setEnabled(true);
            favBut.setBackgroundColor(Color.rgb(0, 0, 0));
        }

        if(tweet.isRetweeted()){
            reTweetBut.setBackgroundColor(Color.rgb(71, 71, 71));
            reTweetBut.setEnabled(false);
        }else{
            reTweetBut.setEnabled(true);
            reTweetBut.setBackgroundColor(Color.rgb(0, 0, 0));
            reTweetBut.setOnClickListener(getReTweetOnClick(user, _onTweetOperationLis));
        }

        favBut.setOnClickListener(getFavOnClick(user, _onTweetOperationLis));
        replyTweet.setOnClickListener(getReplyOnClick(user, _onTweetOperationLis));
        quoteTweetBut.setOnClickListener(getQuoteOnClick(user, _onTweetOperationLis));
        //Log.v(TAG, "bindview called for tweet :" + tweet + " with fav bool as: "+ tweet.isFavourite());
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
     //   Log.v(TAG, "item id in cursor adapter " + id);
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
       // view.findViewById(R.id.overlay_view);
        _enabledItems.put(position, status_);

    }

    @Override
    public void onItemRestore(int position, View view, boolean status_) {

    }

    @Override
    public View onItemFocused(View focusedView_, int listViewPosition_, long uniqueId_) {
        Cursor cursor = getCursor();
        if(cursor != null){
            if(cursor.getCount() > 0){
                cursor.moveToPosition(listViewPosition_);
                ParcelableTweet tweet = getParcelable(cursor).getUserTimeLine().iterator().next();
                Log.v(TAG,  "item pos " +  listViewPosition_ +" onlonglick tweet is: " + tweet);
                return focusedView_.findViewById(R.id.tweet_operation_buttons);
            }
        }
        return null;
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
                onTweetOperationLis_.onTweetFav(v,user_);
            }
        };
    }
}
