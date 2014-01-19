package com.sun.tweetfiltrr.fragment.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.KeywordGroupAdapter;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.KeywordGroupDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.listview.ExpandableListViewAdapter;
import com.sun.tweetfiltrr.multipleselector.api.OnTextViewLoad;
import com.sun.tweetfiltrr.multipleselector.impl.MultipleItemSelector;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.InputValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;


public class KeywordGroupScreen extends SherlockFragmentActivity implements
						LoaderManager.LoaderCallbacks<Cursor>, OnTextViewLoad<ParcelableUser>,
							MultipleItemSelector.OnClickListener<ParcelableUser>{
	
	private CursorAdapter _dataAdapter;																										
	private static final int TUTORIAL_LIST_LOADER = 0x02;
	private static final String TAG = KeywordGroupScreen.class.getName();
	private MultipleItemSelector<ParcelableUser> _mulitpleSelectorDialog;
	private IDBDao<ParcelableUser> _friendDao;
	private IDBDao<ParcelableKeywordGroup> _keywordGroupDao;
	private View _previousView;
	private static final int REMOVE = 1;
	private static final int ADD = 2;
	private DialogHolder _removeDialog;
	private DialogHolder _addDialog;
	private InputValidator _inputValidator;
	
	
	private enum UpdateType{
		REMOVE,
		ADD;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_listview);
		
		_inputValidator = new InputValidator();
		_removeDialog = new DialogHolder();
		_addDialog = new DialogHolder();
		_keywordGroupDao = new KeywordGroupDao(getContentResolver(), new KeywordToParcelable());
		 
		 EditText groupName = (EditText) findViewById(R.id.group_name);
		 groupName.addTextChangedListener(getGroupNameTextWatcher(groupName));
		 
		 prepareEditTextView("Group name", groupName);
		 EditText groupKeyword = (EditText) findViewById(R.id.group_keyword);
		 prepareEditTextView("Keywords", groupKeyword);
		 
		 Button saveChangesBut = (Button) findViewById(R.id.add_new_group);
		 saveChangesBut.setOnClickListener(getSaveButtonLis(_keywordGroupDao, groupName, groupKeyword));
		 

		 _friendDao = new FriendDao(getContentResolver(), new FriendToParcelable());
			//String[] columns = KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS;
			
			String[] columns = {
					KeywordGroupColumn.COLUMN_ID.s(),
					KeywordGroupColumn.COLUMN_GROUP_NAME.s(),
					KeywordGroupColumn.COLUMN_KEYWORDS.s()
			};

				  
				  // the XML defined views which the data will be bound to
				  int[] to = new int[] {
				    R.id.keyword_group_details
				  };
				  
				  // create an adapter from the SimpleCursorAdapter
				  _dataAdapter = new KeywordGroupAdapter(this, R.layout.listview_for_twitter,
						  null, columns, to,  0, _keywordGroupDao);
				  
				  
				  // get reference to the ListView
				  ListView listView = (ListView) findViewById(android.R.id.list);
				  // Assign adapter to ListView
				  listView.setAdapter(new ExpandableListViewAdapter(_dataAdapter, R.id.toolbar,R.id.expandable_toggle_button, getOnViewLoader()));
				   getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null,  this);
				   
					ArrayList<ParcelableUser> strings = new ArrayList<ParcelableUser>();

					 _mulitpleSelectorDialog = new MultipleItemSelector<ParcelableUser>("Select Friends", 
							 this, R.layout.multiple_selector,
							R.id.item_checkbox, R.id.item_text, strings, this, this);
		
		
	};
	

	private TextWatcher getGroupKeywordTextWatcher(final EditText groupKeyword_){
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(_inputValidator.compareWordCount(s.toString(), 10)){
					groupKeyword_.setError("Group name cannot be empty");
				}else{
					groupKeyword_.setError(null);
				}
			}
		};
	}
	
	private TextWatcher getGroupNameTextWatcher(final EditText groupName_){
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(_inputValidator.checkNullInput(s.toString())){
					groupName_.setError("Group name cannot be empty");
				}else{
					groupName_.setError(null);
				}
			}
		};
	}
	
	
	
	private OnClickListener getSaveButtonLis(final IDBDao<ParcelableKeywordGroup> keywordGroupDao_,
			final EditText groupName_, final EditText groupKeywords_){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					String name = groupName_.getText().toString().toLowerCase(Locale.US);
					String keywords = groupKeywords_.getText().toString().toLowerCase(Locale.US);
					ParcelableKeywordGroup keywordGroup = new ParcelableKeywordGroup(name, keywords);
					Collection<ParcelableKeywordGroup> keywordGroups = new ArrayList<ParcelableKeywordGroup>();
					
					keywordGroups = keywordGroupDao_.getEntries(KeywordGroupColumn.COLUMN_GROUP_NAME.s() + " =? ", new String[]{name}, null);
					
					if(keywordGroups.size() == 0){
						keywordGroups.add(keywordGroup);
						keywordGroupDao_.insertOrUpdate(keywordGroups);
						groupKeywords_.setText("");
						groupName_.setText("");
					}
									
			}
		};
	}
	
	private void prepareEditTextView(final String default_, final EditText editText_){
		editText_.setTextColor(Color.GRAY);
		editText_.setText(default_);		
		
		editText_.setOnFocusChangeListener(new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {    
	        	
	        	EditText editText = (EditText) v;
	        	
	            if(!hasFocus && TextUtils.isEmpty(editText.getText().toString())){
	            	editText.setTextColor(Color.GRAY);
	            	editText.setText(default_);
	            	return;
	            } else if (hasFocus && editText.getText().toString().equals(default_)){
	        		editText_.setTextColor(Color.BLACK);
	            	editText.setText("");
	            	return;
	            } 
            	editText.setTextColor(Color.BLACK);
	        }
	    }); 
	}
	
	private ExpandableListViewAdapter.OnExpandingViewLoad getOnViewLoader(){
		
		return 	new ExpandableListViewAdapter.OnExpandingViewLoad(){
			
		@Override
		public void onViewLoad( View expandingView_,  int position_,  long rowId_) {
			Button removeFriendsBut = (Button) expandingView_.findViewById(R.id.remove_friends);
			Button addFriendsBut = (Button) expandingView_.findViewById(R.id.add_friends);
			final String position = rowId_ + "" ;

			removeFriendsBut.setOnClickListener(getOnClickMultiSelectorButton(FriendColumn.COLUMN_GROUP_ID.s() + " =? ",
					new String[] { position },
					FriendColumn.FRIEND_NAME.s() + " ASC ",
					expandingView_, _removeDialog, _mulitpleSelectorDialog,
					REMOVE, rowId_, UpdateType.REMOVE));
			addFriendsBut.setOnClickListener(getOnClickMultiSelectorButton(FriendColumn.COLUMN_GROUP_ID.s() + " IS NULL OR " + FriendColumn.COLUMN_GROUP_ID.s() + " != ? ",
					new String[] { position },
					FriendColumn.FRIEND_NAME.s() + " ASC ",
					expandingView_, _addDialog, _mulitpleSelectorDialog,
					ADD, rowId_, UpdateType.ADD));
	
			}
		};
		
	}
	

	
	private OnClickListener getOnClickSaveButton(final IDBDao<ParcelableUser> friendDao_,
			final long groupId_, final DialogHolder removeFriends_, final DialogHolder addFriends_ ){
		
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).setTitle("Confirm changes")
						.setMessage(" Are you sure you would like to apply these changes? ")
						.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//setFriendsGroupId(removeFriends_._friends, groupId_);
								setFriendsGroupId(addFriends_._friends, groupId_);
								friendDao_.insertOrUpdate(addFriends_._friends, new String[]{FriendColumn.FRIEND_ID.s(),FriendColumn.COLUMN_GROUP_ID.s()});
							}
							
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).create();
				dialog.show();
				
				
			}
		};
		
	}
	
	private void setFriendsGroupId(Collection<ParcelableUser> friends_, long groupId_){
		if (friends_!= null){
			for (ParcelableUser friend : friends_) {
				friend.setGroupId((int) groupId_);
			}
		}	
	}
	
	private OnClickListener getOnClickMultiSelectorButton(final String selection_, final String[] selectionArgs_, final String sortOrder_, final  View expandingView_, final DialogHolder holder_,
			final MultipleItemSelector<ParcelableUser> mulitpleSelectorDialog_, final int tag_, final long groupId_, final UpdateType type_){
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				mulitpleSelectorDialog_.clearAdapter();
		
				final Collection<ParcelableUser> friends = KeywordGroupScreen.this._friendDao
						.getEntries(selection_,	selectionArgs_,	sortOrder_);
					
				mulitpleSelectorDialog_.addToAdapter(friends);
				KeywordGroupScreen.this._previousView = expandingView_;
				mulitpleSelectorDialog_.setTag(tag_);
				holder_._groupRowId = groupId_;
				holder_._type = type_;
				mulitpleSelectorDialog_.showDialog();

			}
		};
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	
		//String[] projection = KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS;
		
		String[] projection = {
				KeywordGroupColumn.COLUMN_ID.s(),
				KeywordGroupColumn.COLUMN_GROUP_NAME.s(),
				KeywordGroupColumn.COLUMN_KEYWORDS.s()
		};

		
			  CursorLoader cursorLoader = new CursorLoader(this,
			    TweetFiltrrProvider.CONTENT_URI_GROUP, projection, null, null, KeywordGroupColumn.COLUMN_ID.s() + " ASC " );
			  return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg, Cursor cursor) {
		_dataAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		_dataAdapter.swapCursor(null);
		
	}

	@Override
	public void onPostTextViewLoad(TextView textView_, ParcelableUser item_) {
		textView_.setText(item_.getUserName());
	}

	@Override
	public void onNegativeClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPositiveClick(Collection<ParcelableUser> selectedItems_,
			DialogInterface dialog_, int which_) {

		if(_previousView != null){	
			if(_mulitpleSelectorDialog.getTag() == REMOVE){
				buildGroupTextView(_removeDialog,selectedItems_, R.id.friends_to_remove );
				_friendDao.updateToNull(_removeDialog._friends, new String[]{FriendColumn.FRIEND_ID.s(),FriendColumn.COLUMN_GROUP_ID.s()});
			}else if(_mulitpleSelectorDialog.getTag() == ADD){
				buildGroupTextView(_addDialog,selectedItems_, R.id.friends_to_add);
				setFriendsGroupId(_addDialog._friends, _addDialog._groupRowId);
				_friendDao.insertOrUpdate(_addDialog._friends, new String[]{FriendColumn.FRIEND_ID.s(),FriendColumn.COLUMN_GROUP_ID.s()});
			}
		}

	
	}
	
	private void buildGroupTextView(DialogHolder dialogHolder_, Collection<ParcelableUser> friends_, int textViewId_){

		if (friends_.size() > 0) {
			StringBuilder builder = new StringBuilder();

			if(dialogHolder_._type == UpdateType.ADD){
				builder.append("+ ");
			}else{
				builder.append("- ");
			}
			
			dialogHolder_._friends = friends_;
			
			Iterator<ParcelableUser> friendIterator = friends_.iterator();
			
			while(friendIterator.hasNext()){
				ParcelableUser friend = friendIterator.next();
				
				if(!friendIterator.hasNext()){
					builder.append(friend.getUserName());
					builder.append(".");
				}else{
					builder.append(friend.getUserName());
					builder.append(", ");
				}
				
			}
			TextView removeFriend = (TextView) _previousView
					.findViewById(textViewId_);
			
			removeFriend.setVisibility(View.VISIBLE);
			removeFriend.setText(builder.toString());
		}
	}

	private class DialogHolder{
		private long _groupRowId;
		private UpdateType _type;
		private Collection<ParcelableUser> _friends;
	}
	
	
}
