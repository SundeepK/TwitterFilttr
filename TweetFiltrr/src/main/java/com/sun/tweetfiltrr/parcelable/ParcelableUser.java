package com.sun.tweetfiltrr.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import twitter4j.User;

public class ParcelableUser implements IParcelableTwitter {

    private static final String TAG = ParcelableUser.class.getName();
    private long _userId;
    private long _createdAt;
    private boolean _isFriend;
    private boolean _isProtected;
    private String _description, _screenName, _location, _lastUpadateDate;
    private String _name;
    private String _profileBackgroundImageUrl;
    private String _profileBannerImageUrl;
    private String _profileImageUrl;
    private long _maxId;
    private long _sinceId;
    private long _maxIdForMentions;
    private long _sinceIdForMentions;
    private long _lastFriendPageNumber;
    private int _lastTimelinePageNumber;
    private List<ParcelableTweet> _timeline;
    private long _groupID;
    private int _totalFriendCount;
    private int _newTweetCount;
    private long[] _friendIDs;
    private long[] _followerIDs;
    private int _lastFriendIndex;
    private long _lastFriendSyncTime;
    private int _currentFriendTotal;
    private int _totalTweetCount;
    private int _totalFollowerCount;
    private int _lastFollowerIndex;
    private int _currentFollowerCount;
    private long _lastFollowerPageNumber;
    private long _keywordMaxID;
    private long _keywordSinceID;
    private ParcelableKeywordGroup _keywordGroup;


    public ParcelableUser(){}

    public ParcelableUser(long userId_, String name_, String screenName_) {
		_userId = userId_;
		_name = name_;
		_screenName = screenName_;
		_timeline =new ArrayList<ParcelableTweet>();

	}

	public ParcelableUser(long userId_, String name_, long sinceId_, long maxId_) {
		_userId = userId_;
		_name = name_;
		_sinceId = sinceId_;
		_maxId = maxId_;
		_timeline =new ArrayList<ParcelableTweet>();
	}

	public ParcelableUser(User user_) {
		_userId = user_.getId();
		_createdAt = getTime(user_.getCreatedAt());
		_isProtected = user_.isProtected();
		_name = user_.getName();
		_screenName = user_.getScreenName();
		_description = user_.getDescription();
		_location = user_.getLocation();
		_profileImageUrl = user_.getBiggerProfileImageURL();
		_profileBackgroundImageUrl = user_.getProfileBackgroundImageURL();
		_profileBannerImageUrl = user_.getProfileBannerURL();
		_timeline =new ArrayList<ParcelableTweet>();
		_sinceId = 1l;
		_maxId = 1l;
		_totalFriendCount =	user_.getFriendsCount();
		_lastFriendPageNumber = -1l;
        _maxIdForMentions = 1l;
        _sinceIdForMentions = 1l;
        _newTweetCount = 0;
        _totalTweetCount = user_.getStatusesCount();
       _totalFollowerCount = user_.getFollowersCount();
        _lastFollowerIndex = 0;
       _currentFollowerCount = 0;
        _lastFollowerPageNumber = 0;


	}

    /**
     * This copy constructor DOES NOT copy the user's timeline {@link ParcelableTweet}, or cached friend/follower ID's
     * you can set it explicitly through its setter.
     *
     * @param user_
     */
    public ParcelableUser(ParcelableUser user_) {
        copyCachedData(user_);
    }

    public void copyCachedData(ParcelableUser user_){
        _userId = user_.getUserId();
        _createdAt = user_.getCreatedAt();
        _isProtected = user_.isProtected();
        _name = user_.getUserName();
        _screenName = user_.getScreenName();
        _description = user_.getDescription();
        _location = user_.getLocation();
        _profileImageUrl = user_.getProfileImageUrl();
        _profileBackgroundImageUrl = user_.getProfileBackgroundImageUrl();
        _profileBannerImageUrl = user_.getProfileBannerImageUrl();
        _timeline =new ArrayList<ParcelableTweet>();
        _sinceId = user_.getSinceId();
        _maxId = user_.getMaxId();
        _totalFriendCount =	user_.getTotalFriendCount();
        _lastFriendPageNumber = user_.getLastFriendPageNumber();
        _newTweetCount = user_.getNewTweetCount();
        _lastFriendIndex = user_.getLastFriendIndex();
        _lastFriendSyncTime =   user_.getLastFriendSyncTime();
        _maxIdForMentions = user_.getMaxIdForMentions();
        _sinceIdForMentions = user_.getSinceIdForMentions();
        _totalTweetCount = user_.getTotalTweetCount();
        _totalFollowerCount = user_.getTotalFollowerCount();
        _lastFollowerIndex =   user_.getLastFollowerIndex();
        _currentFollowerCount = user_.getCurrentFollowerCount();
        _lastFollowerPageNumber = user_.getLastFollowerPageNumber();
        _keywordMaxID = user_.getKeywordMaxID();
        _keywordSinceID = user_.getKeywordSinceID();
    }

