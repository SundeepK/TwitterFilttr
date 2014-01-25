package com.sun.tweetfiltrr.fragment.fragments;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.KeywordGroupAdapter;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.KeywordGroupDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.multipleselector.api.OnTextViewLoad;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.InputValidator;

import java.util.Collection;
import java.util.Locale;

import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;


public class KeywordGroupTab extends SherlockFragment implements
						LoaderManager.LoaderCallbacks<Cursor>, OnTextViewLoad<ParcelableUser>{
	
	private CursorAdapter _groupAdapter;
	private static final int TUTORIAL_LIST_LOADER = 0x02;
	private static final String TAG = KeywordGroupTab.class.getName();
	private IDBDao<ParcelableUser> _friendDao;
	private IDBDao<ParcelableKeywordGroup> _keywordGroupDao;
	private InputValidator _inputValidator;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        ContentResolver resolver = activity.getContentResolver();
        _inputValidator = new InputValidator();
        _keywordGroupDao = new KeywordGroupDao(resolver, new KeywordToParcelable());
        _friendDao = new FriendDao(resolver, new FriendToParcelable());

        //String[] columns = KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS;

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
        _groupAdapter = new KeywordGroupAdapter(getActivity(), R.layout.listview_for_twitter,
                null, columns, to, 0, _keywordGroupDao);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_listview, container, false);
        ListView listView = (ListView) rootView.findViewById(android.R.id.list);

        EditText groupName = (EditText) rootView.findViewById(R.id.group_name);
        groupName.addTextChangedListener(getGroupNameTextWatcher(groupName));
        prepareEditTextView("Group name", groupName);

        EditText groupKeyword = (EditText) rootView.findViewById(R.id.group_keyword);
        prepareEditTextView("Keywords", groupKeyword);

        Button saveChangesBut = (Button) rootView.findViewById(R.id.add_new_group);
        saveChangesBut.setOnClickListener(getSaveButtonLis(_keywordGroupDao, groupName, groupKeyword));

        // Assign adapter to ListView
        listView.setAdapter(_groupAdapter);
        getActivity().getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);
        return rootView;
    }


	

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

                    Collection<ParcelableKeywordGroup>	keywordGroups = keywordGroupDao_.getEntries(KeywordGroupColumn.COLUMN_GROUP_NAME.s() + " =? ", new String[]{name}, null);
					
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
	
//	private ExpandableListViewAdapter.OnExpandingViewLoad getOnViewLoader(){
//
//		return 	new ExpandableListViewAdapter.OnExpandingViewLoad(){
//
//		@Override
//		public void onViewLoad( View expandingView_,  int position_,  long rowId_) {
//			Button removeFriendsBut = (Button) expandingView_.findViewById(R.id.remove_friends);
//			Button addFriendsBut = (Button) expandingView_.findViewById(R.id.add_friends);
//			final String position = rowId_ + "" ;
//
//			removeFriendsBut.setOnClickListener(getOnClickMultiSelectorButton(FriendColumn.COLUMN_GROUP_ID.s() + " =? ",
//					new String[] { position },
//					FriendColumn.FRIEND_NAME.s() + " ASC ",
//					expandingView_, _removeDialog, _mulitpleSelectorDialog,
//					REMOVE, rowId_, UpdateType.REMOVE));
//			addFriendsBut.setOnClickListener(getOnClickMultiSelectorButton(FriendColumn.COLUMN_GROUP_ID.s() + " IS NULL OR " + FriendColumn.COLUMN_GROUP_ID.s() + " != ? ",
//					new String[] { position },
//					FriendColumn.FRIEND_NAME.s() + " ASC ",
//					expandingView_, _addDialog, _mulitpleSelectorDialog,
//					ADD, rowId_, UpdateType.ADD));
//
//			}
//		};
//
//	}
//
//
//
//	private OnClickListener getOnClickSaveButton(final IDBDao<ParcelableUser> friendDao_,
//			final long groupId_, final DialogHolder removeFriends_, final DialogHolder addFriends_ ){
//
//		return new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("Confirm changes")
//						.setMessage(" Are you sure you would like to apply these changes? ")
//						.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								//setFriendsGroupId(removeFriends_._friends, groupId_);
//								setFriendsGroupId(addFriends_._friends, groupId_);
//								friendDao_.insertOrUpdate(addFriends_._friends, new String[]{FriendColumn.FRIEND_ID.s(),FriendColumn.COLUMN_GROUP_ID.s()});
//							}
//
//						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								// TODO Auto-generated method stub
//
//							}
//						}).create();
//				dialog.show();
//
//
//			}
//		};
//
//	}
//
//	private void setFriendsGroupId(Collection<ParcelableUser> friends_, long groupId_){
//		if (friends_!= null){
//			for (ParcelableUser friend : friends_) {
//				friend.setGroupId((int) groupId_);
//			}
//		}
//	}
//
//	private OnClickListener getOnClickMultiSelectorButton(final String selection_, final String[] selectionArgs_, final String sortOrder_, final  View expandingView_, final DialogHolder holder_,
//			final MultipleItemSelector<ParcelableUser> mulitpleSelectorDialog_, final int tag_, final long groupId_, final UpdateType type_){
//		return new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mulitpleSelectorDialog_.clearAdapter();
//
//				final Collection<ParcelableUser> friends = KeywordGroupTab.this._friendDao
//						.getEntries(selection_,	selectionArgs_,	sortOrder_);
//
//				mulitpleSelectorDialog_.addToAdapter(friends);
//				KeywordGroupTab.this._previousView = expandingView_;
//				mulitpleSelectorDialog_.setTag(tag_);
//				holder_._groupRowId = groupId_;
//				holder_._type = type_;
//				mulitpleSelectorDialog_.showDialog();
//
//			}
//		};
//	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	
		//String[] projection = KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS;
		
		String[] projection = {
				KeywordGroupColumn.COLUMN_ID.s(),
				KeywordGroupColumn.COLUMN_GROUP_NAME.s(),
				KeywordGroupColumn.COLUMN_KEYWORDS.s()
		};

			  CursorLoader cursorLoader = new CursorLoader(getActivity(),
			    TweetFiltrrProvider.CONTENT_URI_GROUP, projection, null, null, KeywordGroupColumn.COLUMN_ID.s() + " ASC " );
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

	@Override
	public void onPostTextViewLoad(TextView textView_, ParcelableUser item_) {
		textView_.setText(item_.getUserName());
	}


	
}
