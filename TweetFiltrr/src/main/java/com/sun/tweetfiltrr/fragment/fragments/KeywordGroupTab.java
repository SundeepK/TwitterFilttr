package com.sun.tweetfiltrr.fragment.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.sun.tweetfiltrr.utils.TwitterConstants;

import java.util.Collection;
import java.util.Locale;

import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;


public class KeywordGroupTab extends SherlockFragment implements
						LoaderManager.LoaderCallbacks<Cursor>, OnTextViewLoad<ParcelableUser>, AdapterView.OnItemClickListener {
	
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

        EditText groupName = (EditText) rootView.findViewById(R.id.group_name);
        groupName.addTextChangedListener(getGroupNameTextWatcher(groupName));
        prepareEditTextView("Group name", groupName);

        EditText groupKeyword = (EditText) rootView.findViewById(R.id.group_keyword);
        prepareEditTextView("Keywords", groupKeyword);

        Button saveChangesBut = (Button) rootView.findViewById(R.id.add_new_group);
        saveChangesBut.setOnClickListener(getSaveButtonLis(_keywordGroupDao, groupName, groupKeyword));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);

        // Assign adapter to ListView
        listView.setAdapter(_groupAdapter);
        listView.setOnItemClickListener(this);
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
                isEditTextValid(groupKeyword_, s , 10, 50);
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
                isEditTextValid(groupName_, s , 10, 50);
            }
        };
    }


    private boolean isEditTextValid(EditText editTextToValidate_,  int wordCount, int maxLenght_) {
        final Editable editableText = editTextToValidate_.getEditableText();
        return isEditTextValid(editTextToValidate_, editableText, wordCount, maxLenght_);
    }

    private boolean isEditTextValid(EditText editTextToValidate_, Editable editableText_,  int wordCount, int maxLenght_) {
        boolean isValid = false;
        String inputString = editableText_.toString().toLowerCase(Locale.US);
        if(_inputValidator.compareWordCount(inputString, wordCount)){
            editTextToValidate_.setError("Must use less than " + wordCount + " keywords");
            isValid = false;
        }else if (_inputValidator.checkNullInput(inputString)) {
            editTextToValidate_.setError("Input cannot be empty");
            isValid = false;
        }else if ((inputString.length() > maxLenght_)) {
            editTextToValidate_.setError("Input is too big");
            isValid = false;
        }else {
            editTextToValidate_.setError(null);
            isValid = true;
        }
        return isValid;
    }
	


    private OnClickListener getSaveButtonLis(final IDBDao<ParcelableKeywordGroup> keywordGroupDao_,
                                             final EditText groupName_, final EditText groupKeywords_) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = groupName_.getEditableText().toString().toLowerCase(Locale.US);
                String keywords = groupKeywords_.getEditableText().toString().toLowerCase(Locale.US);

                if (isEditTextValid(groupName_, 10, 50) && isEditTextValid(groupKeywords_, 10, 50)) {

                        ParcelableKeywordGroup keywordGroup = new ParcelableKeywordGroup(name, keywords);
                        Collection<ParcelableKeywordGroup> keywordGroups =
                                keywordGroupDao_.getEntries(KeywordGroupColumn.COLUMN_GROUP_NAME.s() + " =? ", new String[]{name}, null);

                        if (keywordGroups.size() == 0) {
                            keywordGroups.add(keywordGroup);
                            keywordGroupDao_.insertOrUpdate(keywordGroups);
                            groupKeywords_.setText("");
                            groupName_.setText("");

                    }
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
	            	editText.setText("");
	            	return;
	            } 
	        }
	    }); 
	}
	

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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v(TAG, "item clicked  keywordgroup");
        sendMessageToTwitterActivity(id);

    }

    private void sendMessageToTwitterActivity(long rowID){
        ParcelableKeywordGroup group = _keywordGroupDao.getEntry(rowID).iterator().next();
        Log.v(TAG, "sending group " + group.getGroupName());
        Intent intent = new Intent(TwitterConstants.ON_BROADCAST_GROUP_SELECTED);
        intent.putExtra(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE, group);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
