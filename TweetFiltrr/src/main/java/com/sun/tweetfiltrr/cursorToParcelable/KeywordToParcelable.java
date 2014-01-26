package com.sun.tweetfiltrr.cursorToParcelable;

import android.database.Cursor;
import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;

import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;

public class KeywordToParcelable implements CursorToParcelable<ParcelableKeywordGroup> {

	private static final String TAG = KeywordToParcelable.class.getName();

	@Override
	public ParcelableKeywordGroup getParcelable(Cursor cursorKeywordGroup_) {
//		long groupId = cursorKeywordGroup_.getLong(cursorKeywordGroup_
//				.getColumnIndexOrThrow(KeywordGroupColumn.COLUMN_ID.s()));
//		String groupName = cursorKeywordGroup_.getString(cursorKeywordGroup_
//				.getColumnIndexOrThrow("keywordGroupTable_groupName"));
//		String groupKeywords = cursorKeywordGroup_.getString(cursorKeywordGroup_
//				.getColumnIndexOrThrow("keywordGroupTable_keywords"));

        long groupId = cursorKeywordGroup_.getLong(cursorKeywordGroup_
                .getColumnIndexOrThrow(KeywordGroupColumn.COLUMN_ID.a()));
        String groupName = cursorKeywordGroup_.getString(cursorKeywordGroup_
                .getColumnIndexOrThrow(KeywordGroupColumn.COLUMN_GROUP_NAME.a()));
        String groupKeywords = cursorKeywordGroup_.getString(cursorKeywordGroup_
                .getColumnIndexOrThrow(KeywordGroupColumn.COLUMN_KEYWORDS.a()));


        Log.v(TAG, "GroupID is : " + groupId + " with name " + groupName + " and keyword " + groupKeywords);

		ParcelableKeywordGroup group = new ParcelableKeywordGroup(groupId, groupName, groupKeywords);
		
		return group;
	}

	

}
