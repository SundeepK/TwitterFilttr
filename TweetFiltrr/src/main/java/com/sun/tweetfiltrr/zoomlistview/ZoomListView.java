package com.sun.tweetfiltrr.zoomlistview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Sundeep.Kahlon on 30/01/14.
 */
public class ZoomListView extends ListView implements AdapterView.OnItemLongClickListener {


    private static final String TAG = ZoomListView.class.getName();
    private int _xPos;
    private int _yPos;
    private int _pointerId;
    private Rect _viewBounds;
    private boolean _isZoomed;
    private OnItemClickListener _listner;
    private OnItemDisabled _onItemDisableLis;
    public interface OnItemDisabled{

        /**
         * This interface can be used to be notified when a particular item should be disabled, or is currently not focused
         * @param position
         */
        public void itemEnabledStatus(int position, boolean status_);
    }

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

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        if(!(listener == null)){
        _listner = listener;
        }
        super.setOnItemClickListener(listener);
    }

    private void init(Context context_){
        setOnItemLongClickListener(this);
    }

    public void setOnItemDisableListener(OnItemDisabled listener_){
        _onItemDisableLis = listener_;
    }

    private void scaleChildViews(long rowId_, int itemPos_, float scale, boolean shouldEnable){
        if (_isZoomed) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        int firstVisiblePosition = getFirstVisiblePosition();
        int pos = pointToPosition(_xPos, _yPos);
        int positionOrg = pos - firstVisiblePosition;

        for (int i = 0; i <= getLastVisiblePosition() - getFirstVisiblePosition(); i++) {
            if (getAdapter().getItemId(positionOrg) != getAdapter().getItemId(i)) {
                int position = i;
                View view = getChildAt(position);
                if (view != null) {
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                    if (_onItemDisableLis != null) {
                        _onItemDisableLis.itemEnabledStatus(position, shouldEnable);
                    }
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        _isZoomed = true;
        scaleChildViews(l, i, 0.8f, false);
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                _xPos = (int) event.getX();
                _yPos = (int) event.getY();
                _pointerId = event.getPointerId(0);

                if(_isZoomed){
                    if (!_viewBounds.contains(_xPos, _yPos)) {
                        _isZoomed = false;
                        scaleChildViews(1, 1, 1f, true);
                    }
                    return false;
                }

                    int position = pointToPosition(_xPos, _yPos);
                    int childNum = (position != INVALID_POSITION) ? position - getFirstVisiblePosition() : -1;
                    View itemView = (childNum >= 0) ? getChildAt(childNum) : null;
                    if (itemView != null) {
                       _viewBounds = getChildViewRect(this, itemView);
                    }

                break;

        }

        return super.onTouchEvent(event);
    }


    private Rect getChildViewRect(View parentView, View childView) {
        final Rect childRect = new Rect(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
        if (parentView == childView) {
            return childRect;
        }

        ViewGroup parent = (ViewGroup) childView.getParent();
        while (parent != parentView) {
            childRect.offset(parent.getLeft(), parent.getTop());
            childView = parent;
            parent = (ViewGroup) childView.getParent();
        }

        return childRect;
    }

}
