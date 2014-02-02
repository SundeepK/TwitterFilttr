package com.sun.tweetfiltrr.twitter.callables;

import com.sun.tweetfiltrr.twitter.retrievers.api.ITwitterRetriever;
import com.sun.tweetfiltrr.parcelable.CachedFollowerDetails;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import java.util.Collection;
import java.util.concurrent.Callable;

public class FollowersRetriever implements Callable<Collection<ParcelableUser>> {

    private static final String TAG = FollowersRetriever.class.getName();
    private ITwitterRetriever<Collection<ParcelableUser>> _userRetriever;
    private ParcelableUser _currentUser;
    // private final int MAX_TIME_BETWEEN_FREIEND_UPDATES_MINS = 60;
    // private final int _maxTimeBetweenUpdates;

    /**
     * Retrieves the up to 100 new friends + plus the user passed in through the constructor through a {@link java.util.Collection}
     * of {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} users. This implementation will
     * attempt to pick up where it last left off for the user and will also update the user's detail so it is worth updating the database
     * with the {@link java.util.Collection} returned.
     *
     * @param currentUser_
     */
    public FollowersRetriever(ParcelableUser currentUser_, ITwitterRetriever<Collection<ParcelableUser>> userRetriever_) {
        _currentUser = currentUser_;
        _userRetriever = userRetriever_;
    }

    @Override
    public Collection<ParcelableUser> call() throws Exception {
        ICachedUser cachedDataUser = new CachedFollowerDetails(_currentUser);
         return _userRetriever.retrieveTwitterData(cachedDataUser);
    }


}
