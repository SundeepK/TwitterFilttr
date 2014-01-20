package com.sun.tweetfiltrr.cursorToParcelable;

import android.database.Cursor;

import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;


/**
 * Created by Sundeep on 23/12/13.
 */
public class FriendTimeLineToParcelable implements CursorToParcelable<ParcelableUser> {

    private FriendToParcelable _friendToParcelable;
    private TimelineToParcelable _timelineToParcelable;
    public FriendTimeLineToParcelable(FriendToParcelable friendToParcelable_, TimelineToParcelable timelineToParcelable_){

        _friendToParcelable = friendToParcelable_;
        _timelineToParcelable = timelineToParcelable_;
    }

    @Override
    public ParcelableUser getParcelable(Cursor cursor_) {
        ParcelableUser user = _friendToParcelable.getParcelable(cursor_);
        ParcelableTimeLineEntry timeline = _timelineToParcelable.getParcelable(cursor_);
        user.addTimeLineEntry(timeline);
        return user;
    }
}
