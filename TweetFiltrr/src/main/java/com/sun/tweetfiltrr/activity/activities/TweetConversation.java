package com.sun.tweetfiltrr.activity.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
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
import com.sun.tweetfiltrr.asyncretriever.retrievers.AsyncAccessTokenRetriever;
import com.sun.tweetfiltrr.asyncretriever.retrievers.ConversationRetriever;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.imageprocessor.BlurredImageGenerator;
import com.sun.tweetfiltrr.imageprocessor.IImageProcessor;
import com.sun.tweetfiltrr.merge.MergeAdapter;
import com.sun.tweetfiltrr.multipleselector.impl.UserConversationDisplayer;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class TweetConversation extends SherlockFragmentActivity implements ImageTaskListener {
	private static final String TAG = TweetConversation.class.getName();
    private static final int TOP_HEIGHT = 500;
    private ParcelableUser _currentUser;
	private ThreadPoolExecutor _threadExecutor;
    private ImageView _blurredBackground;
    private ImageView _backgroundImage;
    private UrlImageLoader  _sicImageLoader;
    private ConversationAdapter _convoAdapter;
    private IDBDao<ParcelableTweet> _timelineDao;
    private Handler _currentHandler;
    private List<ParcelableUser> _convoUsers;
    private UserConversationDisplayer _conversationDisplayer;
    private IImageProcessor _blurredImageProcessor;
    private static String BLURRED_IMAGE_PREFIX = "blurred_";
    private MergeAdapter _mergeAdapter;
    private ArrayAdapter<ParcelableUser> _singleTweetAdapter;
    private float alpha;
    private View _headerView;
    private ListView _listView;
    private int _screenWidth;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_conversation_layout);

		try {
			initControl();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}

        _screenWidth = getScreenWidth(this);

        _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();

        _sicImageLoader = UrlImageLoader.getInstance();

        _blurredBackground = (ImageView) findViewById(R.id.blurred_user_background_image);
        _backgroundImage = (ImageView) findViewById(R.id.user_background_image);

        _headerView = new View(this);
        _headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, TOP_HEIGHT));

        _listView  = (ListView) findViewById(android.R.id.list);

        DaoFlyWeightFactory flyWeight = DaoFlyWeightFactory
                .getInstance(this.getContentResolver());

        _currentHandler = new Handler();
        _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();

        //init the Dao object using the flyweight so that we can share the Dao's between different fragments
        _timelineDao = (IDBDao<ParcelableTweet>) flyWeight
                .getDao(DaoFlyWeightFactory.DaoFactory.TIMELINE_DAO, _currentUser);
        _convoUsers = new ArrayList<ParcelableUser>();

        List<ParcelableUser> friends = new ArrayList<ParcelableUser>();
        friends.add(_currentUser);

        SmoothProgressBar progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        SmoothProgressBarWrapper smoothProgressBarWrapper = new SmoothProgressBarWrapper(progressBar);

        _singleTweetAdapter = new SingleTweetAdapter(this, R.layout.single_tweet_list_row,friends, _sicImageLoader , smoothProgressBarWrapper, _timelineDao);


        _blurredImageProcessor = new BlurredImageGenerator(this);
        _convoAdapter = new ConversationAdapter(this, R.layout.timeline_list_view, _convoUsers);
        _conversationDisplayer = new UserConversationDisplayer("Select Friends", this,
                R.layout.conversation_listview
                ,  _convoAdapter);
        _mergeAdapter = new MergeAdapter();

        _mergeAdapter.addAdapter(_singleTweetAdapter);
        _mergeAdapter.addAdapter(_convoAdapter);

        _listView.addHeaderView(_headerView);

         attemptLoadImage(_backgroundImage, _sicImageLoader, _currentUser.getProfileBackgroundImageUrl(), 2);

        _listView.setAdapter(_mergeAdapter);

        _listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * Listen to the list scroll. This is where magic happens ;)
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Calculate the ratio between the scroll amount and the list
                // header weight to determinate the top picture alpha
                alpha = (float) -_headerView.getTop() / (float) TOP_HEIGHT;
                // Apply a ceil
                if (alpha > 1) {
                    alpha = 1;
                }
                alpha*=4;
                Log.v(TAG, "alpha value is: " + alpha);
                // Apply on the ImageView if needed
                _blurredBackground.setAlpha(alpha);

                // Parallax effect : we apply half the scroll amount to our
                // three views
               _blurredBackground.setTop(_headerView.getTop() / 2);
               _backgroundImage.setTop(_headerView.getTop() / 2);

            }
        });

        loadConversation();
        overridePendingTransition(R.anim.display_anim_bot_to_top, 0);
    }

    private View.OnFocusChangeListener getTweetEditClick() {
        return new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Bitmap bitmap = ((BitmapDrawable)_backgroundImage.getDrawable()).getBitmap();

                _backgroundImage.setImageBitmap(_blurredImageProcessor.processImage(bitmap));
            }
        };
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


    private void attemptLoadImage(ImageView imageView_, UrlImageLoader loader_
    		, String url_, int sampleSize_) {
		try {
			if (!TextUtils.isEmpty(url_)) {
				loader_.displayImage(url_, imageView_, sampleSize_, this);
			} else {
				imageView_.setImageDrawable(new ColorDrawable(Color.BLACK));
			}
		}catch (NullPointerException nu){
			Log.e(TAG, "Null pointer detected while loading image, using default");
			setDefaultDrawable(imageView_);
		} catch (URISyntaxException e) {
			Log.e(TAG, "Error in background image display");
			setDefaultDrawable(imageView_);
		}
    }
    
    private void setDefaultDrawable(ImageView imageView_){
		imageView_.setImageDrawable(new ColorDrawable(Color.BLACK));
    }
	
	protected void onBroadCastReceive(Intent intent_) {
		
		   _currentUser = intent_.getExtras().getParcelable(TwitterConstants.FRIENDS_BUNDLE);
		    Log.d(TAG, "Got message: " + _currentUser.toString());
//			setTweetDetails();
	}
	

	private void initControl() throws InterruptedException, ExecutionException {
		Uri uri = getIntent().getData();
		Bundle bundle = this.getIntent().getExtras();

        if(bundle != null){
            _currentUser = bundle.getParcelable(TwitterConstants.FRIENDS_BUNDLE);
            if(_currentUser != null){
            Log.v(TAG, "I have parcelalbe!!" + _currentUser.getScreenName());
            Log.v(TAG, "User has timeline size!! " + _currentUser.getUserTimeLine().size());

                return;
            }
        }

		if (uri != null
				&& uri.toString().startsWith(
						TwitterConstants.TWITTER_CALLBACK_URL)) {
			String verifier = uri
					.getQueryParameter(TwitterConstants.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
			Log.v(TAG, "Verifier is " + verifier);
			_currentUser = new AsyncAccessTokenRetriever(getApplicationContext()).execute(verifier).get();
		}else{
			_currentUser = new AsyncAccessTokenRetriever(getApplicationContext()).execute("").get();
		}
		
		Log.v(TAG, "Main user for app : " + _currentUser.toString());

		
	}

    @Override
    public void preImageLoad(ImageSettings imageSettings) {

    }

    @Override
    public void onImageLoadComplete(Bitmap bitmap, ImageSettings imageSettings) {
        String imageFilePath = TwitterConstants.SIC_SAVE_DIRECTORY+"/"
                +BLURRED_IMAGE_PREFIX+imageSettings.getFinalFileName();
        Bitmap blurredBitmap = tryLoadImage(imageFilePath, bitmap);
        Log.v(TAG, "Image file path is " + imageFilePath);

//        updateView(_screenWidth, _blurredBackground,blurredBitmap);

       _blurredBackground.setImageBitmap(blurredBitmap);

//        _backgroundImage.setAlpha(0f);
//        _blurredBackground.setImageDrawable(new ColorDrawable(Color.BLACK));

    }


    @Override
    public void onImageLoadFail(FailedTaskReason failedTaskReason, ImageSettings imageSettings) {

    }

//    private void updateView(final int screenWidth) {
//        Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir() + BLURRED_IMG_PATH);
//        bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, screenWidth, (int) (bmpBlurred.getHeight()
//                * ((float) screenWidth) / (float) bmpBlurred.getWidth()), false);
//
//        mBlurredImage.setImageBitmap(bmpBlurred);
//
//    }

    private Bitmap tryLoadImage(String filePath_, Bitmap bitmap) {
        File blurredImage = new File(filePath_);
        Bitmap bmp = null;

        if(!blurredImage.exists()){
            FileOutputStream out = null;
            if (bmp == null) {
                try {
                   Log.v(TAG, "Bitmap null so attempting to generate a new one");
                    bmp = _blurredImageProcessor.processImage(bitmap);
                    out = new FileOutputStream(blurredImage);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    out.flush();

                   return bmp;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(out!=null){
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

         bmp = BitmapFactory.decodeFile(filePath_);

//           Bitmap bmp = null;
//         bmp = _blurredImageProcessor.processImage(bitmap);

        return bmp;
    }

    private void updateView(int screenWidth, ImageView imageview, Bitmap blurred) {
        Bitmap bmpBlurred = Bitmap.createScaledBitmap(blurred, screenWidth, (int) (blurred.getHeight()
                * ((float) screenWidth) / (float) blurred.getWidth()), false);
        Log.v(TAG, "scaled new height is: " + ((int) (blurred.getHeight()
                * ((float) screenWidth) / (float) blurred.getWidth())));
        imageview.setImageBitmap(bmpBlurred);

    }

    public static int getScreenWidth(Activity context) {

        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        return display.getWidth();
    }


}
