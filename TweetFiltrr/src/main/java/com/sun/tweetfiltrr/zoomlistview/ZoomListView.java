package com.sun.tweetfiltrr.zoomlistview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sun.tweetfiltrr.animation.CyclicFlipAnimation;

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
    private OnItemFocused _onItemFocusedLis;
    private int _expandingViewHeight = 0;
    private int _previousFocusedViewHeight;
    public interface OnItemFocused {

        /**
         * This interface can be used to be notified when a particular item should be disabled, or is currently not focused
         * @param position
         */
        public void onItemOutOfFocus(int position, boolean status_);
        public View onItemFocused(View focusedView_, int listViewPosition_, long uniqueId_);
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

    public void setOnItemDisableListener(OnItemFocused listener_){
        _onItemFocusedLis = listener_;
    }

    private void scaleChildViews(long rowId_, int itemPos_, float scale, boolean shouldEnable){

        if (_isZoomed) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        int firstVisiblePosition = getFirstVisiblePosition();
        int pos = pointToPosition(_xPos, _yPos);
        int positionOrg = pos - firstVisiblePosition;
        scaleAllVisibleViews(positionOrg, scale, shouldEnable);
    }

    private void scaleAllVisibleViews(int clickedItemPosition_, float scale_, boolean shouldEnable_) {
        Animation scaleAnimation;
        if(_isZoomed){
            scaleAnimation = getZoomAnimation(1f, 0.8f, 1f, 0.8f);
        }else{
            scaleAnimation = getZoomAnimation(0.8f, 1f, 0.8f, 1f);
        }
        for (int i = 0; i <= getLastVisiblePosition() - getFirstVisiblePosition(); i++) {
            if (_isZoomed) {
                if (getAdapter().getItemId(clickedItemPosition_) != getAdapter().getItemId(i)) {
                    scaleView(i, scale_, shouldEnable_, scaleAnimation);
                }else{
                    View view = getChildAt(i);

                    if(view != null){
                        Log.v(TAG, "view is valid");
                        View viewToShow =  _onItemFocusedLis.onItemFocused(view, i, getAdapter().getItemId(clickedItemPosition_));

                        viewToShow.setVisibility(VISIBLE);
                        Animation flip = new CyclicFlipAnimation(50f,-50f);
                        flip.setDuration(500);
                        viewToShow.startAnimation(flip);
//                        Log.v(TAG, "Y value " + view.getY());
//                        Log.v(TAG, "viewToShow height " + viewToShow.getY());
//                        Log.v(TAG, "viewToShow pos " + (viewToShow.getHeight()));
//                        Log.v(TAG, "focused view height " + view.getHeight());
//                        Log.v(TAG, "height of focused view " + view.getLayoutParams().height);
//                        Log.v(TAG, "measured height of view " + view.getMeasuredHeight());

//                        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                         int height = view.getMeasuredHeight();
//
//                        if(_expandingViewHeight <= 0){
//                            viewToShow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                            _expandingViewHeight = view.getMeasuredHeight();
//                        }
//
//                        view.getLayoutParams().height = height + _expandingViewHeight;
//                        view.requestLayout();
//                        viewToShow.setY(height);

                    }else{
                        Log.v(TAG, "view is null");
                    }
                }
            } else {
                View view = getChildAt(i);
//                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//
//                int height = view.getMeasuredHeight();
//
//                view.getLayoutParams().height = height - _expandingViewHeight;
//                view.requestLayout();
                View viewToShow =  _onItemFocusedLis.onItemFocused(view, i, getAdapter().getItemId(clickedItemPosition_));

                if(viewToShow != null){
                    viewToShow.setVisibility(GONE);
                }
                scaleView(i, scale_, shouldEnable_, scaleAnimation);


            }
        }
    }

    private Animation getZoomAnimation(float fromX_, float toX_, float fromY_, float toY_){
      Animation  scaleAnimation = new ScaleAnimation(
                fromX_, toX_,
                fromY_, toY_,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(500);
        return scaleAnimation;
    }

    private void scaleView(int position_, float scale_, boolean shouldEnable_, Animation animation_){
        View view = getChildAt(position_);
        if (view != null) {
            view.startAnimation(animation_);
            if (_onItemFocusedLis != null) {
                _onItemFocusedLis.onItemOutOfFocus(position_, shouldEnable_);
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

                if (_isZoomed) {
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
