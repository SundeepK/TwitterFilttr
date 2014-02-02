package com.sun.tweetfiltrr.activity.activities;

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
import com.sun.tweetfiltrr.twitter.tweetoperations.PostTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.TweetOperationTask;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITweetOperation;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import twitter4j.TwitterException;

public class ReplyTweetActivity extends SherlockFragmentActivity implements TweetOperationTask.OnTwitterTaskComplete {

    private IDBDao<ParcelableTweet> _timelineDao;
    private final int _initailCount  = 140;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_tweet_layout);

        UrlImageLoader urlImageLoader = TwitterUtil.getInstance().getGlobalImageLoader(this);
        _timelineDao = new TimelineDao(getContentResolver(), new TimelineToParcelable());

        ParcelableUser user = getIntent().getExtras().getParcelable(TwitterConstants.PARCELABLE_FRIEND_WITH_TIMELINE);
        ParcelableTweet tweet = user.getUserTimeLine().iterator().next(); //we only expect one tweet to reply too

        ImageView profilePic =(ImageView)findViewById(R.id.profile_image);
//        ImageView mediaPhoto =(ImageView)findViewById(R.id.media_photo);
        String photoUrl = tweet.getPhotoUrl();

//        if(!TextUtils.isEmpty(photoUrl)){
//            mediaPhoto.setVisibility(View.VISIBLE);
//            ImageLoaderUtils.attemptLoadImage(mediaPhoto, urlImageLoader, photoUrl,1, null);
//        }else{
//            mediaPhoto.setVisibility(View.GONE);
//        }

        ImageLoaderUtils.attemptLoadImage(profilePic, urlImageLoader, user.getProfileImageUrl(),1, null);

        TextView friendName=(TextView)findViewById(R.id.timeline_friend_name);

        TextView dateTime=(TextView)findViewById(R.id.timeline_date_time);
        dateTime.setText(tweet.getTweetDate());

        friendName.setText(user.getScreenName());

        TextView tweetText = (TextView)findViewById(R.id.timeline_entry);
        tweetText.setText(tweet.getTweetText());


        ImageButton postTweetBut =(ImageButton)findViewById(R.id.send_tweet_but);
        EditText tweetEditTxt = (EditText)findViewById(R.id.reply_tweet_edittxt);

        int initailCount = _initailCount - ("@" + user.getScreenName()).length();

        TextView charCountView = (TextView)findViewById(R.id.char_count_txtbox);
        charCountView.setText(Integer.toString(initailCount));
        tweetEditTxt.setText("@" + user.getScreenName());

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
            }
        };
    }

    private View.OnClickListener getOnClickPostTweet(final EditText editText_,
            final IDBDao<ParcelableTweet> timelineDao_, final TweetOperationTask.OnTwitterTaskComplete lis_ ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // make an empty tweet with only text since we only care about the text
                // the tweet updatedin the DB will be the one returned from twitter after the post is complete so this is safe
                ParcelableTweet tweet = new ParcelableTweet(editText_.getText().toString(), "",0l,0l,"",0l,0l,"",false,false, false);

                ITweetOperation postTweet = new PostTweet();
                TweetOperationTask task = new TweetOperationTask(timelineDao_,tweet, lis_ );
                task.execute(postTweet);
                ReplyTweetActivity.this.finish();
            }
        };
    }


    @Override
	public void finish() {
		super.finish();
	};

    @Override
    public void onSuccessfulComplete(ParcelableTweet tweet_) {
        Toast.makeText(this, "Tweet posted", 2).show();
    }

    @Override
    public void onTaskFail(ParcelableTweet failedTweet_, TwitterException exception_) {
        Toast.makeText(this, "Tweet failed", 2).show();
    }


}
