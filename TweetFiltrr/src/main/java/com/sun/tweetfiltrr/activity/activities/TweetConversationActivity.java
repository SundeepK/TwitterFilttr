package com.sun.tweetfiltrr.activity.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.ConversationAdapter;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.application.TweetConvoModule;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.imageprocessor.IImageProcessor;
import com.sun.tweetfiltrr.merge.MergeAdapter;
import com.sun.tweetfiltrr.multipleselector.impl.UserConversationDisplayer;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.TweetOperationController;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.ConversationRetriever;
import com.sun.tweetfiltrr.utils.FileImageProcessorUtils;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.ObjectGraph;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class TweetConversationActivity extends SherlockFragmentActivity implements ImageTaskListener {
	private static final String TAG = TweetConversationActivity.class.getName();
    private static final int TOP_HEIGHT = 500;
    private static final String BLURRED_IMAGE_PREFIX = "blurred_";
    private ParcelableUser _currentUser;
    private ImageView _blurredBackground;
    private Handler _currentHandler = new Handler();
    private UserConversationDisplayer _conversationDisplayer;
    private float alpha;

    @Inject TimelineDao _timelineDao;
    @Inject UrlImageLoader _sicImageLoader;
    @Inject ExecutorService _threadExecutor;
    @Inject @Named("blurred") IImageProcessor _blurredImageProcessor;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_conversation_layout);

        final ObjectGraph appObjectGraph = ((TweetFiltrrApplication) getApplication()).getObjectGraph();
        final ObjectGraph objectGraph = appObjectGraph.plus(new TweetConvoModule());
        objectGraph.inject(this);

        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);

        final SmoothProgressBar progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);
        final SmoothProgressBarWrapper smoothProgressBarWrapper = new SmoothProgressBarWrapper(progressBar);
        final SingleTweetAdapter.OnTweetOperation onTweetOperationListener
                = new TweetOperationController(smoothProgressBarWrapper, _timelineDao);

        _blurredBackground = (ImageView) findViewById(R.id.blurred_user_background_image);
        final ImageView backgroundProfileImage = (ImageView) findViewById(R.id.user_background_image);
        final View headerView = new View(this);
        headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, TOP_HEIGHT));
        final ListView listview = (ListView) findViewById(android.R.id.list);

        List<ParcelableUser> _convoUsers = new ArrayList<ParcelableUser>();
        List<ParcelableUser> friends = new ArrayList<ParcelableUser>();
        friends.add(_currentUser);

        final ArrayAdapter<ParcelableUser> _singleTweetAdapter = new SingleTweetAdapter(this, R.layout.single_tweet_list_row, friends, _sicImageLoader, onTweetOperationListener);
        ConversationAdapter _convoAdapter = new ConversationAdapter(this, R.layout.timeline_list_view, _convoUsers);
        _conversationDisplayer = new UserConversationDisplayer("Select Friends", this,
                R.layout.conversation_listview
                , _convoAdapter);
        final MergeAdapter mergeAdapter = new MergeAdapter();

        mergeAdapter.addAdapter(_singleTweetAdapter);
        mergeAdapter.addAdapter(_convoAdapter);

        listview.addHeaderView(headerView);
        ImageLoaderUtils.attemptLoadImage(backgroundProfileImage, _sicImageLoader,
                _currentUser.getProfileBackgroundImageUrl(), 2, this);
        listview.setAdapter(mergeAdapter);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                alpha = (float) -headerView.getTop() / (float) TOP_HEIGHT;
                if (alpha > 1) {
                    alpha = 1;
                }
                alpha *= 4;
                Log.v(TAG, "alpha value is: " + alpha);
                _blurredBackground.setAlpha(alpha);
                _blurredBackground.setTop(headerView.getTop() / 2);
                backgroundProfileImage.setTop(headerView.getTop() / 2);

            }
        });

        loadConversation();
        overridePendingTransition(R.anim.display_anim_bot_to_top, 0);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.display_anim_top_bot_top, R.anim.display_anim_top_bot_top );
    }


    private void loadConversation(){
        ParcelableTweet tweet = _currentUser.getUserTimeLine().iterator().next();
        if(tweet.getInReplyToUserId() > 0){
        ConversationRetriever convoRetriever = new ConversationRetriever(_currentUser, _timelineDao, _conversationDisplayer, _currentHandler);
        _threadExecutor.execute(convoRetriever);
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
       _blurredBackground.setImageBitmap(blurredBitmap);
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
