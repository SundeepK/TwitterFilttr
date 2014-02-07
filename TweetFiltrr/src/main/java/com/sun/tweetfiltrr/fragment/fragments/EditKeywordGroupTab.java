package com.sun.tweetfiltrr.fragment.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.EditKeywordGroupAdapter;
import com.sun.tweetfiltrr.cursorToParcelable.CursorToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordFriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.KeywordGroupDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;


public class EditKeywordGroupTab extends SherlockFragmentActivity implements
						LoaderManager.LoaderCallbacks<Cursor>{
	
	private CursorAdapter _groupAdapter;
	private static final int TUTORIAL_LIST_LOADER = 0x07;
	private static final String TAG = EditKeywordGroupTab.class.getName();
	private IDBDao<ParcelableUser> _friendDao;
	private IDBDao<ParcelableKeywordGroup> _keywordGroupDao;
    private ParcelableUser _currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_keyworrd_group_layout);
        ParcelableKeywordGroup group = getIntent().getExtras().getParcelable(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE);
        Log.v(TAG, "keyword group from bundle "  + group.getGroupName());


        ListView listView = (ListView) findViewById(android.R.id.list);

        ContentResolver resolver = this.getContentResolver();
        UrlImageLoader imageLoader = TwitterUtil.getInstance().getGlobalImageLoader(this);
        _currentUser = UserRetrieverUtils.getCurrentLoggedInUser(this);

        CursorToParcelable<ParcelableUser> friendCursorToParcelable =  new FriendToParcelable();
        CursorToParcelable<ParcelableKeywordGroup> keywordToParcelable = new KeywordToParcelable();
        _keywordGroupDao = new KeywordGroupDao(resolver,  keywordToParcelable  );
        _friendDao = new FriendDao(resolver, friendCursorToParcelable);

        CursorToParcelable<ParcelableUser> cursorToParcelable =
                new KeywordFriendToParcelable(friendCursorToParcelable, keywordToParcelable);
//        String[] columns = {
//                KeywordGroupColumn.COLUMN_ID.s(),
//                KeywordGroupColumn.COLUMN_GROUP_NAME.s(),
//                KeywordGroupColumn.COLUMN_KEYWORDS.s()
//        };

//        String[] columns = {
//                KeywordGroupColumn.COLUMN_ID.s(),
//                KeywordGroupColumn.COLUMN_GROUP_NAME.s(),
//                KeywordGroupColumn.COLUMN_KEYWORDS.s()
//        };

        String[] columns = new String[]{
                "_id",
                "friendTable_friendName",
                "friendTable_profileImageUrl"
        };



        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.user_name
        };

        // create an adapter from the SimpleCursorAdapter
        _groupAdapter = new EditKeywordGroupAdapter(this, R.layout.listview_for_twitter,
                null,columns, to, 0, cursorToParcelable, imageLoader, group, _friendDao);

        listView.setFastScrollEnabled(true);
        listView.setScrollingCacheEnabled(true);
        listView.setAdapter(_groupAdapter);


        this.getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);


    }


	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	
		//String[] projection = KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS;
        String[] pro = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS);
        Log.v(TAG, "on loader create user is: " + _currentUser.toString());
        CursorLoader cursorLoader = new CursorLoader(this,
                TweetFiltrrProvider.CONTENT_URI_FRIENDS_LEFT_GROUP, pro,
                FriendTable.FriendColumn.IS_FRIEND.a() + " = ? ",
                new String[]{"1"}, FriendTable.FriendColumn.FRIEND_NAME.a() + " ASC " );
        return cursorLoader;


	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg, Cursor cursor) {
		_groupAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		_groupAdapter.swapCursor(null);
		
	}


	
}
