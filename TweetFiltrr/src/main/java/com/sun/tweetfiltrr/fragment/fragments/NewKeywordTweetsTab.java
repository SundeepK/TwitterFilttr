package com.sun.tweetfiltrr.fragment.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.NewTweetsCursorAdapter;
import com.sun.tweetfiltrr.alarm.TwitterUpdateReceiver;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.concurrent.ThreadPoolExecutor;

public class NewKeywordTweetsTab extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private ParcelableUser _currentUser;
	private UrlImageLoader  _sicImageLoader;
	private ListView _tweetsListView;
    private static final String TAG = NewKeywordTweetsTab.class.getName();
    private CursorAdapter _dataAdapter;
    private static final int LIST_LOADER = 0x01;


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = this.getArguments();

			_currentUser = bundle.getParcelable(TwitterConstants.FRIENDS_BUNDLE);
			
		
		ThreadPoolExecutor _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();

//		 UrlImageLoaderConfiguration configs = new UrlImageLoaderConfiguration.Builder(getActivity())
//	        .setMaxCacheMemorySize(1)
//	        .setDirectoryName("/storage/sdcard0/Pictures/twitterFiltrr")
//	        .setImageQuality(100)
//	        .setThreadExecutor(_threadExecutor)
//	        .setImageType(CompressFormat.JPEG)
//	        .setImageConfig(Bitmap.Config.ARGB_8888)
//	        .useExternalStorage(true)
//	        .build();
//	        UrlImageLoader.getInstance().init(configs);
	        
		   _sicImageLoader = TwitterUtil.getInstance().getGlobalImageLoader(getActivity());

	}



	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_home_new_tweets_tab, container, false);
        View moreLayout = inflater.inflate(R.layout.user_home_new_tweets_list_header,null);
        Button refreshButton = (Button) moreLayout.findViewById(R.id.refresh);
        refreshButton.setOnClickListener(refreshButtonOnClickLis());
        String[] columns = new String[] {
                "_id",
                "friendTable_totalNewTweets",
                "friendTable_profileImageUrl"
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.friend_desc,
                R.id.friend_name,
                R.id.list_image
        };

        _dataAdapter = new NewTweetsCursorAdapter(getActivity(), R.layout.listview_for_twitter, null, columns, to,  0, _sicImageLoader);
        _tweetsListView = (ListView) rootView.findViewById(android.R.id.list);
        _tweetsListView.addHeaderView(moreLayout);
        _tweetsListView.setAdapter(_dataAdapter);

        getActivity().getSupportLoaderManager().initLoader(LIST_LOADER, null,  this);

        return rootView;
    }

    private View.OnClickListener refreshButtonOnClickLis() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAlarmReceiver();
            }
        };

    }


    private void scheduleAlarmReceiver() {
        Log.v(TAG, "Setting up alarm receiver");

        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getActivity(), 0, new Intent(getActivity(), TwitterUpdateReceiver.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        // Use inexact repeating which is easier on battery (system can phase events and not wake at exact times)
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(), 1000 * 60 * 60, pendingIntent);
//		      setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, TwitterConstants.ALARM_TRIGGER_AT_TIME,
//		    		  TwitterConstants.ALARM_INTERVAL, pendingIntent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

//        String[] projection = {
//                FriendTable.FriendColumn._ID.s(),
//                FriendTable.FriendColumn.TOTAL_NEW_TWEETS.s(),
//                FriendTable.FriendColumn.FRIEND_NAME.s(),
//                FriendTable.FriendColumn.FRIEND_SCREENNAME.s(),
//                FriendTable.FriendColumn.PROFILE_IMAGE_URL.s()
//        };

      //  String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS);


        Log.v(TAG, "on loader create user is: " +_currentUser.toString() );
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_FRIEND, FriendDao.FULLY_QUALIFIED_PROJECTIONS,
                FriendTable.FriendColumn.TOTAL_NEW_TWEETS.s() + " > 0 ",
                null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        _dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _dataAdapter.swapCursor(null);
    }
}
