package com.sun.tweetfiltrr.activity.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.EditKeywordGroupAdapter;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordFriendToParcelable;
import com.sun.tweetfiltrr.database.utils.DBUtils;
import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dao.impl.KeywordGroupDao;
import com.sun.tweetfiltrr.database.dao.impl.UserFriendsDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.twitter.callables.BulkFriendRetriever;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class EditKeywordGroupActivity extends SherlockFragmentActivity implements
						LoaderManager.LoaderCallbacks<Cursor>,BulkFriendRetriever.OnFriendLoadFinish {
	
	private EditKeywordGroupAdapter _groupAdapter;
	private static final int TUTORIAL_LIST_LOADER = 0x07;
	private static final String TAG = EditKeywordGroupActivity.class.getName();
    private ParcelableUser _currentUser;
    private ParcelableKeywordGroup _group;
    private  SmoothProgressBarWrapper smoothProgressBarWrapper;
    @Inject FriendDao _friendDao;
    @Inject FriendToParcelable _friendToParcelable;
    @Inject KeywordFriendToParcelable _cursorToParcelable;
    @Inject UrlImageLoader _simpleImageLoader;
    @Inject ExecutorService _threadPool;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TweetFiltrrApplication) getApplication()).getObjectGraph().inject(this);
        setContentView(R.layout.edit_keyworrd_group_layout);
        _group = getIntent().getExtras().getParcelable(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE);
        Log.v(TAG, "keyword group from bundle "  + _group.getGroupName());
        SmoothProgressBar progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        smoothProgressBarWrapper = new SmoothProgressBarWrapper(progressBar);
        ListView listView = (ListView) findViewById(android.R.id.list);
        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);

        String[] cols = new String[]{FriendTable.FriendColumn.FRIEND_ID.s(),
                FriendTable.FriendColumn.FRIEND_NAME.s(), FriendTable.FriendColumn.FRIEND_SCREENNAME.s(),
                FriendTable.FriendColumn.FOLLOWER_COUNT.s(), FriendTable.FriendColumn.FRIEND_COUNT.s(),
                FriendTable.FriendColumn.COLUMN_LAST_FRIEND_INDEX.s(), FriendTable.FriendColumn.COLUMN_CURRENT_FRIEND_COUNT.s(),
                FriendTable.FriendColumn.LAST_FRIEND_PAGE_NO.s(), FriendTable.FriendColumn.IS_FRIEND.s(),
                FriendTable.FriendColumn.PROFILE_IMAGE_URL.s(), FriendTable.FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(),
                FriendTable.FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.s(),
                FriendTable.FriendColumn.DESCRIPTION.s()};

        //TODO this is for testing purposes and needs to be changed to current user instead
        Collection<ParcelableUser> users =   (_friendDao.getEntries(FriendTable.FriendColumn.FRIEND_ID.s()
                + " = ? ", new String[]{Long.toString(15670515l)}, null));

        final ParcelableUser user = users.iterator().next();
        final UserFriendsDao usersToFriendDao= new UserFriendsDao(getContentResolver() , _friendToParcelable,user);
        final Collection<IDatabaseUpdater> _dbUpdaters = new ArrayList<IDatabaseUpdater>();
        _dbUpdaters.add(new DatabaseUpdater(_friendDao, cols));
        _dbUpdaters.add(new DatabaseUpdater(usersToFriendDao));


        Button loadFriends = (Button) findViewById(R.id.load_friends_but);
        loadFriends.setText(_currentUser.getCurrentFollowerCount() + " out of "
                + _currentUser.getTotalFriendCount() + " friend's , load more?");
        loadFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onclicked for load friends");
                if(!BulkFriendRetriever.isLoadingFriends()){
                    smoothProgressBarWrapper.startRefreshAnimation();
                    BulkFriendRetriever r = new BulkFriendRetriever(user, _dbUpdaters,EditKeywordGroupActivity.this );
                    EditKeywordGroupActivity.this._threadPool.submit(r);
                }else{
                    Toast.makeText(EditKeywordGroupActivity.this, "Already loading friends", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button saveButton = (Button) findViewById(R.id.save_group_but);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collection<ParcelableUser> users = _groupAdapter.getChangedGroupIdsForUsers(_group.getGroupId());
                _friendDao.insertOrUpdate(users ,
                        new String[]{FriendTable.FriendColumn.FRIEND_ID.s(), FriendTable.FriendColumn.COLUMN_GROUP_ID.s()});
                Toast.makeText(EditKeywordGroupActivity.this, "Updated " + users.size() + " friends ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // CursorToParcelable<ParcelableUser> friendCursorToParcelable =  new FriendToParcelable();
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
                null,columns, to, 0, _cursorToParcelable, _simpleImageLoader, _group);

        listView.setFastScrollEnabled(true);
        listView.setScrollingCacheEnabled(true);
        listView.setAdapter(_groupAdapter);

        this.getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);

    }


    @Override
    public void finish() {
        super.finish();
    }


    @Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			//String[] projection = KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS;
        String[] pro = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS);
        Log.v(TAG, "on loader create user is: " + _currentUser.toString());
        return new CursorLoader(this,
                TweetFiltrrProvider.CONTENT_URI_FRIENDS_LEFT_GROUP, pro,
                FriendTable.FriendColumn.IS_FRIEND.a() + " = ? ",
                new String[]{Integer.toString(1)}, FriendTable.FriendColumn.FRIEND_NAME.a() + " ASC " );
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg, Cursor cursor) {
        if(cursor != null){
            _groupAdapter.swapCursor(cursor);
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		_groupAdapter.swapCursor(null);
		
	}

    @Override
    public void onBulkFriendLoadFinish(final ParcelableUser user_) {
        final int newFriendCount = EditKeywordGroupActivity.this._currentUser.getCurrentFriendCount() -
                user_.getCurrentFriendCount();
        EditKeywordGroupActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                smoothProgressBarWrapper.setRefreshAnimationFinish();
                Toast.makeText(EditKeywordGroupActivity.this, "Found " + newFriendCount + " new friends", Toast.LENGTH_SHORT).show();
            }
        });
        //switch users to this
        if(newFriendCount > 0){
            _currentUser = user_;
        }
    }
}
