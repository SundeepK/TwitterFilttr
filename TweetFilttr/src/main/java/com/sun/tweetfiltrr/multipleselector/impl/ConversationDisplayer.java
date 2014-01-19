package com.sun.tweetfiltrr.multipleselector.impl;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sun.tweetfiltrr.asyncretriever.ConversationRetriever;
import com.sun.tweetfiltrr.dialog.ConversationDialog;
import com.sun.tweetfiltrr.imageprocessor.IOnImageProcessCallback;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConversationDisplayer extends MultipleItemSelector<ParcelableUser> implements ConversationRetriever.OnConvoLoadListener, IOnImageProcessCallback {
    protected static final String TAG = ConversationDisplayer.class.getName();
    private int _resource;
    private EditText _tweetTxtBox;
    private LinearLayout _layout;
    private List<ParcelableUser> _users;
    private Resources _resources;

    public ConversationDisplayer(String title_, Context context_,
                                 int resource_, AItemSelector<ParcelableUser> listAdapter_) {

        super( listAdapter_);
        _users = new ArrayList<ParcelableUser>();
        _resources = context_.getResources();
        _layout = new LinearLayout(context_);
        _layout.setOrientation(LinearLayout.VERTICAL);
        _layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        _list = new ListView(context_);
        _list.setAdapter(_listAdapter);
        _layout.addView(_list);
       _tweetTxtBox = new EditText(context_);
       _layout.addView(_tweetTxtBox);
       _dialog = createNewDialog(context_, title_);

    }


    @Override
    protected Dialog createNewDialog(Context context_, String title_) {
        Log.v(TAG, "Im in conversation createNewDialog mewthod");
//        AlertDialog.Builder builder = new AlertDialog.Builder(context_, R.style.CustomDialogTheme)
//                .setView(_layout).setPositiveButton(" Tweet! ", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.v(TAG, _tweetTxtBox.getText().toString());
//
//                        for (ParcelableUser user : _users) {
//                            for (ParcelableTimeLineEntry tweet : user.getUserTimeLine()) {
//                                Log.v(TAG, tweet.toString());
//                            }
//                        }
//                        ParcelableUser user = _users.get(_users.size() - 1);
//                        //At this point the app user can only reply to one tweet, but resonse can be sent ot multiple people
//                        //so we can just break after first iteration
//                        for (ParcelableTimeLineEntry tweet : user.getUserTimeLine()) {
//                            ParcelableTimeLineEntry reply = new ParcelableTimeLineEntry(_tweetTxtBox.getText().toString(), "", 0, 0, "", 0, tweet.getTweetID());
//                            AsyncStatusUpdater statusUpdater = new AsyncStatusUpdater(reply, null);
//                            ThreadPoolExecutor executor = TwitterUtil.getInstance().getGlobalExecutor();
//                            executor.execute(statusUpdater);
//                            break;
//                        }
//
//                    }
//
//                }).setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//
//        AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ConversationDialog dialog = new ConversationDialog(context_,_listAdapter, _users);
        return dialog;
    }

    @Override
    public void onLoadFinish(LinkedList<ParcelableUser> conversation_) {

//		for (ParcelableUser user : conversation_) {
//			for (ParcelableTimeLineEntry tweet : user.getUserTimeLine()) {
//				Log.v(TAG, tweet.toString());
//			}
//		}
        _users.clear();
        _users.addAll(conversation_);
        clearAdapter();

        StringBuilder builder = new StringBuilder();
        for (ParcelableUser user : conversation_) {
            builder.append("@");
            builder.append(user.getScreenName());
            builder.append(" ");

        }

        _tweetTxtBox.setText(builder.toString());
        addToAdapter(_users);
        showDialog();
        _dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


    }

    @Override
    public void OnImageProcessFinish(Bitmap outPutBitmap_) {
        _dialog.getWindow().setBackgroundDrawable(new BitmapDrawable(_resources,outPutBitmap_));
    }
}
