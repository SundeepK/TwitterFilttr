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
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EditKeywordGroupAdapter extends SimpleCursorAdapter implements SectionIndexer, IUpdatedGroup {


    private static final String TAG = EditKeywordGroupAdapter.class.getName();
	private final LayoutInflater _inflater;
    private CursorToParcelable<ParcelableUser> _keywordUserToParcelable;
    private UrlImageLoader _imageLoader;
    private ParcelableKeywordGroup _group;
    private AlphabetIndexer _alphabetIndexer;
    private final Map<ParcelableUser, Boolean> _isChecked;
    public EditKeywordGroupAdapter(Context context, int layout, Cursor c,
                                   String[] from, int[] to, int flags,
                                   CursorToParcelable<ParcelableUser> keywordUserToParcelable_, UrlImageLoader imageLoader_,
                                   ParcelableKeywordGroup group_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _keywordUserToParcelable = keywordUserToParcelable_;
        _imageLoader = imageLoader_;
        _group = group_;
        _isChecked = new HashMap<ParcelableUser, Boolean>();
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        if (c != null) {
            _alphabetIndexer = new AlphabetIndexer(c,
                    c.getColumnIndex(FriendTable.FriendColumn.FRIEND_NAME.a()),
                    "ABCDEFGHIJKLMNOPQRTSUVWXYZ");
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

        isPartOfGroupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBox = (CheckBox) v;
                boolean status = checkBox.isChecked();
                _isChecked.put(user, status);
                //checkBox.setChecked(status);

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
                    _isChecked.remove(user);
                }
//                        EditKeywordGroupAdapter.this._friendDao.insertOrUpdate(users ,
//                                new String[]{FriendTable.FriendColumn.FRIEND_ID.s(), FriendTable.FriendColumn.COLUMN_GROUP_ID.s()});
            }
        });
        Boolean status  = _isChecked.get(user);
        if(status == null){
            if(_group.getGroupId() == user.getGroupId()){
                status = Boolean.TRUE;
            }else{
                status = Boolean.FALSE;
            }
        }

        if(status){
            groupName.setText(_group.getGroupName());
        }else{
            groupName.setText(usersGroup.getGroupName());
        }

        isPartOfGroupCheckBox.setChecked(status.booleanValue());
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
		return _inflater.inflate(R.layout.edit_keyword_group_list_item,parent,false);
	}

    @Override
    public Object[] getSections() {
        return _alphabetIndexer.getSections();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {

        if(!getCursor().isClosed()){
            return _alphabetIndexer.getPositionForSection(sectionIndex);
        }

        return 0;


    }

    @Override
    public int getSectionForPosition(int position) {
        return _alphabetIndexer.getSectionForPosition(position);

    }

    @Override
    public Collection<ParcelableUser> getChangedGroupIdsForUsers(long groupId_) {
        Collection<ParcelableUser> users ;

        if(groupId_ == _group.getGroupId()){
             users=  _isChecked.keySet();

            for(ParcelableUser user : users){
                Log.v(TAG, "user to new group " + user.toString());
                 user.setGroupId(_group.getGroupId());
            }

        }else{
            users = new ArrayList<ParcelableUser>();
        }
        return users;
    }
}

