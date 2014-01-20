package com.sun.tweetfiltrr.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;


public class ParcelableUserToKeywords implements IParcelableTwitter {

	private ParcelableKeywordGroup _keywordGroup;
	private ParcelableUser _friend;
	
	
	@Override
	public void writeToParcel(Parcel out_, int flags_) {
		out_.writeParcelable(_keywordGroup, flags_);
		out_.writeParcelable(_friend, flags_);
	}
	
	public ParcelableUserToKeywords(ParcelableKeywordGroup keywordGroup_, ParcelableUser friend_){
		_keywordGroup = keywordGroup_;
		_friend = friend_;
	}


	public ParcelableKeywordGroup getKeywordGroup() {
		return _keywordGroup;
	}

	public ParcelableUser getFriend() {
		return _friend;
	}



	public static final Creator<ParcelableUserToKeywords> CREATOR = new Creator<ParcelableUserToKeywords>() {
		@Override
		public ParcelableUserToKeywords createFromParcel(Parcel in) {
			return new ParcelableUserToKeywords(in);
		}

		@Override
		public ParcelableUserToKeywords[] newArray(int size) {
			return new ParcelableUserToKeywords[size];
		}
	};
	
	public ParcelableUserToKeywords(Parcel parcelIn_) {
		_keywordGroup = parcelIn_.readParcelable(ParcelableKeywordGroup.class.getClassLoader());
		_friend = parcelIn_.readParcelable(ParcelableUser.class.getClassLoader());

	}
	
	@Override
	public int describeContents() {
		return 0;
	}


	
	
	
}
