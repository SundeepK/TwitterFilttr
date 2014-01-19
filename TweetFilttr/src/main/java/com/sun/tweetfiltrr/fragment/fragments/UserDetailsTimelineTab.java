package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class UserDetailsTimelineTab extends ATimelineFragment {


    private static final String TAG = UserDetailsTimelineTab.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, getCurrentUser());
        Fragment frag = new UserProfileFragment();
        frag.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.user_details_fragment, frag)
                .commit();
        return rootView;
    }

    @Override
    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_, ParcelableUser currentUser_) {
        return new PullToRefreshView(getActivity(), currentUser_, this, adapter_, this, this, R.layout.user_details_fragment);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Log.v(TAG, "current rowcount is " + getTimeLineCount());
        String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, TimelineDao.FULLY_QUALIFIED_PROJECTIONS);
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_TIMELINE_FRIEND, projection, FriendColumn.FRIEND_ID.a() + " = ? ",
                new String[]{getCurrentUser().getUserId() + ""}, TimelineColumn.TWEET_ID.a() + " DESC " + " LIMIT " + getTimeLineCount());

        return cursorLoader;
    }


}
