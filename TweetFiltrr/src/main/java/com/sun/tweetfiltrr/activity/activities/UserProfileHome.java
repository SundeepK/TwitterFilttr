package com.sun.tweetfiltrr.activity.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.sun.tweetfiltrr.fragment.fragments.BlankFragment;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTab;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.fragment.fragments.SettingsScreen;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.inject.Inject;

public class UserProfileHome extends ATwitterActivity implements
        ListView.OnItemClickListener, SlidingMenu.OnOpenedListener, SlidingMenu.OnClosedListener {

    public enum FragmentState{
        TWEETS,
        FOLLOWING,
        FOLLOWERS
    }

	private static final String TAG = UserProfileHome.class.getName();
    private ParcelableUser _currentUser;
    private UserDetailsTimelineTab _userDetailsFrag;
    private FriendsTab _userFriendFrag;
    private FollowersTab _userFollowerFrag;
    private Fragment _blankFragment;
    private ArrayList<ParcelableUser> _userQueue; // not a queue but going to use it like one
    private FragmentState _currentFragmentState = FragmentState.TWEETS;
    private Bundle _userBundle;
    private LinkedList<String> _fragmentTags = new LinkedList<String>();
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

        _blankFragment = new BlankFragment();
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

        showTweetsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showMenu();
                _currentFragmentState = FragmentState.TWEETS;
            }
        });

        showFriendsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showMenu();
                _currentFragmentState = FragmentState.FOLLOWING;
            }
        });

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
                i = new Intent(UserProfileHome.this, TwitterUserProfileHome.class);
                i.putExtra(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
                startActivity(i);

                break;
            case 1:
                i = new Intent(UserProfileHome.this, KeywordGroupScreen.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(UserProfileHome.this, SettingsScreen.class);
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
            Intent i = new Intent(UserProfileHome.this, UserProfileHome.class);
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
        Fragment fragmentToDisplay = getFragmentToCommit();
        changeFragment(fragmentToDisplay);
    }

    @Override
    public void onClosed() {
        changeFragment(_blankFragment);
    }

    private void changeFragment(Fragment fragment_){
        String currentFragmentID = Integer.toString(fragment_.hashCode());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(!_fragmentTags.isEmpty()){
            String previousTag = _fragmentTags.pop();
            Fragment previousFragment = fragmentManager.findFragmentByTag(previousTag);
            fragmentTransaction.hide(previousFragment);
        }
        // fragmentTransaction.replace(R.id.menu_frame, fragment_);
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
       // fragmentTransaction.setCustomAnimations(R.anim.slide_open_anim, R.anim.slide_close_anim);
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentFragmentID);
        if(currentFragment == null){
            fragmentTransaction.add(R.id.menu_frame, fragment_, currentFragmentID);
        }else{
            fragmentTransaction.show(currentFragment);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        _fragmentTags.push(currentFragmentID);
    }

    private Fragment getFragmentToCommit(){
        Fragment fragment = null;
        switch (_currentFragmentState){
            case TWEETS:
                if(_userDetailsFrag == null){
                    _userDetailsFrag = new UserDetailsTimelineTab();
                    _userDetailsFrag.setArguments(_userBundle);
                }
                fragment =_userDetailsFrag;
                break;
            case FOLLOWING:
                if(_userFriendFrag == null){
                    _userFriendFrag = new FriendsTab();
                    _userFriendFrag.setArguments(_userBundle);
                }
                fragment = _userFriendFrag;
                break;
            case FOLLOWERS:
                if(_userFollowerFrag == null){
                    _userFollowerFrag = new FollowersTab();
                    _userFollowerFrag.setArguments(_userBundle);
                }
                fragment =_userFollowerFrag;
                break;
            default:
                fragment = _blankFragment;
                break;
        }
        return fragment;
    }
}
