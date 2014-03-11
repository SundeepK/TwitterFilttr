package com.sun.tweetfiltrr.parcelable;

/**
 * Created by Sundeep.Kahlon on 11/03/14.
 *
 */
public class CachedKeywordFriendDetails extends CachedFriendDetails{
    private ParcelableUser _user;

    public CachedKeywordFriendDetails(ParcelableUser user_) {
        super(user_);
        _user =user_;
    }

    @Override
    public long getMaxId() {
        return _user.getMaxIdForMentions();
    }

    @Override
    public void setMaxId(long id_) {
        _user.setMaxIdForMentions(id_);
    }

    @Override
    public long getSinceId() {
        return _user.getSinceIdForMentions();
    }

    @Override
    public void setSinceId(long id_) {
        _user.setSinceIdForMentions(id_);
    }
}
