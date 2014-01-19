package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.asyncretriever.ConversationRetriever;
import com.sun.tweetfiltrr.cursorToParcelable.FriendTimeLineToParcelable;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.net.URISyntaxException;
import java.util.concurrent.ThreadPoolExecutor;

public class UserTimelineCursorAdapter extends SimpleCursorAdapter  {


    private static final String TAG = UserTimelineCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private UrlImageLoader  _imageLoader;
	private int _layout;
    private FriendTimeLineToParcelable _friendTimeLineToParcelable;


    public UserTimelineCursorAdapter(Context context, int layout, Cursor c,
                                     String[] from, int[] to, int flags,
                                     FriendTimeLineToParcelable friendTimeLineToParcelable_, UrlImageLoader imageLoader_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _layout = layout;
        _friendTimeLineToParcelable = friendTimeLineToParcelable_;
        _imageLoader = imageLoader_;

	}


	@Override
	public void bindView(View view_, Context context, Cursor cursor_) {
		
		ParcelableUser user = getParcelable(cursor_);
        ParcelableTimeLineEntry firstTweet = user.getUserTimeLine().get(0);
        ImageView profilePic =(ImageView)view_.findViewById(R.id.profile_image);
        ImageView mediaPhoto =(ImageView)view_.findViewById(R.id.media_photo);

        String photoUrl = firstTweet.getPhotoUrl();

        if(!TextUtils.isEmpty(photoUrl)){
            mediaPhoto.setVisibility(View.VISIBLE);
            Log.v(TAG, "Url for image " + photoUrl + "for user " + user.toString());
        }else{
            mediaPhoto.setVisibility(View.GONE);
        }

        loadImage(profilePic, user.getProfileImageUrl());
        loadImage(mediaPhoto, photoUrl);

        TextView friendName=(TextView)view_.findViewById(R.id.timeline_friend_name);
		
		TextView dateTime=(TextView)view_.findViewById(R.id.timeline_date_time);
		dateTime.setText(firstTweet.getTweetDate());

		friendName.setText(user.getUserName() + " @" + user.getScreenName());
        TextView tweetTextView =(TextView)view_.findViewById(R.id.timeline_entry);
        tweetTextView.setText(firstTweet.getTweetText());

       
	}

    private void loadImage(ImageView view_,String url_){
        if (!TextUtils.isEmpty(url_)) {
            try {
                _imageLoader.displayImage(url_, view_, 1);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
	
	
	private OnClickListener getConversationLis(final ParcelableUser currentFriend_,
			final IDBDao<ParcelableTimeLineEntry> timelineDao_, final ThreadPoolExecutor executor_, final ConversationRetriever.OnConvoLoadListener listener_, final Handler currentHandler_){
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
	
	protected ParcelableUser getParcelable(Cursor cursorTimeline_) {
     return _friendTimeLineToParcelable.getParcelable(cursorTimeline_);
	}


	
	@Override
	public View newView(Context context, Cursor  cursor, ViewGroup parent) {
		final View view=_inflater.inflate(_layout,parent,false); 
        return view;
	}




}
