package com.sun.tweetfiltrr.activity.adapter.mergeadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.multipleselector.impl.AItemSelector;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;
import java.util.List;

public class ConversationAdapter extends AItemSelector<ParcelableUser> {

    private int _layout;
    private ViewHolder _viewHolder;
    private LayoutInflater _inflater;

    public ConversationAdapter(Context context, int resource, List<ParcelableUser> objects) {
        super(context, resource, objects);
        _inflater = LayoutInflater.from(context);
        _layout = resource;
    }

    @Override
    public Collection<ParcelableUser> getAllSelectedItems() {

        return null;
    }

    @Override
    public View getView(int position_, View convertView_, ViewGroup parent_) {
        TextView userName = null;
        TextView tweetTextView = null;
        if (convertView_ == null) {

            convertView_ = _inflater.inflate(_layout, parent_, false);

            try {

                userName = (TextView) convertView_.findViewById(R.id.timeline_friend_name);
                tweetTextView = (TextView) convertView_.findViewById(R.id.timeline_entry);
                _viewHolder = new ViewHolder();
                _viewHolder._userName = userName;
                _viewHolder._textEntry = tweetTextView;
                _viewHolder._textEntry.setId(position_);
                convertView_.setTag(_viewHolder);

            } catch (ClassCastException e) {
                throw new IllegalStateException(
                        "ConversationAdapter requires two resource IDs to be TextView and CheckBox",
                        e);
            }
        } else {
            _viewHolder = (ViewHolder) convertView_.getTag();
        }

        ParcelableUser user = getItem(position_);
        _viewHolder._userName.setText(user.getUserName());

        for (ParcelableTweet tweet : user.getUserTimeLine()) {
            _viewHolder._textEntry.setText(tweet.getTweetText());
            break;
        }


        return convertView_;
    }


    private static class ViewHolder {
        private TextView _userName;
        private TextView _textEntry;
    }


}
