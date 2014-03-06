package com.sun.tweetfiltrr.activity.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.customviews.views.CircleCroppedDrawable;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTab;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.fragment.fragments.SettingsScreen;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.inject.Inject;

public class UserProfileHomeActivity extends ATwitterActivity implements
        ListView.OnItemClickListener, SlidingMenu.OnOpenedListener, SlidingMenu.OnClosedListener {

    public enum FragmentState{
        TWEETS,
        FOLLOWING,
        FOLLOWERS
    }

	private static final String TAG = UserProfileHomeActivity.class.getName();
    private ParcelableUser _currentUser;
    private ArrayList<ParcelableUser> _userQueue; // not a queue but going to use it like one
    private FragmentState _currentFragmentState = FragmentState.TWEETS;
    private Bundle _userBundle;
    private HashMap<String, FragmentInfo> _fragInfoMap = new HashMap<String, FragmentInfo>();
    private LinkedList<FragmentInfo> _fragmentTags = new LinkedList<FragmentInfo>();
    @Inject UrlImageLoader _imageloader;

    @Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
        setContentView(R.layout.user_profile_home);
        _userQueue = UserRetrieverUtils.getUserQueue(this);

        if(_userQueue.isEmpty()){
            Log.v(TAG, "queue is empty");
            _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);
        }else{
            _currentUser = _userQueue.get(_userQueue.size() - 1);
            Log.v(TAG, "queue is not empty with user " + _currentUser);
        }

        Button showTweetsBut = (Button) findViewById(R.id.user_tweets_but);
        Button showFriendsBut = (Button) findViewById(R.id.user_friends_but);
        Button showFollowersBut = (Button) findViewById(R.id.user_followers_but);
        ImageView profileImage = (ImageView) findViewById(R.id.user_profile_imageview);
        ImageView profileBackgroundImage = (ImageView) findViewById(R.id.user_profile_background_imageview);
        TextView userName = (TextView) findViewById(R.id.user_name);
        TextView userDesc = (TextView) findViewById(R.id.user_desc_textview);

        ImageLoaderUtils.attemptLoadImage(profileImage,_imageloader, _currentUser.getProfileImageUrl(), 1, this);
        ImageLoaderUtils.attemptLoadImage(profileBackgroundImage,_imageloader, _currentUser.getProfileBackgroundImageUrl(), 2, null);
        userName.setText(_currentUser.getUserName());
        userDesc.setText(_currentUser.getDescription());

        final SlidingMenu menu = new SlidingMenu(this);
        _userBundle = new Bundle();
        _userBundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _currentUser);

        Log.v(TAG, "current user is " + _currentUser.getScreenName());
        menu.setBackground(new ColorDrawable(Color.BLACK));
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.sliding_menu_fragment);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setBehindOffset(100);
        menu.setFadeEnabled(true);
        menu.setOnOpenedListener(this);
        menu.setOnClosedListener(this);

        showTweetsBut.setText("Tweets    \t \t" + _currentUser.getTotalTweetCount());
        showTweetsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showMenu();
                _currentFragmentState = FragmentState.TWEETS;
            }
        });

        showFriendsBut.setText("Following \t" + _currentUser.getTotalFriendCount());
        showFriendsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showMenu();
                _currentFragmentState = FragmentState.FOLLOWING;
            }
        });

        showFollowersBut.setText("Followers \t" + _currentUser.getTotalFollowerCount());
        showFollowersBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showMenu();
                _currentFragmentState = FragmentState.FOLLOWERS;
            }
        });
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = null;
        switch (position) {
            case 0:
                i = new Intent(UserProfileHomeActivity.this, UserProfileHomeActivity.class);
                i.putExtra(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
                startActivity(i);

                break;
            case 1:
                i = new Intent(UserProfileHomeActivity.this, KeywordGroupActivity.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(UserProfileHomeActivity.this, SettingsScreen.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }


    @Override
    public void onImageLoadComplete(Bitmap bitmap, ImageSettings imageSettings) {
        setCircleDrawable(bitmap, imageSettings.getImageView());
    }

    protected  void setCircleDrawable(Bitmap bitmap_, ImageView imageView_){
        CircleCroppedDrawable d = new CircleCroppedDrawable(bitmap_);
        imageView_.setImageBitmap(null);
        imageView_.setImageDrawable(d);
        imageView_.setBackground(d);
    }


    @Override
    public void onBackPressed() {
        if(!_userQueue.isEmpty()){
            ParcelableUser user = _userQueue.remove(_userQueue.size()-1);
            Log.v(TAG, "_userQueue is not empty, removing user " + user.getScreenName());
            Intent i = new Intent(UserProfileHomeActivity.this, UserProfileHomeActivity.class);
            ArrayList<ParcelableUser> users = new ArrayList<ParcelableUser>();
            users.addAll(_userQueue);
            i.putExtra(TwitterConstants.PARCELABLE_USER_QUEUE, users);
            this.startActivity(i);
        }
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onOpened() {
        changeFragment(getFragmentToCommit());
    }

    @Override
    public void onClosed() {
    }

    private Fragment createFragment(FragmentInfo fragmentInfo_){
        return Fragment.instantiate(this, fragmentInfo_._fragmentClass.getName(), fragmentInfo_._bundle);
    }

    private void showNewFragment(FragmentManager fragmentManager_, FragmentTransaction fragmentTransac_,
                                 FragmentInfo fragmentInfo_){
        String  currentFragmentTag =fragmentInfo_.getTag();
        Fragment currentFragment = fragmentManager_.findFragmentByTag(currentFragmentTag);
        if(currentFragment == null){
            Fragment replacementFragment = createFragment(fragmentInfo_);
            fragmentTransac_.add(R.id.menu_frame, replacementFragment, currentFragmentTag);
        }else{
            fragmentTransac_.show(currentFragment);
        }
        _fragmentTags.push(fragmentInfo_);
    }

    private void hidePreviousFragment(FragmentManager fragmentManager_, FragmentTransaction fragmentTransac_,
                                      FragmentInfo fragmentInfo_){
        if(!_fragmentTags.isEmpty()){
            FragmentInfo previousFragInfo  = _fragmentTags.pop();
            if(previousFragInfo != null){
                if(!TextUtils.equals(previousFragInfo.getTag(), fragmentInfo_.getTag())){
                    fragmentTransac_.setCustomAnimations(R.anim.slide_open_anim, R.anim.slide_close_anim);
                }
                Fragment previousFragment = fragmentManager_.findFragmentByTag(previousFragInfo.getTag());
                fragmentTransac_.hide(previousFragment);
            }
        }else{
            fragmentTransac_.setCustomAnimations(R.anim.slide_open_anim, R.anim.slide_close_anim);
        }
    }

    private void changeFragment(FragmentInfo fragmentInfo_){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        hidePreviousFragment(fragmentManager,fragmentTransaction, fragmentInfo_);
        showNewFragment(fragmentManager, fragmentTransaction, fragmentInfo_);
        fragmentTransaction.commit();
    }

    private FragmentInfo getFragmentToCommit(){
        FragmentInfo fragment = null;
        switch (_currentFragmentState){
            case TWEETS:
                fragment = getFragmentInfo(UserDetailsTimelineTab.class, _userBundle);
                break;
            case FOLLOWING:
                fragment = getFragmentInfo(FriendsTab.class, _userBundle);
                break;
            case FOLLOWERS:

                fragment = getFragmentInfo(FollowersTab.class, _userBundle);
                break;
            default:
                throw new IllegalStateException("A unknown fragment was requested");
        }
        return fragment;
    }

    private FragmentInfo getFragmentInfo(Class<?> fragmentClass_, Bundle bundle_){
        FragmentInfo fragment;
        String key = fragmentClass_.getName();
        if(_fragInfoMap.containsKey(key)){
            fragment = _fragInfoMap.get(key);
        }else{
            fragment = new FragmentInfo(fragmentClass_, bundle_);
            _fragInfoMap.put(fragmentClass_.getName(), fragment);
        }
        return fragment;
    }

    private class FragmentInfo{
        private Class<?> _fragmentClass;
        private Bundle _bundle;

        public FragmentInfo(Class<?> fragmentClass_, Bundle bundle_){
            _fragmentClass = fragmentClass_;
            _bundle  = bundle_;
        }

        public String getTag(){
            return Integer.toString(this.hashCode()) + _fragmentClass.getName();
        }
    }

}
