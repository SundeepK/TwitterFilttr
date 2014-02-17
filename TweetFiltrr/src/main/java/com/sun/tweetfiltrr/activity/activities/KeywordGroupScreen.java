package com.sun.tweetfiltrr.activity.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.KeywordGroupAdapter;
import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.KeywordGroupDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.fragment.fragments.EditKeywordGroupTab;
import com.sun.tweetfiltrr.multipleselector.api.OnTextViewLoad;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.callables.BulkFriendRetriever;
import com.sun.tweetfiltrr.utils.InputValidator;
import com.sun.tweetfiltrr.utils.TwitterConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Executors;

import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;


public class KeywordGroupScreen extends SherlockFragmentActivity implements
						LoaderManager.LoaderCallbacks<Cursor>, OnTextViewLoad<ParcelableUser>, AdapterView.OnItemClickListener {
	
	private CursorAdapter _groupAdapter;
	private static final int TUTORIAL_LIST_LOADER = 0x02;
	private static final String TAG = KeywordGroupScreen.class.getName();
	private IDBDao<ParcelableUser> _friendDao;
	private IDBDao<ParcelableKeywordGroup> _keywordGroupDao;
	private InputValidator _inputValidator;
    private Collection<IDatabaseUpdater> _dbUpdaters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_listview);


        ActionBar actionBar =  getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_title, null);
        TextView title = (TextView) v.findViewById(R.id.action_bar_title);
        title.setText("Keyword filters");
        actionBar.setCustomView(v);

        Activity activity = this;
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
        _groupAdapter = new KeywordGroupAdapter(this, R.layout.listview_for_twitter,
                null, columns, to, 0, _keywordGroupDao);

        EditText groupName = (EditText) findViewById(R.id.group_name);
        groupName.addTextChangedListener(getGroupNameTextWatcher(groupName));
        prepareEditTextView("Group name", groupName);

        EditText groupKeyword = (EditText) findViewById(R.id.group_keyword);
        prepareEditTextView("Keywords", groupKeyword);

        Button saveChangesBut = (Button) findViewById(R.id.add_new_group);
        saveChangesBut.setOnClickListener(getSaveButtonLis(_keywordGroupDao, groupName, groupKeyword));

        ListView listView = (ListView) findViewById(android.R.id.list);

        // Assign adapter to ListView
        listView.setAdapter(_groupAdapter);
        listView.setOnItemClickListener(this);
        this.getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);



        DaoFlyWeightFactory flyWeight = DaoFlyWeightFactory.getInstance(getContentResolver());
        String[] cols = new String[]{FriendTable.FriendColumn.FRIEND_ID.s(), FriendTable.FriendColumn.FRIEND_NAME.s(), FriendTable.FriendColumn.FRIEND_SCREENNAME.s(),
                FriendTable.FriendColumn.FOLLOWER_COUNT.s(),
                FriendTable.FriendColumn.FRIEND_COUNT.s(), FriendTable.FriendColumn.COLUMN_LAST_FRIEND_INDEX.s(),
                FriendTable.FriendColumn.COLUMN_CURRENT_FRIEND_COUNT.s(), FriendTable.FriendColumn.LAST_FRIEND_PAGE_NO.s(),
                FriendTable.FriendColumn.IS_FRIEND.s(), FriendTable.FriendColumn.PROFILE_IMAGE_URL.s(), FriendTable.FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(),
                FriendTable.FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.s(),
                FriendTable.FriendColumn.DESCRIPTION.s()};
        //TODO this is for testing purposes and needs to be changed to current user instead
        Collection<ParcelableUser> users =   (_friendDao.getEntries(FriendTable.FriendColumn.FRIEND_ID.s()
                + " = ? ", new String[]{Long.toString(15670515l)}, null));
        final ParcelableUser user = users.iterator().next();
        final IDBDao<ParcelableUser> _usersToFriendDao=   (IDBDao<ParcelableUser>)
                flyWeight.getDao(DaoFlyWeightFactory.DaoFactory.USERS_FRIEND_DAO, user);
        final Collection<IDatabaseUpdater> _dbUpdaters = new ArrayList<IDatabaseUpdater>();
        _dbUpdaters.add(new DatabaseUpdater(_friendDao, cols));
        _dbUpdaters.add(new DatabaseUpdater(_usersToFriendDao));



        Button loadFriends = (Button) findViewById(R.id.load_all_friends);
        saveChangesBut.setOnClickListener(getSaveButtonLis(_keywordGroupDao, groupName, groupKeyword));

        loadFriends.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onclicked for load friends");
                BulkFriendRetriever r = new BulkFriendRetriever(user,_dbUpdaters);
                Executors.newFixedThreadPool(1).submit(r);

            }
        });


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

			  CursorLoader cursorLoader = new CursorLoader(this,
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

        ParcelableKeywordGroup group = _keywordGroupDao.getEntry(id).iterator().next();
        Log.v(TAG, "sending group " + group.getGroupName());
        Intent intent = new Intent(this, EditKeywordGroupTab.class);
        intent.putExtra(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE, group);
        this.startActivity(intent);
    }

    private void sendMessageToTwitterActivity(long rowID){

    }
}
