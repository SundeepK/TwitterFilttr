package com.sun.tweetfiltrr.activity.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.TwitterUserHomeTabsAdapter;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.fragment.fragments.SettingsScreen;
import com.sun.tweetfiltrr.fragment.fragments.SlidingMenuFragment;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

public class TwitterFilttrLoggedInUserHome extends ATwitterActivity implements TabListener,
        ListView.OnItemClickListener, ImageTaskListener {

	private ViewPager _asyncBackgroundViewPager;
	private TwitterUserHomeTabsAdapter _tabsAdapter;
	private static final String TAG = TwitterFilttrLoggedInUserHome.class.getName();

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.user_home_viewpager);
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.LEFT);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu_fragment);
        menu.setBehindOffset(200);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, new SlidingMenuFragment())
                .commit();

//        _slidingMenuListView.setOnItemClickListener(this);

        UrlImageLoader loader = TwitterUtil.getInstance().getGlobalImageLoader(this);
        ImageView slidingMenuBackground = (ImageView) findViewById(R.id.sliding_menu_background);
        ImageLoaderUtils.attemptLoadImage(slidingMenuBackground, loader, getCurrentUser().getProfileBackgroundImageUrl(), 2, this);

        _asyncBackgroundViewPager = (ViewPager) findViewById(R.id.user_view_pager);
		_tabsAdapter = new TwitterUserHomeTabsAdapter(getSupportFragmentManager(), getCurrentUser());
		_asyncBackgroundViewPager.setAdapter(_tabsAdapter);

//        _asyncBackgroundViewPager = (AsyncBackgroundViewPager) findViewById(R.id.user_view_pager);
//		_tabsAdapter = new TwitterUserHomeTabsAdapter(getSupportFragmentManager(), _currentUser);
//		_asyncBackgroundViewPager.setAdapter(_tabsAdapter);
//		_asyncBackgroundViewPager.setExternalStorageDir("/storage/sdcard0/Pictures/twitterFiltrr", 2);
//
//        if(!TextUtils.isEmpty(_currentUser.getProfileBackgroundImageUrl())){
//
//            try {
//			_asyncBackgroundViewPager.loadImage(new URI(_currentUser.getProfileBackgroundImageUrl()), 2, false);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//        }

	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = null;
        switch (position) {
            case 0:
                i = new Intent(TwitterFilttrLoggedInUserHome.this, TwitterFilttrUserHome.class);
                i.putExtra(TwitterConstants.FRIENDS_BUNDLE, getCurrentUser());
                startActivity(i);

                break;
            case 1:
                i = new Intent(TwitterFilttrLoggedInUserHome.this, KeywordGroupScreen.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(TwitterFilttrLoggedInUserHome.this, SettingsScreen.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    public void preImageLoad(ImageSettings imageSettings) {

    }

    @Override
    public void onImageLoadComplete(Bitmap bitmap, ImageSettings imageSettings) {
        //_slidingMenuListView.setBackground(new BitmapDrawable(getResources(),bitmap).);

    }

    @Override
    public void onImageLoadFail(FailedTaskReason failedTaskReason, ImageSettings imageSettings) {

    }
}
