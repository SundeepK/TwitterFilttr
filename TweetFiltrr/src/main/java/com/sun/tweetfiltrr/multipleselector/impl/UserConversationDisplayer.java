package com.sun.tweetfiltrr.multipleselector.impl;

import android.app.Dialog;
import android.content.Context;

import com.sun.tweetfiltrr.asyncretriever.retrievers.ConversationRetriever;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserConversationDisplayer extends MultipleItemSelector<ParcelableUser> implements ConversationRetriever.OnConvoLoadListener {
    protected static final String TAG = UserConversationDisplayer.class.getName();
    private List<ParcelableUser> _users;

    public UserConversationDisplayer(String title_, Context context_,
                                     int resource_, AItemSelector<ParcelableUser> listAdapter_) {
        super( listAdapter_);
        _users = new ArrayList<ParcelableUser>();
    }



    @Override
    protected Dialog createNewDialog(Context context_, String title_) {
        return null;
    }

    @Override
    public void onLoadFinish(LinkedList<ParcelableUser> conversation_) {
        _users.clear();
        _users.addAll(conversation_);
        clearAdapter();

        StringBuilder builder = new StringBuilder();
        for (ParcelableUser user : conversation_) {
            builder.append("@");
            builder.append(user.getScreenName());
            builder.append(" ");

        }

        addToAdapter(_users);

    }

}
