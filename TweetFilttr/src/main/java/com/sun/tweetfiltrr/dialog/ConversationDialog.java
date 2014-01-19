package com.sun.tweetfiltrr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.List;

/**
 * Created by Sundeep on 22/12/13.
 */
public class ConversationDialog extends Dialog {

    private ListAdapter _listAdapter;
    private List<ParcelableUser> _usersInConvo;
    public ConversationDialog(Context context, ListAdapter listAdapter_, List<ParcelableUser> usersInConvo_) {
        super(context);
        _listAdapter = listAdapter_;
        _usersInConvo = usersInConvo_;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.conversation_display_dialog);
        ListView tweetsListView = (ListView) findViewById(android.R.id.list);
        tweetsListView.setAdapter(_listAdapter);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
}
