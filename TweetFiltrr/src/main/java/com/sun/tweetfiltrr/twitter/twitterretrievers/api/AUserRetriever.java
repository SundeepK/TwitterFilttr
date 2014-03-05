package com.sun.tweetfiltrr.twitter.twitterretrievers.api;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public abstract class AUserRetriever implements ITwitterAPICall<Collection<ParcelableUser>> {

    private static final String TAG = AUserRetriever.class.getName();

    public AUserRetriever() {
    }


    @Override
    public Collection<ParcelableUser> retrieveTwitterData(ICachedUser user_,ITwitterAPICallStatus failLis_ ) {
        Collection<ParcelableUser> friends = new ArrayList<ParcelableUser>();
        if (shouldSearchForUsers(user_)) {
            try {
                friends = loadFriends(user_);
            } catch (TwitterException e) {
                Log.v(TAG, "Twitterexeception occured while resreshing friends");
                e.printStackTrace();
                failLis_.onTwitterApiCallFail(user_.getUser(), e, this);
            }
            updateUserDetails(user_, friends);
            failLis_.onTwitterApiCallSuccess(user_.getUser(), this);
        }
        return friends;
    }

    private void updateUserDetails(ICachedUser user_, Collection<ParcelableUser> friends_) {
        int currentFriendCount = user_.getCurrentUserCount() + friends_.size();
        user_.setCurrentCount(currentFriendCount);

    }

    private boolean shouldSearchForUsers(ICachedUser user_) {
        if(user_.getCurrentUserCount() < user_.getTotalCount()){
            return true;
        }
        Log.v(TAG, "Not searching for friend for user :" + user_.getUser().toString());
        return false;
    }


    private void loadFriendIds(ICachedUser user_, Twitter twitter_) throws TwitterException {
        IDs ids = getUsersIds(user_, twitter_);
        long[] usrIds = ids.getIDs();
        Log.v(TAG, "loadFriendIds ids are :" +Arrays.toString(usrIds));
        user_.setUserIds(usrIds);
        user_.setLastPageNumber(ids.getNextCursor());
        int currentIndex = user_.getLastArrayIndex();
        if(currentIndex > 5000){ //TODO this number needs to be fixed and not be 5000
            user_.setLastArrayIndex((currentIndex % 5000));
        }

        Log.v(TAG, "load friend ids called");
    }

    private Collection<ParcelableUser> loadFriends(ICachedUser user_) throws TwitterException {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        long[] userIds = user_.getUserIds();
        if (userIds != null) {
            if (userIds.length > 0) {
                Log.v(TAG, "Not searching for friend ids user :" + user_.getUser().toString());
                Log.v(TAG, "ids :" +Arrays.toString(userIds));
            }
        }else{
            loadFriendIds(user_, twitter);
            Log.v(TAG, "Attempting to search friends ids for user :" + user_.getUser().toString());
        }
        Log.v(TAG, "lastindex for :" + user_.getUser().getScreenName() + " is "+ user_.getUser().getLastFriendIndex());
        long ids[] = getFriendsIdsToQuery(user_, twitter);
        Log.v(TAG, "friend ids to query for :" + Arrays.toString(ids));
        ResponseList<User> friends = twitter.lookupUsers(ids);
        Iterator<User> friendsIter = friends.iterator();
        return extractFriends(user_, friendsIter);
    }

    /**
     * Attempt to get the friendID's that we need to search for. This method will simply copy upto 100 friendID's
     * since it's the max twitter can handle for one query, but will update the current last index and associate
     * it with the user so we can pick up where we left off.
     *
     * @param user_
     *          {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} that we want to search friends for
     * @param twitter_
     *          {@link twitter4j.Twitter} used to make queries to
     * @return
     */
    private long[] getFriendsIdsToQuery(ICachedUser user_, Twitter twitter_) {
        long[] friendIds = user_.getUserIds();
        int lastArrayIndex = user_.getLastArrayIndex();
        int friendCount = 0;
        //minus 1 from the lastOffsetPos becuase were dealing with arrays here
        int lastOffsetPos = lastArrayIndex < 1 ? 0 : lastArrayIndex -1;
        //we can just return here rather than copy array since twitter can handle only 100 id's
        if(lastOffsetPos ==0 && friendIds.length < 100){
            return friendIds;
        }
        if (friendIds != null) {
            friendCount = friendIds.length;
        }else{
            friendCount = 100;
        }

        Log.v(TAG, "user count: " + friendCount );


        int friendDiff = friendCount - user_.getCurrentUserCount(); //TODO this is incorrect since we are reuting one more than we should maybe it includes current user plus followers, so check DB to confirm

        Log.v(TAG, "user difference: " + friendDiff );
        Log.v(TAG, "current user count: " + user_.getCurrentUserCount() );


        int lenght = friendDiff  < 100 ? friendDiff : 100;

        lenght = lenght <= 0 ? friendCount : lenght; //TODO CHECK THIS also
//        if( lenght + lastOffsetPos > friendCount){
//            lenght = friendCount -lastOffsetPos;
//        }
        long[] ids = new long[lenght];
        System.arraycopy(friendIds, lastOffsetPos , ids, 0, lenght);
        user_.setLastArrayIndex((lastOffsetPos + lenght));
        return ids;
    }


    /**
     *
     * Extract {@link twitter4j.User} objects and create {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} from them.
     *
     *
     * @param friendsIter_
     *          {@link java.util.Iterator} that is associated with the newly queried friends
     * @return
     *        {@link java.util.Collection} of {@link com.sun.tweetfiltrr.parcelable.ParcelableUser}
     */
    private Collection<ParcelableUser> extractFriends(ICachedUser user_,Iterator<User> friendsIter_) {
        Collection<ParcelableUser> friends = new ArrayList<ParcelableUser>();
        while (friendsIter_.hasNext()) {
            ParcelableUser friend = new ParcelableUser(friendsIter_.next());
        //    Log.v(TAG, "freidn retirever" + friend.toString());
            processFriend(friend);
            friends.add(friend);
        }
      //  Log.v(TAG , "Adding friend: " + user_.getUser().toString());
        friends.add(user_.getUser());
        Log.v(TAG , "final size of collection: " + friends.size());

        return friends;
    }

    protected abstract void processFriend(ParcelableUser user_);


    protected abstract IDs getUsersIds(ICachedUser user_, Twitter twitter_) throws TwitterException;

    @Override
    public TwitterAPICallType getTweetOperationType() {
        return TwitterAPICallType.GET_FRIENDS;
    }
}
