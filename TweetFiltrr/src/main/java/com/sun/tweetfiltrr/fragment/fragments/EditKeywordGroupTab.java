package com.sun.tweetfiltrr.fragment.fragments;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
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
import com.sun.tweetfiltrr.database.dao.UserFriendsDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.database.tables.UsersToFriendsTable;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;


public class EditKeywordGroupTab extends SherlockFragment implements
						LoaderManager.LoaderCallbacks<Cursor>{
	
	private CursorAdapter _groupAdapter;
	private static final int TUTORIAL_LIST_LOADER = 0x02;
	private static final String TAG = EditKeywordGroupTab.class.getName();
	private IDBDao<ParcelableUser> _friendDao;
	private IDBDao<ParcelableKeywordGroup> _keywordGroupDao;
    private ParcelableUser _currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParcelableKeywordGroup group = getActivity().getIntent().getParcelableExtra(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE);


        Activity activity = getActivity();
        ContentResolver resolver = activity.getContentResolver();
        UrlImageLoader imageLoader = TwitterUtil.getInstance().getGlobalImageLoader(getActivity());
        _currentUser = UserRetrieverUtils.getCurrentLoggedInUser(activity);

        CursorToParcelable<ParcelableUser> friendCursorToParcelable =  new FriendToParcelable();
        CursorToParcelable<ParcelableKeywordGroup> keywordToParcelable = new KeywordToParcelable();
        _keywordGroupDao = new KeywordGroupDao(resolver,  keywordToParcelable  );
        _friendDao = new FriendDao(resolver, friendCursorToParcelable);

        CursorToParcelable<ParcelableUser> cursorToParcelable = new KeywordFriendToParcelable(friendCursorToParcelable, keywordToParcelable);
        String[] columns = {
                KeywordGroupColumn.COLUMN_ID.s(),
                KeywordGroupColumn.COLUMN_GROUP_NAME.s(),
                KeywordGroupColumn.COLUMN_KEYWORDS.s()
        };


        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.user_name
        };

        // create an adapter from the SimpleCursorAdapter
        _groupAdapter = new EditKeywordGroupAdapter(getActivity(), R.layout.listview_for_twitter,
                null, columns, to, 0, cursorToParcelable, imageLoader, group);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_listview, container, false);
        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        listView.setAdapter(_groupAdapter);
        getActivity().getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);
        return rootView;
    }

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	
		//String[] projection = KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS;
        String[] pro = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, UserFriendsDao.FULLY_QUALIFIED_PROJECTIONS);
        Log.v(TAG, "on loader create user is: " + _currentUser.toString());
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_USER_TO_FRIEND, pro,
                UsersToFriendsTable.UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s() + "." + UsersToFriendsTable.UsersToFriendsColumn.USER_ID.s() + "=?",
                new String[]{"" + _currentUser.getUserId()}, FriendTable.FriendColumn._ID.s() + " ASC " );
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
