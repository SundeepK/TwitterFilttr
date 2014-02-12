package com.sun.tweetfiltrr.activity.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.PostTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.TweetOperationTask;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITweetOperation;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.Collection;

import twitter4j.TwitterException;

public class PostTweetActivity extends SherlockFragmentActivity implements ITwitterAPICallStatus {

    private static final String TAG =PostTweetActivity.class.getName() ;
    private IDBDao<ParcelableTweet> _timelineDao;
    private final int _initailCount  = 140;
    private ParcelableUser _user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_tweet_layout);

        UrlImageLoader urlImageLoader = TwitterUtil.getInstance().getGlobalImageLoader(this);

        TextView tweetText = (TextView)findViewById(R.id.timeline_entry);
        TextView friendName=(TextView)findViewById(R.id.timeline_friend_name);
        TextView dateTime=(TextView)findViewById(R.id.timeline_date_time);
        TextView charCountView = (TextView)findViewById(R.id.char_count_txtbox);
        ImageButton postTweetBut =(ImageButton)findViewById(R.id.send_tweet_but);
        EditText tweetEditTxt = (EditText)findViewById(R.id.reply_tweet_edittxt);
        ImageView profilePic =(ImageView)findViewById(R.id.profile_image);

        _timelineDao = new TimelineDao(getContentResolver(), new TimelineToParcelable());

         _user = getIntent().getExtras().getParcelable(TwitterConstants.PARCELABLE_FRIEND_WITH_TIMELINE);

        if(_user != null){
            boolean shouldQuote =  getIntent().getExtras().getBoolean(TwitterConstants.IS_QUOTE_REPLY);
            Collection<ParcelableTweet> tweets = _user.getUserTimeLine();
            friendName.setText("@" + _user.getScreenName());

            if(!tweets.isEmpty()){
                ParcelableTweet tweet =  tweets.iterator().next();//we only expect one tweet to reply too
                dateTime.setText(tweet.getTweetDate());
                tweetText.setText(tweet.getTweetText());
                String photoUrl = tweet.getPhotoUrl();

                if(shouldQuote){
                    tweetEditTxt.setText("RT @" + _user.getScreenName()+": " + tweet.getTweetText());
                }else{
                    tweetEditTxt.setText("@" + _user.getScreenName());
                }
            }

//        ImageView mediaPhoto =(ImageView)findViewById(R.id.media_photo);
//        if(!TextUtils.isEmpty(photoUrl)){
//            mediaPhoto.setVisibility(View.VISIBLE);
//            ImageLoaderUtils.attemptLoadImage(mediaPhoto, urlImageLoader, photoUrl,1, null);
//        }else{
//            mediaPhoto.setVisibility(View.GONE);
//        }

            ImageLoaderUtils.attemptLoadImage(profilePic, urlImageLoader, _user.getProfileImageUrl(),1, null);


        }

        int initailCount = _initailCount -tweetEditTxt.getEditableText().length();
        charCountView.setText(Integer.toString(initailCount));
        tweetEditTxt.addTextChangedListener(getTweetTextLis(charCountView));

        postTweetBut.setOnClickListener(getOnClickPostTweet(tweetEditTxt, _timelineDao, this));


    }

    private TextWatcher getTweetTextLis(final TextView tweetCoundTxtView_) {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                int length =  _initailCount - s.length();
                tweetCoundTxtView_.setText(Integer.toString(length));

                if(length < 0){
                    tweetCoundTxtView_.setTextColor(Color.rgb(222,89,71));
                }
            }
        };
    }

    private View.OnClickListener getOnClickPostTweet(final EditText editText_,
            final IDBDao<ParcelableTweet> timelineDao_, final ITwitterAPICallStatus lis_ ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // make an empty tweet with only text since we only care about the text
                // the tweet updated in the DB will be the one returned from twitter after the post is complete so this is safe
                ParcelableTweet tweet = new ParcelableTweet(editText_.getText().toString(), "",0l,0l,"",0l,0l,"",false,false, false);
                _user.addTimeLineEntry(tweet);
                ITwitterAPICall<ParcelableTweet> postTweet = new PostTweet();
                TweetOperationTask task = new TweetOperationTask(timelineDao_,_user, lis_ );
                task.execute(postTweet);
                PostTweetActivity.this.finish();
            }
        };
    }


    @Override
	public void finish() {
		super.finish();
	};

    public void onTaskSuccessfulComplete(ParcelableTweet tweet_) {
        Toast.makeText(this, "Tweet posted", 2).show();
    }

    public void onTaskFail(ParcelableTweet failedTweet_, TwitterException exception_, ITweetOperation operation_) {
        Toast.makeText(this, "Tweet failed", 2).show();
    }


    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_) {

    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall apiCallType_) {

    }
}
