package com.sun.tweetfiltrr.fragment.api;

import android.content.Intent;

public abstract class ABroadCastReceiverFragment extends ATwitterFragment {

	private static final String TAG = ABroadCastReceiverFragment.class.getName();

	protected  abstract void onBroadCastReceive(final Intent intent_);


}
