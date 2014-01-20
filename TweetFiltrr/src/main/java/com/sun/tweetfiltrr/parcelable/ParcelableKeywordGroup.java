package com.sun.tweetfiltrr.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

public class ParcelableKeywordGroup implements IParcelableTwitter {
	private long _groupId;
	private String _groupName, _groupKeywords;
	
	public String getGroupName() {
		return _groupName;
	}

	public void setGroupName(String _groupName) {
		this._groupName = _groupName;
	}

	public String getGroupKeywords() {
		return _groupKeywords;
	}

	public void setGroupKeywords(String _groupKeywords) {
		this._groupKeywords = _groupKeywords;
	}

	public long getGroupId() {
		return _groupId;
	}

	public ParcelableKeywordGroup(long groupId_,  String groupName_, String groupKeywords_){
		_groupId = groupId_;
		_groupName = groupName_;
		_groupKeywords = groupKeywords_;
	}
	
	public ParcelableKeywordGroup( String groupName_, String groupKeywords_){
		_groupName = groupName_;
		_groupKeywords = groupKeywords_;
	}
	
	

	public static final Creator<ParcelableKeywordGroup> CREATOR = new Creator<ParcelableKeywordGroup>() {
		@Override
		public ParcelableKeywordGroup createFromParcel(Parcel in) {
			return new ParcelableKeywordGroup(in);
		}

		@Override
		public ParcelableKeywordGroup[] newArray(int size) {
			return new ParcelableKeywordGroup[size];
		}
	};
	
	public ParcelableKeywordGroup(Parcel parcelIn_) {
		_groupId = parcelIn_.readLong();
		_groupName = parcelIn_.readString();
		_groupKeywords = parcelIn_.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out_, int flags_) {
		out_.writeLong(_groupId);
		out_.writeString(_groupName);
		out_.writeString(_groupKeywords);		
	}

}
