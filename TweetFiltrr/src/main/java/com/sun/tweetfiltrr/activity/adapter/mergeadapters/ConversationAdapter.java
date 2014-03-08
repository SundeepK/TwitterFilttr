package com.sun.tweetfiltrr.activity.adapter.mergeadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;

import java.util.Collection;
import java.util.List;

public class ConversationAdapter extends ArrayAdapter<ParcelableUser>  {

    private static final int TYPE_MAX_COUNT = 2;
    private static final int RIGHT_MSG = 1;
    private static final int LEFT_MSG = 0;
    private ParcelableUser _currentUser;
    private LayoutInflater _inflater;
    private UrlImageLoader _imageLoader;

    public ConversationAdapter(Context context,  List<ParcelableUser> objects,
                               UrlImageLoader imageLoader_, ParcelableUser user_) {
        super(context, 0, objects);
        _inflater = LayoutInflater.from(context);
        _imageLoader = imageLoader_;
        _currentUser = user_;
    }


    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
       return position % 2 == 0 ? LEFT_MSG : RIGHT_MSG;
    }

    @Override
    public View getView(int position_, View convertView_, ViewGroup parent_) {

        ViewHolder holder = null;
        int type = getItemViewType(position_);
        if (convertView_ == null) {
            holder = new ViewHolder();
            switch (type) {
                case LEFT_MSG:
                    convertView_ = _inflater.inflate(R.layout.left_convo_list_view, parent_, false);
                    holder._userName = (TextView) convertView_.findViewById(R.id.timeline_friend_name);
                    holder._userProfile  = (ImageView) convertView_.findViewById(R.id.user_profile_imageview);
                    holder._textEntry = (TextView) convertView_.findViewById(R.id.timeline_entry);
                    break;
                case RIGHT_MSG:
                    convertView_ = _inflater.inflate(R.layout.right_convo_list_view, parent_, false);
                    holder._userName = (TextView) convertView_.findViewById(R.id.timeline_friend_name);
                    holder._userProfile  = (ImageView) convertView_.findViewById(R.id.user_profile_imageview);
                    holder._textEntry = (TextView) convertView_.findViewById(R.id.timeline_entry);
                    break;
            }
            convertView_.setTag(holder);
        } else {
            holder = (ViewHolder)convertView_.getTag();
        }

       ParcelableUser user = getItem(position_);
        holder._userName.setText(user.getUserName());
        Collection<ParcelableTweet> tweets = user.getUserTimeLine();
        if (!tweets.isEmpty()) {
            holder._textEntry.setText(tweets.iterator().next().getTweetText());
        }
        ImageLoaderUtils.attemptLoadImage(holder._userProfile, _imageLoader, user.getProfileImageUrl(), 1, null);
        return convertView_;
    }


    private static class ViewHolder {
        private TextView _userName;
        private ImageView _userProfile;
        private TextView _textEntry;
    }
}
