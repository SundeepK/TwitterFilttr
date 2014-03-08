package com.sun.tweetfiltrr.activity.adapter.mergeadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.multipleselector.impl.AItemSelector;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;

import java.util.Collection;
import java.util.List;

public class ConversationAdapter extends AItemSelector<ParcelableUser> {

    private int _layout;
    private ViewHolder _leftMessage;
    private ViewHolder _rightMessage;
    private ViewHolder _currentView;
    private LayoutInflater _inflater;
    private UrlImageLoader _imageLoader;
    View right;
    View left;
    public ConversationAdapter(Context context, int resource, List<ParcelableUser> objects, UrlImageLoader imageLoader_) {
        super(context, resource, objects);
        _inflater = LayoutInflater.from(context);
        _layout = resource;
        _imageLoader = imageLoader_;
    }

    @Override
    public Collection<ParcelableUser> getAllSelectedItems() {

        return null;
    }

    @Override
    public View getView(int position_, View convertView_, ViewGroup parent_) {
        ParcelableUser user = getItem(position_);
        View holder = setViewHolder(convertView_, parent_, user, position_);

        _currentView._userName.setText(user.getUserName());
        Collection<ParcelableTweet> tweets = user.getUserTimeLine();
        if (!tweets.isEmpty()) {
            _currentView._textEntry.setText(tweets.iterator().next().getTweetText());
        }
        ImageLoaderUtils.attemptLoadImage(_currentView._userProfile, _imageLoader, user.getProfileImageUrl(), 1, null);
        return holder;
    }

    private View setViewHolder(View convertView_, ViewGroup parent_, ParcelableUser user_, int position_) {
        ViewHolder holder;
        TextView userName;
        TextView tweetTextView;
        ImageView profileImage;
        if (convertView_ == null) {

             left = _inflater.inflate(R.layout.left_convo_list_view, parent_, false);
            userName = (TextView) left.findViewById(R.id.timeline_friend_name);
            tweetTextView = (TextView) left.findViewById(R.id.timeline_entry);
            profileImage = (ImageView) left.findViewById(R.id.user_profile_imageview);
            _leftMessage = new ViewHolder();
            _leftMessage._userName = userName;
            _leftMessage._userProfile = profileImage;
            _leftMessage._textEntry = tweetTextView;

             right = _inflater.inflate(R.layout.right_convo_list_view, parent_, false);

            userName = (TextView) right.findViewById(R.id.timeline_friend_name);
            tweetTextView = (TextView) right.findViewById(R.id.timeline_entry);
            profileImage = (ImageView) right.findViewById(R.id.user_profile_imageview);
            _rightMessage = new ViewHolder();
            _rightMessage._userName = userName;
            _rightMessage._userProfile = profileImage;
            _rightMessage._textEntry = tweetTextView;

            if(position_ % 2 == 0){
                convertView_ = left;
                convertView_.setTag(_leftMessage);
            }else{
                convertView_ = right;
                convertView_.setTag(_rightMessage);
            }
            _currentView  = (ViewHolder) convertView_.getTag();

        }else{
            if(position_ % 2 == 0){
//                convertView_ = left;

                convertView_.setTag(_leftMessage);
            }else{
//                convertView_ = right;

                convertView_.setTag(_rightMessage);
            }
            _currentView  = (ViewHolder) convertView_.getTag();


        }

        return convertView_;

    }


    private static class ViewHolder {
        private TextView _userName;
        private ImageView _userProfile;
        private TextView _textEntry;
    }
}
