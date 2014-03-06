package com.sun.tweetfiltrr.activity.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.KeywordGroupAdapter;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.KeywordGroupDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.multipleselector.api.OnTextViewLoad;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.validator.InputValidator;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.Collection;
import java.util.Locale;

import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;


public class KeywordGroupActivity extends SherlockFragmentActivity implements
						LoaderManager.LoaderCallbacks<Cursor>, OnTextViewLoad<ParcelableUser>, AdapterView.OnItemClickListener {
	
	private CursorAdapter _groupAdapter;
	private static final int TUTORIAL_LIST_LOADER = 0x02;
	private static final String TAG = KeywordGroupActivity.class.getName();
	private IDBDao<ParcelableKeywordGroup> _keywordGroupDao;
	private InputValidator _inputValidator;
    private ParcelableUser _currentUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_listview);
        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);

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
        groupName.addTextChangedListener(_inputValidator.getEditTextWatcher(groupName, 10, 50));
        _inputValidator.prepareEditTextView("Group name", groupName);

        EditText groupKeyword = (EditText) findViewById(R.id.group_keyword);
        _inputValidator.prepareEditTextView("Keywords", groupKeyword);

        Button saveChangesBut = (Button) findViewById(R.id.add_new_group);
        saveChangesBut.setOnClickListener(getSaveButtonLis(_keywordGroupDao, groupName, groupKeyword));
        saveChangesBut.setOnClickListener(getSaveButtonLis(_keywordGroupDao, groupName, groupKeyword));

        ListView listView = (ListView) findViewById(android.R.id.list);

        // Assign adapter to ListView
        listView.setAdapter(_groupAdapter);
        listView.setOnItemClickListener(this);
        this.getSupportLoaderManager().initLoader(TUTORIAL_LIST_LOADER, null, this);
    }



    private OnClickListener getSaveButtonLis(final IDBDao<ParcelableKeywordGroup> keywordGroupDao_,
                                             final EditText groupName_, final EditText groupKeywords_) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (_inputValidator.isEditTextValid(groupName_, 10, 50) && _inputValidator.isEditTextValid(groupKeywords_, 10, 50)) {
                    Editable name = groupName_.getEditableText();
                    Editable keywords = groupKeywords_.getEditableText();
                    if(name != null && keywords != null){
                       String groupName = name.toString().toLowerCase(Locale.US);
                       String groupKeywords = keywords.toString().toLowerCase(Locale.US);
                       //check DB for duplicate group name
                       ParcelableKeywordGroup keywordGroup = new ParcelableKeywordGroup(groupName, groupKeywords);
                       Collection<ParcelableKeywordGroup> keywordGroups =
                                keywordGroupDao_.getEntries(KeywordGroupColumn.COLUMN_GROUP_NAME.s()
                                        + " =? ", new String[]{groupName}, null);
                        if (keywordGroups.size() == 0) {
                            keywordGroups.add(keywordGroup);
                            keywordGroupDao_.insertOrUpdate(keywordGroups);
                            groupKeywords_.setText("");
                            groupName_.setText("");
                    }

                    }
                }
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
        Intent intent = new Intent(this, EditKeywordGroupActivity.class);
        intent.putExtra(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE, group);
        this.startActivity(intent);
    }

}
