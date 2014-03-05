package com.sun.tweetfiltrr.parcelable;

import android.os.Parcel;
import android.util.Log;

import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import twitter4j.MediaEntity;
import twitter4j.Status;

public class ParcelableTweet implements IParcelableTwitter {

    private static final String PHOTO_MEDIA_TYPE = "photo";
	private String _tweetText;
	private long _tweetID;
	private long _friendID;
	private String _tweetDate;
	private String _inReplyToScreenName;
	private long _inReplyToUserId;
	private long _inReplyToTweetId;
    private String _photoUrl;
    private boolean _isFavourite;
    private boolean _isRetweeted;
    private boolean _isKeyWordSearedTweet;
    private boolean _isMention;

	public static final Creator<ParcelableTweet> CREATOR = new Creator<ParcelableTweet>() {
		@Override
		public ParcelableTweet createFromParcel(Parcel in) {
			return new ParcelableTweet(in);
		}

		@Override
		public ParcelableTweet[] newArray(int size) {
			return new ParcelableTweet[size];
		}
	};
    private static final String TAG = ParcelableTweet.class.getName();

    @Override
    public void writeToParcel(Parcel out_, int flags) {
        out_.writeString(_tweetText);
        out_.writeString(_tweetDate);
        out_.writeLong(_tweetID);
        out_.writeLong(_friendID);
        out_.writeString(_inReplyToScreenName);
        out_.writeLong(_inReplyToUserId);
        out_.writeLong(_inReplyToTweetId);
        out_.writeInt(_isFavourite ? 1 : 0);
        out_.writeInt(_isRetweeted ? 1 : 0);
        out_.writeString(_photoUrl);
        out_.writeInt(_isKeyWordSearedTweet ? 1 : 0);
        out_.writeInt(_isMention ? 1 : 0);

    }

	public ParcelableTweet(Parcel parcelIn_) {
		_tweetText = parcelIn_.readString();
		_tweetDate = parcelIn_.readString();
		_tweetID = parcelIn_.readLong();
		_friendID = parcelIn_.readLong();
		_inReplyToScreenName = parcelIn_.readString();
		_inReplyToUserId = parcelIn_.readLong();
		_inReplyToTweetId = parcelIn_.readLong();
        _isFavourite = parcelIn_.readInt() == 1 ? true : false;
        _isRetweeted = parcelIn_.readInt() == 1 ? true : false;
        _photoUrl = parcelIn_.readString();
        _isKeyWordSearedTweet = parcelIn_.readInt() == 1 ? true : false;
        _isMention = parcelIn_.readInt() == 1 ? true : false;

    }

	
	public ParcelableTweet(Status tweet, String dateCreated_, long friendID_) {
		_tweetText = tweet.getText();
		_tweetDate = dateCreated_;
		_tweetID = tweet.getId();
		_friendID = friendID_;
		_inReplyToScreenName = tweet.getInReplyToScreenName();
		_inReplyToUserId = tweet.getInReplyToUserId();
        _inReplyToTweetId = tweet.getInReplyToStatusId();
        _isFavourite = tweet.isFavorited();
        _isRetweeted = tweet.isRetweetedByMe();
        Log.v(TAG, "is reteeted by me is :"  + _isRetweeted);
        _photoUrl =getPhotoUrl(tweet.getMediaEntities());
        _isKeyWordSearedTweet = false;
        _isMention = false;

    }

    private String getPhotoUrl(MediaEntity[] mediaEntries_)
    {
        //for now we'll just take the first entry
        if (mediaEntries_.length > 0) {
//            MediaEntity photo = mediaEntries_[0];
            for(MediaEntity photo :mediaEntries_){
                if (photo.getType().equals(PHOTO_MEDIA_TYPE)) {
                    Log.v(TAG, "Found Image " + photo.getMediaURL());
                    return photo.getMediaURL();
                }
            }

        }
        //just return empty string
        return "";
    }

	
	public ParcelableTweet(String tweetText_, String tweetDate_,
                           long tweetID_, long friendID_, String inReplyToScreenName_,
                           long inReplyToUserId_, long inReplyToTweetId_, String photoUrl_,
                           boolean isFavourite_, boolean isRetweeted_, boolean isMention_) {
		_tweetText = tweetText_;
		_tweetDate = tweetDate_;
		_tweetID = tweetID_;
		_friendID = friendID_;
		_inReplyToScreenName = inReplyToScreenName_;
		_inReplyToUserId = inReplyToUserId_;
		_inReplyToTweetId = inReplyToTweetId_;
        _photoUrl = photoUrl_;
        _isFavourite =isFavourite_;
        _isRetweeted = isRetweeted_;
        _isMention = isMention_;
		
	}

	@Override
	public int describeContents() {
		return 0;
	}


    public boolean isKeyWordSearchedTweet() {
        return _isKeyWordSearedTweet;
    }

    public void setIsKeyWordSearedTweet(boolean _isKeyWordSearedTweet) {
        this._isKeyWordSearedTweet = _isKeyWordSearedTweet;
    }

	public long getFriendID() {
		return _friendID;
	}

	public void setFriendID(long _friendID) {
		this._friendID = _friendID;
	}

	public String getTweetText() {
		return _tweetText;
	}

	public long getTweetID() {
		return _tweetID;
	}

	public String getTweetDate() {
		return _tweetDate;
	}


	public String getInReplyToScreenName() {
		return _inReplyToScreenName;
	}


	public void setInReplyToScreenName(String _inReplyToScreenName) {
		this._inReplyToScreenName = _inReplyToScreenName;
	}


	public long getInReplyToUserId() {
		return _inReplyToUserId;
	}


	public void setInReplyToUserId(long _inReplyToUserId) {
		this._inReplyToUserId = _inReplyToUserId;
	}


	public long getInReplyToTweetId() {
		return _inReplyToTweetId;
	}


	public void setInReplyToTweetId(long _inReplyToTweetId) {
		this._inReplyToTweetId = _inReplyToTweetId;
	}

    public boolean isFavourite() {
        return _isFavourite;
    }

    public void setIsFavourite(boolean isFavourite_) {
        _isFavourite = isFavourite_;
    }

    public boolean isRetweeted() {
        return _isRetweeted;
    }

    public void setIsRetweeted(boolean isRetweeted_) {
        _isRetweeted = isRetweeted_;
    }

    public String getPhotoUrl() {
        return _photoUrl;
    }

    public void setPhotoUrl(String _photoUrl) {
        this._photoUrl = _photoUrl;
    }

    public boolean isMention() {
        return _isMention;
    }

    public void setIsMention(boolean _isMention) {
        this._isMention = _isMention;
    }


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tweetd ID: ");
		builder.append(_tweetID);
		builder.append("Friend ID: ");
		builder.append(_friendID);
		builder.append(" text: ");
		builder.append(_tweetText);
		builder.append(" Date: ");
		builder.append(_tweetDate);
		builder.append(" in reply to user: ");
		builder.append(_inReplyToScreenName);	
		builder.append(" in reply to tweetID: ");
		builder.append(_inReplyToTweetId);	
		builder.append(" in reply to userID: ");
		builder.append(_inReplyToUserId);
        builder.append(" is favourite: ");
        builder.append(_isFavourite);
        builder.append(" is retweeted: ");
        builder.append(_isRetweeted);
        builder.append(" media photo url: ");
        builder.append(_photoUrl);
        builder.append(" is mention: ");
        builder.append(_isMention);

        return builder.toString();
	}
	
	

}