    @Override
	public void writeToParcel(Parcel outParcel_, int flags) {
		outParcel_.writeLong(_userId);
		outParcel_.writeLong(_createdAt);
		outParcel_.writeInt(_isProtected ? 1 : 0);
		outParcel_.writeString(_description);
		outParcel_.writeString(_name);
		outParcel_.writeString(_screenName);
		outParcel_.writeString(_location);
		outParcel_.writeString(_profileImageUrl);
		outParcel_.writeString(_profileBackgroundImageUrl);
		outParcel_.writeString(_profileBannerImageUrl);
		outParcel_.writeLong(_maxId);
		outParcel_.writeLong(_sinceId);
		outParcel_.writeString(_lastUpadateDate);
		outParcel_.writeLong(_groupID);
		outParcel_.writeLong(_lastFriendPageNumber);
		outParcel_.writeInt(_totalFriendCount);
		outParcel_.writeInt(_lastTimelinePageNumber);
		outParcel_.writeInt(_isFriend ? 1 : 0);
        outParcel_.writeInt(_newTweetCount);
        outParcel_.writeInt(_lastFriendIndex);
        outParcel_.writeLong(_lastFriendSyncTime);
        outParcel_.writeInt(_currentFriendTotal);
        outParcel_.writeLong(_sinceIdForMentions);
        outParcel_.writeLong(_maxIdForMentions);
        outParcel_.writeInt(_totalTweetCount);
        outParcel_.writeInt(_totalFollowerCount);
        outParcel_.writeInt(_lastFollowerIndex);
        outParcel_.writeInt(_currentFollowerCount);
        outParcel_.writeLong(_lastFollowerPageNumber);
        outParcel_.writeLong(_keywordMaxID);
        outParcel_.writeLong(_keywordSinceID);
        outParcel_.writeParcelable(_keywordGroup, flags);
        outParcel_.writeTypedList(_timeline);

        Log.v(TAG, "in parcelableUser, curretnly timeline size is:" +  _timeline.size());

	}

	public ParcelableUser(Parcel parcelIn_) {
        _timeline =new ArrayList<ParcelableTweet>();
		_userId = parcelIn_.readLong();
		_createdAt = parcelIn_.readLong();
		_isProtected = parcelIn_.readInt() == 1;
		_description = parcelIn_.readString();
		_name = parcelIn_.readString();
		_screenName = parcelIn_.readString();
		_location = parcelIn_.readString();
		_profileImageUrl = parcelIn_.readString();
		_profileBackgroundImageUrl = parcelIn_.readString();
		_profileBannerImageUrl = parcelIn_.readString();
		_maxId = parcelIn_.readLong();
		_sinceId = parcelIn_.readLong();
		_lastUpadateDate = parcelIn_.readString();
		_groupID = parcelIn_.readLong();
		_lastFriendPageNumber = parcelIn_.readLong();
		_totalFriendCount = parcelIn_.readInt();
		_lastTimelinePageNumber = parcelIn_.readInt();
		_isFriend = parcelIn_.readInt() == 1;
        _newTweetCount = parcelIn_.readInt();
        _lastFriendIndex = parcelIn_.readInt();
        _lastFriendSyncTime = parcelIn_.readLong();
        _currentFriendTotal = parcelIn_.readInt();
        _maxIdForMentions = parcelIn_.readLong();
        _sinceIdForMentions = parcelIn_.readLong();
        _totalTweetCount = parcelIn_.readInt();
        _totalFollowerCount = parcelIn_.readInt();
        _lastFollowerIndex  = parcelIn_.readInt();
        _currentFollowerCount = parcelIn_.readInt();
        _lastFollowerPageNumber = parcelIn_.readLong();
        _keywordMaxID = parcelIn_.readLong();
        _keywordSinceID = parcelIn_.readLong();
        _keywordGroup = parcelIn_.readParcelable(ParcelableKeywordGroup.class.getClassLoader());
        parcelIn_.readTypedList(_timeline, ParcelableTweet.CREATOR);
        Log.v(TAG, "in reading parcelableuser now, curretnly timeline size is:" +  _timeline.size());

	}



