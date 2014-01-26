package com.sun.tweetfiltrr.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.sun.tweetfiltrr.fragment.fragments.EditKeywordGroupTab;
import com.sun.tweetfiltrr.fragment.fragments.KeywordGroupTab;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.utils.TwitterConstants;


public class KeywordGroupTabsAdapter extends FragmentPagerAdapter {
    private static final String TAG = KeywordGroupTabsAdapter.class.getName();
    private FragmentManager _fragManager;
    private Fragment _first;
    private Fragment _currentFragment;

    public KeywordGroupTabsAdapter(FragmentManager fm) {
		super(fm);
        _fragManager = fm;
	}

    public void onSwitchToNextFragment(ParcelableKeywordGroup group_) {
        _fragManager.beginTransaction().remove(_currentFragment)
                .commitAllowingStateLoss();

        Bundle bundle = new Bundle();
        bundle.putParcelable(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE, group_);
        Log.v(TAG, "inside keywordgroup dater with group " + group_.getGroupName());
        Fragment frag =  new EditKeywordGroupTab();
        frag.setArguments(bundle);
        _currentFragment = frag;
        notifyDataSetChanged();

    }


	@Override
	    public Fragment getItem(int index) {

	        switch (index) {
	        case 0:

                if(_first == null){
                    _first =  new KeywordGroupTab();
                }

                return _first;
	        case 1:

                if(_currentFragment==null){
                    _currentFragment = new EditKeywordGroupTab();
                Log.v(TAG, "current tab name" + _currentFragment.toString());
                }

                return _currentFragment;

	        case 2:

	        }
	 
	        return null;
	    }
	 
	    @Override
	    public int getCount() {
	        return 2;
	    }

    @Override
    public int getItemPosition(Object object)
    {
        if (object instanceof KeywordGroupTab)
            return POSITION_UNCHANGED;

        return POSITION_NONE;
    }

    @Override
    public float getPageWidth(int position) {
        switch (position){
            case 1:
                return 0.8f;
            case 2:
                return 0.8f;
            default:
                return 1;
      }
    }


}
