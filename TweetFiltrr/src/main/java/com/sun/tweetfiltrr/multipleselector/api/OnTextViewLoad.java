package com.sun.tweetfiltrr.multipleselector.api;

import android.widget.TextView;

public interface OnTextViewLoad<T> {
	public void onPostTextViewLoad(TextView textView_, T item_);
}