	public static final Parcelable.Creator<ParcelableUser> CREATOR = new Parcelable.Creator<ParcelableUser>() {
		@Override
		public ParcelableUser createFromParcel(Parcel in) {
			return new ParcelableUser(in);
		}

		@Override
		public ParcelableUser[] newArray(int size) {
			return new ParcelableUser[size];
		}
	};

    public int getCurrentFriendCount() {
        return _currentFriendTotal;
    }

    public void setCurrentFriendTotal(int _currentFriendTotal) {
        this._currentFriendTotal = _currentFriendTotal;
    }

	public int getTotalFriendCount() {
		return _totalFriendCount;
	}

	public void setFriendCount(int _friendCount) {
		this._totalFriendCount = _friendCount;
	}
	
	public String getProfileBannerImageUrl() {
		return _profileBannerImageUrl;
	}

	public void setProfileBannerImageUrl(String _profileBannerImageUrl) {
		this._profileBannerImageUrl = _profileBannerImageUrl;
	}
	
	protected long getTime(Date date) {
		return date != null ? date.getTime() : 0;
	}
	
	public String getProfileBackgroundImageUrl() {
		return _profileBackgroundImageUrl;
	}

    public void addAll(Collection<ParcelableTweet> tweets_){
        _timeline.addAll(tweets_);
    }

	public void addTimeLineEntry(ParcelableTweet tweet_){
		_timeline.add(tweet_);
	}

    public long getLastFriendSyncTime() {
        return _lastFriendSyncTime;
    }

    public void setLastFriendSyncTime(long _lastFriendSyncTime) {
        this._lastFriendSyncTime = _lastFriendSyncTime;
    }

	public String getUserName(){
		return _name;
	}
	
	public void setGroupId(long groupId_){
		 _groupID = groupId_;
	}
	
