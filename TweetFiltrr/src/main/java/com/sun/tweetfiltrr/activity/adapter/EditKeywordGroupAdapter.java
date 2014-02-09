package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.cursorToParcelable.CursorToParcelable;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;

public class EditKeywordGroupAdapter extends SimpleCursorAdapter implements SectionIndexer, IUpdatedGroup {


    private static final String TAG = EditKeywordGroupAdapter.class.getName();
	private final LayoutInflater _inflater;
    private CursorToParcelable<ParcelableUser> _keywordUserToParcelable;
    private UrlImageLoader _imageLoader;
    private SparseBooleanArray _listItemStatus;
    private ParcelableKeywordGroup _group;
    private IDBDao<ParcelableUser> _friendDao;
    private AlphabetIndexer _alphabetIndexer;
    private Map<Long, Boolean> _isChecked;
    public EditKeywordGroupAdapter(Context context, int layout, Cursor c,
                                   String[] from, int[] to, int flags,
                                   CursorToParcelable<ParcelableUser> keywordUserToParcelable_, UrlImageLoader imageLoader_,
                                   ParcelableKeywordGroup group_, IDBDao<ParcelableUser> friendDao_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _keywordUserToParcelable = keywordUserToParcelable_;
        _imageLoader = imageLoader_;
        _listItemStatus = new SparseBooleanArray();
        _group = group_;
        _friendDao = friendDao_;
        _isChecked = new HashMap<Long, Boolean>();
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        if(_alphabetIndexer == null && c != null){
            _alphabetIndexer = new AlphabetIndexer(c,
                    c.getColumnIndex(FriendColumn.FRIEND_NAME.a()),
                    " ABCDEFGHIJKLMNOPQRTSUVWXYZ");
        }
        return super.swapCursor(c);
    }

    @Override
	public void bindView(View view_, Context context, Cursor cursor_) {

        final ParcelableUser user = getUser(cursor_);
        ImageView profileImage = (ImageView)view_.findViewById(R.id.profile_image);
		TextView userName =(TextView)view_.findViewById(R.id.user_name);
        final TextView groupName =(TextView)view_.findViewById(R.id.group_name);

        ImageLoaderUtils.attemptLoadImage(profileImage,_imageLoader , user.getProfileImageUrl(),1, null);
		userName.setText(user.getUserName());
        CheckBox isPartOfGroupCheckBox = (CheckBox) view_.findViewById(R.id.is_part_of_group_checkbox);
        ParcelableKeywordGroup usersGroup = user.getKeywordGroup();
        groupName.setText(usersGroup.getGroupName());

        isPartOfGroupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBox = (CheckBox) v;
                boolean status = checkBox.isChecked();
                _isChecked.put(user.getUserId(), status);
                checkBox.setChecked(status);

                Log.v(TAG, "user to update is " + user.getScreenName());
                Log.v(TAG, "setting group update to  " + _group.getGroupName());

                if(status){
                    groupName.setText(_group.getGroupName());
                }else{

                    if(_group.getGroupId() == user.getGroupId()){
                        groupName.setText("No Group");

                    }else{
                        groupName.setText(user.getKeywordGroup().getGroupName());
                    }
                    _isChecked.remove(user.getUserId());
                }
//                        EditKeywordGroupAdapter.this._friendDao.insertOrUpdate(users ,
//                                new String[]{FriendColumn.FRIEND_ID.s(), FriendColumn.COLUMN_GROUP_ID.s()});
            }
        });
        Boolean status  = _isChecked.get(user.getUserId());
        if(status == null){
            if(_group.getGroupId() == user.getGroupId()){
                status = true;
            }else{
                status = false;
            }
        }
        isPartOfGroupCheckBox.setChecked(status);

//        if(usersGroup != null){
//            Log.v(TAG, "user group not null " +  usersGroup.getGroupName());
//
//            if(_group != null){
//                Log.v(TAG, "passed in group name " +  _group.getGroupName());
//
//                if(usersGroup.getGroupId() == _group.getGroupId()){
//                isPartOfGroupCheckBox.setChecked(true);
//            }
//            }
//        }

	}


    private void registerCheckBoxListener(final CheckBox checkBox_,
                                          final int position_, final SparseBooleanArray sparseBool_) {

        checkBox_.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CheckBox checkBox = (CheckBox) v;
                boolean status = checkBox.isChecked();
                sparseBool_.put(position_, status);
                checkBox.setChecked(sparseBool_.get(position_, false));

            }
        });

    }
	
    private ParcelableUser getUser(Cursor cursor_){
        return _keywordUserToParcelable.getParcelable(cursor_);
    }

	@Override
	public View newView(Context context, Cursor  cursor, ViewGroup parent) {
		final View view=_inflater.inflate(R.layout.edit_keyword_group_list_item,parent,false);
        return view;
	}

    @Override
    public Object[] getSections() {
        return _alphabetIndexer.getSections();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return _alphabetIndexer.getPositionForSection(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return _alphabetIndexer.getSectionForPosition(position);
    }

    @Override
    public Set<Long> getChangedUserIdsForGroup(long groupId_) {
        if(groupId_ == _group.getGroupId()){
            return _isChecked.keySet();
        }
        return new HashSet<Long>();
    }
}

