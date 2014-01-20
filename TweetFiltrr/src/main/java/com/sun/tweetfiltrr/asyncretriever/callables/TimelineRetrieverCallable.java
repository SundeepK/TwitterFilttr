package com.sun.tweetfiltrr.asyncretriever.callables;

import com.sun.tweetfiltrr.asyncretriever.api.ITwitterRetriever;
import com.sun.tweetfiltrr.parcelable.CachedFriendDetails;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import java.util.concurrent.Callable;

public class TimelineRetrieverCallable implements Callable<ParcelableUser> {

    private static final String TAG = TimelineRetrieverCallable.class.getName();
    private ITwitterRetriever<ParcelableUser> _userRetriever;
    private ParcelableUser _currentUser;


    /**
     * Retrieves the up to 100 new friends + plus the user passed in through the constructor through a {@link java.util.Collection}
     * of {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} users. This implementation will
     * attempt to pick up where it last left off for the user and will also update the user's detail so it is worth updating the database
     * with the {@link java.util.Collection} returned.
     *
     * @param currentUser_
     */
    public TimelineRetrieverCallable(ParcelableUser currentUser_, ITwitterRetriever<ParcelableUser> userRetriever_) {
        _currentUser = currentUser_;
        _userRetriever = userRetriever_;
    }

    @Override
    public ParcelableUser call() throws Exception {
        ICachedUser cachedDataUser = new CachedFriendDetails(_currentUser);
         return _userRetriever.retrieveTwitterData(cachedDataUser);
    }


}
