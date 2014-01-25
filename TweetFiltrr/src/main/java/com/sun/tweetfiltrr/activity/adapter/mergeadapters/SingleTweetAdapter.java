package com.sun.tweetfiltrr.activity.adapter.mergeadapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;

import java.net.URISyntaxException;
import java.util.List;

public class SingleTweetAdapter extends ArrayAdapter<ParcelableUser> {

    private List<ParcelableUser> _currentUser;
    int _userTweetsListView;
    private UrlImageLoader _sicUrlImageLoader;
    private static final String TAG = SingleTweetAdapter.class.getName();
    private SmoothProgressBarWrapper _smoothProgressBarWrapper;
    IDBDao<ParcelableTimeLineEntry> _timelineDao;

    public SingleTweetAdapter(Context context,   int userTweetsListView_,
                              List<ParcelableUser> objects_,  UrlImageLoader sicUrlImageLoader_,
                              SmoothProgressBarWrapper smoothProgressBarWrapper_, IDBDao<ParcelableTimeLineEntry> timelineDao_) {
        super(context, userTweetsListView_, objects_);
        this._userTweetsListView = userTweetsListView_;
        _sicUrlImageLoader = sicUrlImageLoader_;
        _smoothProgressBarWrapper = smoothProgressBarWrapper_;
        _timelineDao = timelineDao_;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final ParcelableUser currentUser = getItem(position);


        ParcelableTimeLineEntry tweet = currentUser.getUserTimeLine().iterator().next();
        viewHolder._friendName.setText(currentUser.getUserName());
        viewHolder._tweetText.setText(tweet.getTweetDate());
        viewHolder._tweetText.setText(tweet.getTweetText());
        try {
            _sicUrlImageLoader.displayImage(currentUser.getProfileImageUrl(), viewHolder._profileImage, 1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        viewHolder._retweetBut.setOnClickListener();
        viewHolder._favouriteBut.setOnClickListener(getFavOnClick(tweet));
//        viewHolder._quoteBut.setOnClickListener();
//        viewHolder._replyBut.setOnClickListener();

        return view;
    }

    private View.OnClickListener getFavOnClick(final ParcelableTimeLineEntry tweetToFav_){
       return new View.OnClickListener() {
           @Override
           public void onClick(View v) {
//               new FavouriteTweet(_smoothProgressBarWrapper, _timelineDao)
//                       .executeOnExecutor(TwitterUtil.getInstance().getGlobalExecutor(),
//                               new ParcelableTimeLineEntry[]{tweetToFav_});
           }
       };
    }

    private View getWorkingView(final View convertView) {

        View workingView = null;

        if(null == convertView) {
            final Context context = getContext();
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            workingView = inflater.inflate(_userTweetsListView, null);
        } else {
            workingView = convertView;
        }

        return workingView;
    }

    private ViewHolder getViewHolder(final View workingView) {

        final Object tag = workingView.getTag();
        ViewHolder viewHolder = null;


        if(null == tag || !(tag instanceof ViewHolder)) {
            viewHolder = new ViewHolder();

            viewHolder._friendName = (TextView) workingView.findViewById(R.id.friend_name);
            viewHolder._tweetText = (TextView) workingView.findViewById(R.id.timeline_entry);
            viewHolder._tweetDate = (TextView) workingView.findViewById(R.id.timeline_date_time);
            viewHolder._profileImage = (ImageView) workingView.findViewById(R.id.profile_image);
            viewHolder._editTweetBox = (EditText) workingView.findViewById(R.id.tweet_edit_text);
            viewHolder._retweetBut = (ImageButton) workingView.findViewById(R.id.retweet_but);
            viewHolder._favouriteBut = (ImageButton) workingView.findViewById(R.id.favourite_but);
            viewHolder._quoteBut = (ImageButton) workingView.findViewById(R.id.copy_tweet_but);
            viewHolder._replyBut = (ImageButton) workingView.findViewById(R.id.reply_but);


            workingView.setTag(viewHolder);
            Log.v("decodePairArray", workingView.getTag().toString());
        } else {
            Log.v("decodePairArray", tag.toString());
            viewHolder = (ViewHolder) tag;
        }

        return viewHolder;
    }


    private static class ViewHolder {

        public TextView _friendName;
        public TextView _tweetText;
        public TextView _tweetDate;
        public ImageView _profileImage;
        public EditText _editTweetBox;
        public ImageButton _favouriteBut;
        public ImageButton _retweetBut;
        public ImageButton _quoteBut;
        public ImageButton _replyBut;

    }


}
