package com.sun.tweetfiltrr.asyncretriever;

import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;

public class AsyncKeywordGroupLoader  extends AsyncTask<Void, Void, String> {

	TextView _viewToUpdate;
	Lock _lockToSecure;
	Cursor _cursorToIterate;
	
	@Override
	protected void onPostExecute(String result) {
		_viewToUpdate.setText(result);
		super.onPostExecute(result);
	}

	public AsyncKeywordGroupLoader(TextView viewToUpdate_, Lock lockToSecure_, Cursor cursorToIterate_){
		_viewToUpdate = viewToUpdate_;
		_lockToSecure = lockToSecure_;
		_cursorToIterate = cursorToIterate_;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		Collection<String> friends = new ArrayList<String>();
		String groupName;
		String keywords;
		int groupID;
		try{
		_lockToSecure.lock();
			
		groupName =	_cursorToIterate.getString(_cursorToIterate.getColumnIndex(KeywordGroupColumn.COLUMN_GROUP_NAME.s()));
		groupID = _cursorToIterate.getInt(_cursorToIterate.getColumnIndex(FriendColumn.COLUMN_GROUP_ID.s()));
		keywords = 	_cursorToIterate.getString(_cursorToIterate.getColumnIndex(KeywordGroupColumn.COLUMN_KEYWORDS.s()));
		while(!_cursorToIterate.isLast()){
				int currentID= _cursorToIterate.getInt(_cursorToIterate.getColumnIndex(FriendColumn.COLUMN_GROUP_ID.s()));
//			if(groupID == currentID){
				friends.add(_cursorToIterate.getString(_cursorToIterate.getColumnIndex(FriendColumn.FRIEND_NAME.s())));
				_cursorToIterate.moveToNext();
//			}else{
//				break;
//			}

		}
			
		}finally{
			_lockToSecure.unlock();
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(groupName);
		builder.append("\n");
		
		for(String iteam : friends){
			builder.append(iteam);
			builder.append(", ");

		}
		builder.append("\n");
		builder.append(keywords);
		
		return builder.toString();
	}


}
