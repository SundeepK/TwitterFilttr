package com.sun.tweetfiltrr.database.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.database.utils.DBUtils;
import com.sun.tweetfiltrr.database.dao.api.ADBDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;
@Singleton
public class KeywordGroupDao extends ADBDao<ParcelableKeywordGroup> {
	private static final String TAG = KeywordGroupDao.class.getName();
	
	public static final String[] FULLY_QUALIFIED_PROJECTIONS = DBUtils.getFullyQualifiedProjections(KeywordGroupColumn.values());
    public static final String[] PROJECTIONS = DBUtils.getprojections(KeywordGroupColumn.values());

    Uri _keywordGroupUri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_GROUP + "/" + TweetFiltrrProvider.GROUP_TABLE_ID);
	@Inject
	public KeywordGroupDao(ContentResolver contentResolver_,KeywordToParcelable cursorToKeyword_){
		super(contentResolver_, cursorToKeyword_);
	}
	

	@Override
	public Collection<ParcelableKeywordGroup> getEntry(long rowID_,
			String selection_, String[] selectionArgs_, String sortOrder_) {
		
		Uri uri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_GROUP + "/"
				+ rowID_);
		Cursor cursorFriend = _contentResolver.query(uri, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
		Log.v(TAG, "before getting groupTime" + cursorFriend.getCount());
        return processCursor(cursorFriend);
	}


	@Override
	public Cursor getCursor(String selection_, String[] selectionArgs_, String sortOrder_) {
        return _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_TIMELINE, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
	}

	@Override
	public Collection<ParcelableKeywordGroup> getEntries(String selection_,
			String[] selectionArgs_, String sortOrder_) {

		Cursor cursorFriend = _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_GROUP, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
        return processCursor(cursorFriend);
	}


	@Override
	protected Uri getUpdateUri() {
		return _keywordGroupUri;
	}

    @Override
    protected String[] getTableColumns() {
        return PROJECTIONS;
    }


    @Override
	protected ContentValues getContentValues(ParcelableKeywordGroup keywordGroup_,
                                             String[] columns_, boolean shouldSetNull_) {
		ContentValues contentValue = new ContentValues();

		for(String column : columns_){
			
            if (shouldSetNull_) {
                if (!(TextUtils.equals(column, KeywordGroupColumn.COLUMN_ID.s())  ||
                        !TextUtils.equals(column, FriendColumn._ID.s()))) {
                    contentValue.putNull(column);
                    Log.v(TAG, "im null value");
                    continue ;
                }
            }

            if(TextUtils.equals(column, KeywordGroupColumn.COLUMN_GROUP_NAME.s())){
                contentValue.put(KeywordGroupColumn.COLUMN_GROUP_NAME.s(),keywordGroup_.getGroupName());
            }else if(TextUtils.equals(column, KeywordGroupColumn.COLUMN_KEYWORDS.s())){
                contentValue.put(KeywordGroupColumn.COLUMN_KEYWORDS.s(),keywordGroup_.getGroupKeywords());
            }

		}
		
		return contentValue;
	}


	@Override
	public int deleteEntries(Collection<ParcelableKeywordGroup> entries_) {
		int count = 0;
			
		for(ParcelableKeywordGroup group : entries_){
			count += deleteEntry(group.getGroupId());
		}
		
		return count;
	}


	@Override
	public int deleteEntry(long rowID_) {
		int count = 0;
			Uri uri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_GROUP + "/"	);
			count += _contentResolver.delete(_keywordGroupUri, KeywordGroupColumn.COLUMN_ID.s() + "=?", new String[]{rowID_ + ""});
		return count;
	}

}
