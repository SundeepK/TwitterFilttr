package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.cursorToParcelable.CursorToParcelable;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;

public class EditKeywordGroupAdapter extends SimpleCursorAdapter  {


    private static final String TAG = EditKeywordGroupAdapter.class.getName();
	private final LayoutInflater _inflater;
    private CursorToParcelable<ParcelableUser> _keywordUserToParcelable;
    private UrlImageLoader _imageLoader;
    private SparseBooleanArray _listItemStatus;
    private ParcelableKeywordGroup _group;

    public EditKeywordGroupAdapter(Context context, int layout, Cursor c,
                                   String[] from, int[] to, int flags,
                                   CursorToParcelable<ParcelableUser> keywordUserToParcelable_, UrlImageLoader imageLoader_,
                                   ParcelableKeywordGroup group_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _keywordUserToParcelable = keywordUserToParcelable_;
        _imageLoader = imageLoader_;
        _listItemStatus = new SparseBooleanArray();
        _group = group_;
    }


	@Override
	public void bindView(View view_, Context context, Cursor cursor_) {

        ParcelableUser user = getUser(cursor_);
        ImageView profileImage = (ImageView)view_.findViewById(R.id.profile_image);
		TextView userName =(TextView)view_.findViewById(R.id.user_name);
        ImageLoaderUtils.attemptLoadImage(profileImage,_imageLoader , user.getProfileImageUrl(),2, null);
		userName.setText(user.getUserName());
        CheckBox isPartOfGroupCheckBox = (CheckBox) view_.findViewById(R.id.is_part_of_group_checkbox);
        ParcelableKeywordGroup usersGroup = user.getKeywordGroup();

        if(usersGroup != null){
            if(usersGroup.getGroupId() == _group.getGroupId()){
                isPartOfGroupCheckBox.setChecked(true);
            }
        }

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

}

