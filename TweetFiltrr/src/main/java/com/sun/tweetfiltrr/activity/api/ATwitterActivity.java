package com.sun.tweetfiltrr.activity.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.PostTweetActivity;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

/**
 * Created by Sundeep on 10/01/14.
 */
public class ATwitterActivity extends SherlockFragmentActivity {

    private final static String  TAG = ATwitterActivity.class.getName();
    private ParcelableUser _currentUser;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.v(TAG, "Home buttom clicked");
                return true;
            case R.id.tweet_action_bar_button:
                i = new Intent(this, PostTweetActivity.class);
                i.putExtra(TwitterConstants.PARCELABLE_FRIEND_WITH_TIMELINE, _currentUser);
                i.putExtra(TwitterConstants.IS_QUOTE_REPLY, false);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override //TODO check for internet connection becuase if its slow/cant connect tghen we will get null user
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);
        loadActionBar();
    }


    protected ActionBar loadActionBar(){
        ActionBar actionBar =  getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_title, null);
        TextView title = (TextView) v.findViewById(R.id.action_bar_title);
        title.setText("@" + _currentUser.getScreenName());
        actionBar.setCustomView(v);
        return actionBar;
    }

}
