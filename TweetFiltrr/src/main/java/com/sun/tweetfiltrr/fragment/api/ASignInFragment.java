package com.sun.tweetfiltrr.fragment.api;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.ITwitterAuthCallback;
import com.sun.tweetfiltrr.activity.activities.TwitterFilttrLoggedInUserHome;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.customviews.views.CircleCroppedDrawable;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import javax.inject.Inject;

import twitter4j.auth.AccessToken;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public abstract class ASignInFragment extends SherlockFragment implements ImageTaskListener, ITwitterAuthCallback {

    private static final String TAG = ASignInFragment.class.getName();
    private ImageView _profile;
    @Inject
    UrlImageLoader _sicImageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TweetFiltrrApplication) getActivity().getApplication()).getObjectGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_screen_auto_sign_in, container, false);
        initView(rootView);
        authenticateUser(this);
        return rootView;
    }

    protected void initView(View rootView_){
        _profile = (ImageView) rootView_.findViewById(R.id.app_loading_image_view);
        SharedPreferences sharedPreferences =  PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        TextView userName = (TextView) rootView_.findViewById(R.id.friend_name);
        String profileUrl =  sharedPreferences.getString(
                TwitterConstants.LOGIN_PROFILE_BG, null);
        String name = sharedPreferences.getString(
                TwitterConstants.AUTH_USER_NAME_BG, null);
        userName.setText(name);
        ImageLoaderUtils.attemptLoadImage(_profile, _sicImageLoader, profileUrl, 1, this);
    }

    protected abstract void authenticateUser(ITwitterAuthCallback callback_);

    private void finishSignIn(UserBundle bundle){
        TwitterUtil.getInstance().setTwitterFactories(bundle.getAccessToken());
        TwitterUtil.getInstance().setCurrentUser(bundle.getUser());
        Intent intent = new Intent(getActivity(), TwitterFilttrLoggedInUserHome.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void preImageLoad(ImageSettings imageSettings) {

    }

    protected void startLoadAnimation(View view){
        Animation anim = getZoomAnimation(1f,1.1f, 1f, 1.1f);
        view.startAnimation(anim);
    }

    @Override
    public void onImageLoadComplete(Bitmap bitmap, ImageSettings imageSettings) {
        setCircleDrawable(bitmap, _profile);
        startLoadAnimation(_profile);
    }

    protected  void setCircleDrawable(Bitmap bitmap_, ImageView imageView_){
        CircleCroppedDrawable d = new CircleCroppedDrawable(bitmap_);
        imageView_.setImageBitmap(null);
        imageView_.setImageDrawable(d);
        imageView_.setBackground(d);
    }

    private Animation getZoomAnimation(float fromX_, float toX_, float fromY_, float toY_){
        Animation  scaleAnimation = new ScaleAnimation(
                fromX_, toX_,
                fromY_, toY_,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(600);
        scaleAnimation.setInterpolator(new CycleInterpolator(1));
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.INFINITE);
        return scaleAnimation;
    }

    @Override
    public void onImageLoadFail(FailedTaskReason failedTaskReason, ImageSettings imageSettings) {

    }

    @Override
    public void onSuccessTwitterOAuth(UserBundle userBundle) {
        Log.v(TAG, "auth seccess " );
        persistUserDetails(userBundle);
        finishSignIn(userBundle);
    }

    @Override
    public void onFailTwitterOAuth(Exception e) {

    }

    private void persistUserDetails(UserBundle bundle){
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final AccessToken token = bundle.getAccessToken();
        final ParcelableUser user = bundle.getUser();
        Log.v(TAG, "going to persist user " + user);
        editor.putString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN,
                token.getToken());
        Log.v(TAG, "secrect token: " + token.getToken());
        editor.putString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
                token.getTokenSecret());
        Log.v(TAG, "secrect secret: " + token.getTokenSecret());
        editor.putBoolean(
                TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
        editor.putString(TwitterConstants.AUTH_USER_SCREEN_BG,	user.getProfileBackgroundImageUrl());
        editor.putString(TwitterConstants.LOGIN_PROFILE_BG,user.getProfileImageUrl());
        editor.putString(TwitterConstants.AUTH_USER_DESC_BG,user.getDescription());
        editor.putString(TwitterConstants.AUTH_USER_NAME_BG, user.getScreenName());
        editor.putLong(TwitterConstants.AUTH_USER_ID, user.getUserId());
        editor.commit();
    }


}