	public long getGroupId(){
		return _groupID;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


    public long getMaxIdForMentions() {
        return _maxIdForMentions;
    }

    public void setMaxIdForMentions(long _maxIdForMentions) {
        this._maxIdForMentions = _maxIdForMentions;
    }

    public long getSinceIdForMentions() {
        return _sinceIdForMentions;
    }

    public void setSinceIdForMentions(long _sinceIdForMentions) {
        this._sinceIdForMentions = _sinceIdForMentions;
    }

	public long getCreatedAt() {
		return _createdAt;
	}

	public boolean isProtected() {
		return _isProtected;
	}

	public String getDescription() {
		return _description;
	}

	public String getScreenName() {
		return _screenName;
	}

	public String getLocation() {
		return _location;
	}

	public String getProfileImageUrl() {
		return _profileImageUrl;
	}

	public String getLastUpadateDate() {
		return _lastUpadateDate;
	}

	public void setLastUpadateDate(String _lastUpadateDate) {
		this._lastUpadateDate = _lastUpadateDate;
	}
	
	public long getSinceId() {
		return _sinceId;
	}

	public void setSinceId(long _sinceId) {
		this._sinceId = _sinceId;
	}

	public long getMaxId() {
		return _maxId;
	}

	public void setMaxId(long _pageNumber) {
		this._maxId = _pageNumber;
	}

	public List<ParcelableTweet> getUserTimeLine(){
		return _timeline;
	}

	public long getLastFriendPageNumber() {
		return _lastFriendPageNumber;
	}

	public void setLastFriendPageNumber(long _lastFriendPageNumber) {
		this._lastFriendPageNumber = _lastFriendPageNumber;
	}
	
	public int getLastTimelinePageNumber() {
		return _lastTimelinePageNumber;
	}

	public void setLastTimelinePageNumber(int _lastTimelinePageNumber) {
		this._lastTimelinePageNumber = _lastTimelinePageNumber;
	}

	public boolean isFriend() {
		return _isFriend;
	}

	public void setIsFriend(boolean _isFriend) {
		this._isFriend = _isFriend;
	}

    public int getNewTweetCount() {
        return _newTweetCount;
    }

    public void setNewTweetCount(int _newTweetCount) {
        this._newTweetCount = _newTweetCount;
    }

    public int getLastFriendIndex() {
        return _lastFriendIndex;
    }

    public void setLastFriendIndex(int _lastFriendMultiple) {
        this._lastFriendIndex = _lastFriendMultiple;
    }

    public int getTotalTweetCount() {
        return _totalTweetCount;
    }

    public void setTotalTweetCount(int _totalTweetCount) {
        this._totalTweetCount = _totalTweetCount;
    }

    public long getLastFollowerPageNumber() {
        return _lastFollowerPageNumber;
    }

    public void setLastFollowerPageNumber(long _lastFollowerPageNumber) {
        this._lastFollowerPageNumber = _lastFollowerPageNumber;
    }

    public int getCurrentFollowerCount() {
        return _currentFollowerCount;
    }

    public void setCurrentFollowerCount(int _currentFollowerCount) {
        this._currentFollowerCount = _currentFollowerCount;
    }

    public int getLastFollowerIndex() {
        return _lastFollowerIndex;
    }

    public void setLastFollowerIndex(int _lastFollowerIndex) {
        this._lastFollowerIndex = _lastFollowerIndex;
    }

    public int getTotalFollowerCount() {
        return _totalFollowerCount;
    }

    public void setTotalFollowerCount(int _totalFollowerCount) {
        this._totalFollowerCount = _totalFollowerCount;
    }

    public ParcelableKeywordGroup getKeywordGroup() {
        return _keywordGroup;
    }

    public void setKeywordGroup(ParcelableKeywordGroup _keywordGroup) {
        this._keywordGroup = _keywordGroup;
    }

    public long getKeywordMaxID() {
        return _keywordMaxID;
    }

    public void setKeywordMaxID(long _keywordMaxID) {
        this._keywordMaxID = _keywordMaxID;
    }

    public long getKeywordSinceID() {
        return _keywordSinceID;
    }

    public void setKeywordSinceID(long _keywordSinceID) {
        this._keywordSinceID = _keywordSinceID;
    }

    /**
     * Returns the friend id's currently loaded, can return null if no friend id's have been quried yet
     *
     * @return
     *      long []
     */
    public long[] getFriendIDs() {
        return _friendIDs;
    }

    public void setFriendIDs(long[] _friendIDs) {

        Log.v(TAG, "Friend ids passed in are" + Arrays.toString(_friendIDs));
        this._friendIDs = _friendIDs;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" User name ");
		builder.append(_name);
		builder.append(" UserId ");
		builder.append(_userId);
		builder.append(" Screen name ");
		builder.append(_screenName);
		builder.append(" maxID ");
		builder.append(_maxId);
		builder.append(" sinceID ");
		builder.append(_sinceId);
		builder.append(" groupID ");
		builder.append(_groupID);
        builder.append(" newTweetCount ");
        builder.append(_newTweetCount);
        builder.append(" currentFriendTotal ");
        builder.append(_currentFriendTotal);
        builder.append(" totalFriendCount ");
        builder.append(_totalFriendCount);
        builder.append(" lastFriendIndex ");
        builder.append(_lastFriendIndex);
        builder.append(" lastPageNumberForTweet ");
        builder.append(_lastTimelinePageNumber);
        builder.append(" total tweetcount ");
        builder.append(_totalTweetCount);
        builder.append(" keywordMaxid ");
        builder.append(_keywordMaxID);
        builder.append(" keywordSinceid ");
        builder.append(_keywordSinceID);
		return builder.toString();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParcelableUser that = (ParcelableUser) o;
        if (_createdAt != that._createdAt) return false;
        if (_userId != that._userId) return false;
        if (_screenName != null ? !_screenName.equals(that._screenName) : that._screenName != null) return false;
        if (_name != null ? !_name.equals(that._name) : that._name != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (_userId ^ (_userId >>> 32));
        result = 31 * result + (int) (_createdAt ^ (_createdAt >>> 32));
        result = 31 * result + (_screenName != null ? _screenName.hashCode() : 0);
        result = 31 * result + (_name != null ? _name.hashCode() : 0);
        return result;
    }

    public long getUserId() {
		return _userId;
	}

    public long[] getFollowerIDs() {
        return _followerIDs;
    }

    public void setFollowerIDs(long[] _followerIDs) {
        this._followerIDs = _followerIDs;
    }

    public void setScreenName(String screenName) {
        this._screenName = screenName;
    }

    public void setPofileBackgroundImageUrl(String pofileBackgroundImageUrl) {
        this._profileBackgroundImageUrl = pofileBackgroundImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this._profileImageUrl = profileImageUrl;
    }

    public void setDescription(String description) {
        this._description = description;
    }
}
