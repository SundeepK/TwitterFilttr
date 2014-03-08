package com.sun.tweetfiltrr.activity.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.application.TweetConvoModule;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.fragment.fragments.ConversationFragment;
import com.sun.tweetfiltrr.imageprocessor.IImageProcessor;
import com.sun.tweetfiltrr.merge.MergeAdapter;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.TweetOperationController;
import com.sun.tweetfiltrr.utils.FileImageProcessorUtils;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.ObjectGraph;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class TweetConversationActivity extends SherlockFragmentActivity implements ImageTaskListener {
	private static final String TAG = TweetConversationActivity.class.getName();
    private static final String BLURRED_IMAGE_PREFIX = "blurred_";
    private ParcelableUser _currentUser;
    private SlidingMenu _convoFragment;
    @Inject TimelineDao _timelineDao;
    @Inject UrlImageLoader _sicImageLoader;
    @Inject @Named("blurred") IImageProcessor _blurredImageProcessor;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_conversation_layout);

        final ObjectGraph appObjectGraph = ((TweetFiltrrApplication) getApplication()).getObjectGraph();
        final ObjectGraph objectGraph = appObjectGraph.plus(new TweetConvoModule());
        objectGraph.inject(this);

        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);
        //load sliding fragment for convo
        _convoFragment = new SlidingMenu(this);
        final Fragment convoFragment = new ConversationFragment();
        final Bundle userBundle = new Bundle();

        userBundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
        convoFragment.setArguments(userBundle);
        Log.v(TAG, "current user is " + _currentUser.getScreenName());
        if(Build.VERSION.SDK_INT  < 16 ){
            _convoFragment.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }else{
            _convoFragment.setBackground(new ColorDrawable(Color.BLACK));
        }
        _convoFragment.setFadeDegree(0.35f);
        _convoFragment.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        _convoFragment.setMenu(R.layout.sliding_menu_fragment);
        _convoFragment.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        _convoFragment.setMode(SlidingMenu.RIGHT);
        _convoFragment.setBehindOffset(100);
        _convoFragment.setFadeEnabled(true);
        //set tweet convo fragment
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.menu_frame, convoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        final SmoothProgressBar progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);
        final SmoothProgressBarWrapper smoothProgressBarWrapper = new SmoothProgressBarWrapper(progressBar);
        final SingleTweetAdapter.OnTweetOperation onTweetOperationListener
                = new TweetOperationController(smoothProgressBarWrapper, _timelineDao);

        final ListView listview = (ListView) findViewById(android.R.id.list);
        List<ParcelableUser> friends = new ArrayList<ParcelableUser>();
        friends.add(_currentUser);

        final ArrayAdapter<ParcelableUser> _singleTweetAdapter = new SingleTweetAdapter(this, R.layout.single_tweet_list_row,
                friends, _sicImageLoader, onTweetOperationListener);
        final MergeAdapter mergeAdapter = new MergeAdapter();
        //load media image
        final ImageView mediaPhoto =  (ImageView) findViewById(R.id.media_photo_imageview);
        final Collection<ParcelableTweet> tweets = _currentUser.getUserTimeLine();
        if(!tweets.isEmpty()){
            final ParcelableTweet tweet = tweets.iterator().next();
            final String photoUrl = tweet.getPhotoUrl();
            if(!TextUtils.isEmpty(photoUrl)){
                mediaPhoto.setVisibility(View.VISIBLE);
                ImageLoaderUtils.attemptLoadImage(mediaPhoto,_sicImageLoader,photoUrl,1,null);
            }
        }
        mergeAdapter.addAdapter(_singleTweetAdapter);
        listview.setAdapter(mergeAdapter);
        Button showConvo = (Button) findViewById(R.id.show_convo_But);
        showConvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _convoFragment.showMenu();
            }
        });
        overridePendingTransition(R.anim.display_anim_bot_to_top, 0);
    }


    @Override
    public void onBackPressed() {
        if(_convoFragment.isActivated()){
            _convoFragment.toggle();
        }else{
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.display_anim_top_bot_top, R.anim.display_anim_top_bot_top );
        }
    }

    @Override
    public void preImageLoad(ImageSettings imageSettings) {
    }

    @Override
    public void onImageLoadComplete(Bitmap bitmap, ImageSettings imageSettings) {
        final String imageFilePath = TwitterConstants.SIC_SAVE_DIRECTORY+"/"
                + BLURRED_IMAGE_PREFIX +imageSettings.getFinalFileName();
        final Bitmap blurredBitmap = FileImageProcessorUtils.loadImage(imageFilePath,bitmap,_blurredImageProcessor,
                Bitmap.CompressFormat.JPEG, 80, this);
        Log.v(TAG, "Image file path is " + imageFilePath);
    }

    @Override
    public void onImageLoadFail(FailedTaskReason failedTaskReason, ImageSettings imageSettings) {
    }

    private void updateView(int screenWidth, ImageView imageview, Bitmap blurred) {
        Bitmap bmpBlurred = Bitmap.createScaledBitmap(blurred, screenWidth, (int) (blurred.getHeight()
                * ((float) screenWidth) / (float) blurred.getWidth()), false);
        Log.v(TAG, "scaled new height is: " + ((int) (blurred.getHeight()
                * ((float) screenWidth) / (float) blurred.getWidth())));
        imageview.setImageBitmap(bmpBlurred);

    }
}
