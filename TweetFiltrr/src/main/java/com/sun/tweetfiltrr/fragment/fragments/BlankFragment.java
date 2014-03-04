package com.sun.tweetfiltrr.fragment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.tweetfiltrr.R;

/**
 * Created by Sundeep.Kahlon on 04/03/14.
 */
public class BlankFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.blank_linerlayout, container, false);
        return rootView;
    }
}
