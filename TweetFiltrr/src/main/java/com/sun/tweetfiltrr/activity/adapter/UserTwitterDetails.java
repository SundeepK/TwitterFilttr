package com.sun.tweetfiltrr.activity.adapter;

public class UserTwitterDetails {
	
	private String _attribute;
	private int _count;
	
	public UserTwitterDetails(String attribute_, int count_){
		_attribute = attribute_;
		_count = count_;
	}

	public String getAttribute() {
		return _attribute;
	}

	public int get_count() {
		return _count;
	}

		

}
