package com.sun.tweetfiltrr.api;

public interface IProgressStatus {

	public void onProgressStart(int startingValue_);
	
	public void onProgressUpdate(int updateValue_);

	public void onProgressFinish();
}
