package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.utils.DBUtils;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class UserDetailsTimelineTab extends ATimelineFragment {


    private static final String TAG = UserDetailsTimelineTab.class.getName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, getCurrentUser());
//        Log.v(TAG, "current user is " + getCurrentUser().getScreenName());
//
//        Fragment frag = new UserProfileFragment();
//        frag.setArguments(bundle);
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.user_details_fragment, frag, getCurrentUser().getScreenName())
//        .commit();
        return rootView;
    }

//    @Override
//    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_, ParcelableUser currentUser_,
//                                                     ZoomListView.OnItemFocused listener_, Collection<IDatabaseUpdater> updaters_) {
//        return  new PullToRefreshView.Builder<Collection<ParcelableUser>>(getActivity(), currentUser_)
//                .setCursorAadapter(adapter_)
//                .setOnItemFocusedListener(listener_)
//                .setDBUpdaters(updaters_)
//                .setOnScrollListener(TwitterUtil.getInstance().getGlobalImageLoader(getActivity()))
//                .setHeaderLayout( R.layout.user_details_fragment)
//                .setEmptyLayout(0)
//                .setOnRefreshListener(this)
//                .setLoadMoreListener(this)
//                .setOnItemClick(this)
//                .build();
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Log.v(TAG, "current rowcount is " + getTimeLineCount());
        String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, TimelineDao.FULLY_QUALIFIED_PROJECTIONS);
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_TIMELINE_FRIEND, projection, FriendColumn.FRIEND_ID.a() + " = ? ",
                new String[]{getCurrentUser().getUserId() + ""}, TimelineColumn.TWEET_ID.a() + " DESC " + " LIMIT " + getTimeLineCount());
        return cursorLoader;
    }

    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getUsersRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Log.v(TAG, "User passed for callable is: " + getCurrentUser().toString());
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        callables.add(getTweetRetriver().getTimeLineRetriever(getCurrentUser(), shouldRunOnce_, shouldLookForOldTweets, this));
        return callables;
    }

    @Override
    protected int getLoaderID() {
        return 0x53;
    }



}
