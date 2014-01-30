package com.sun.tweetfiltrr.zoomlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sun.tweetfiltrr.utils.TwitterConstants;

/**
 * Created by Sundeep.Kahlon on 30/01/14.
 */
public class ZoomListView extends ListView implements AdapterView.OnItemLongClickListener {


    private static final String TAG = ZoomListView.class.getName();

    public ZoomListView(Context context_) {
        super(context_);
        init(context_);
    }

    public ZoomListView(Context context_, AttributeSet attrs) {
        super(context_, attrs);
        init(context_);

    }

    public ZoomListView(Context context_, AttributeSet attrs, int defStyle) {
        super(context_, attrs, defStyle);
        init(context_);

    }


    private void init(Context context_){
        setOnItemLongClickListener(this);
    }


    private void scaleChildViews(long rowId_, int itemPos_){
        ListAdapter adapter = getAdapter();
        int viewCount = getChildCount();

//        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();

        int firstVisiblePosition = getFirstVisiblePosition() - getHeaderViewsCount(); // This is the same as child #0
        int startingChild = itemPos_ - firstVisiblePosition;

        for(int i = 0; i < lastVisiblePosition;  i++){
            Log.v(TAG, "scalling item number : " + i + " with iem id: " + adapter.getItemId(i) + " comparing to id " + rowId_);
//            if(rowId_ != adapter.getItemId(i)){
//            if(i != itemPos_){
            int position = firstVisiblePosition + i;

            View view = getChildAt(i);
                if(view != null){
                 view.setScaleX(0.5f);
                 view.setScaleY(0.5f);
                }else{
                    Log.v(TAG, "scalling item number : " + i + " is null");
//                }
            }
//            }
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.v(TAG, "im on long item click");
//        View v = getChildAt(i);
        scaleChildViews(l, i);
        return true;
    }




}
