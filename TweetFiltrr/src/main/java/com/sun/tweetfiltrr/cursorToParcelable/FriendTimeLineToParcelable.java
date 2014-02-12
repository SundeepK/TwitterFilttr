package com.sun.tweetfiltrr.cursorToParcelable;

import android.database.Cursor;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by Sundeep on 23/12/13.
 */
@Singleton
public class FriendTimeLineToParcelable implements CursorToParcelable<ParcelableUser> {

    private FriendToParcelable _friendToParcelable;
    private TimelineToParcelable _timelineToParcelable;

    @Inject
    public FriendTimeLineToParcelable(FriendToParcelable friendToParcelable_, TimelineToParcelable timelineToParcelable_){

        _friendToParcelable = friendToParcelable_;
        _timelineToParcelable = timelineToParcelable_;
    }

    @Override
    public ParcelableUser getParcelable(Cursor cursor_) {
        ParcelableUser user = _friendToParcelable.getParcelable(cursor_);
        ParcelableTweet timeline = _timelineToParcelable.getParcelable(cursor_);
        user.addTimeLineEntry(timeline);
        return user;
    }
}
