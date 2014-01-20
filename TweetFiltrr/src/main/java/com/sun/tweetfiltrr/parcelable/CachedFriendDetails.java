package com.sun.tweetfiltrr.parcelable;

import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

/**
 * Created by Sundeep on 19/01/14.
 */
public class CachedFriendDetails implements ICachedUser {

    private ParcelableUser _user;
    public CachedFriendDetails(ParcelableUser user_){
        _user = user_;
    }


    @Override
    public int getTotalCount() {
        return _user.getTotalFriendCount();
    }

    @Override
    public void setTotalCount(int totalCount_) {
        _user.setTotalFollowerCount(totalCount_);
    }

    @Override
    public long getLastPageNumber() {
        return _user.getLastFriendPageNumber();
    }

    @Override
    public void setLastPageNumber(long pageNumber_) {
        _user.setLastFriendPageNumber(pageNumber_);
    }

    @Override
    public int getCurrentUserCount() {
       return _user.getCurrentFriendCount();
    }

    @Override
    public int getLastArrayIndex() {
        return _user.getLastFriendIndex();
    }

    @Override
    public void setCurrentCount(int count_) {
        _user.setCurrentFriendTotal(count_);
    }

    @Override
    public void setLastArrayIndex(int index_) {
        _user.setLastFriendIndex(index_);
    }

    @Override
    public long[] getUserIds() {
        return _user.getFriendIDs();
    }

    @Override
    public void setUserIds(long[] userIds_) {
        _user.setFriendIDs(userIds_);
    }

    @Override
    public ParcelableUser getUser() {
        return _user;
    }

    @Override
    public long getMaxId() {
        return _user.getMaxId();
    }

    @Override
    public void setMaxId(long id_) {
        _user.setMaxId(id_);
    }

    @Override
    public long getSinceId() {
        return _user.getSinceId();
    }

    @Override
    public void setSinceId(long id_) {
        _user.setSinceId(id_);
    }
}
