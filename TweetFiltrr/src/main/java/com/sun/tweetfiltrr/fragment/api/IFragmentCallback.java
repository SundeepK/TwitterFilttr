package com.sun.tweetfiltrr.fragment.api;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sundeep on 14/01/14.
 */
public interface IFragmentCallback {

    public View onCreateViewCallback(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) ;
    public void onCreateCallback(Bundle savedInstanceState);



}
