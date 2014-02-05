package com.sun.tweetfiltrr.zoomlistview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sun.tweetfiltrr.animation.CyclicFlipAnimation;

import java.util.HashMap;
import java.util.Map;

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
    private OnScrollListener _onScrollListener;
    private boolean _shouldPerformScrollAnimation;
    final private Map<Long, PropertyHolder> _itemIDToProperty = new HashMap<Long, PropertyHolder>();
    private long _currentFocusedId;


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

        scaleAllVisibleViews(shouldEnable);
    }


    private void scaleAllVisibleViews(final boolean shouldEnable_) {
        final ListAdapter adapter = getAdapter();
        Animation scaleAnimation;
        if(_isZoomed){
            scaleAnimation = getZoomAnimation(1f, 0.6f, 1f, 0.6f);
        }else{
            scaleAnimation = getZoomAnimation(0.6f, 1f, 0.6f, 1f);
        }

        int count = getChildCount();
            for (int i = 0; i < count; i++) {
                applyAnimation(i, adapter, shouldEnable_, scaleAnimation);
            }
    }


    private void applyAnimation(int position_, ListAdapter adapter_, boolean shouldEnable_, Animation animation_){
        if (_isZoomed) {
                if (_currentFocusedId != adapter_.getItemId(position_)) {
                    scaleView(position_,  shouldEnable_, animation_);
                }else{
                    displayExpandingView(position_, adapter_);
                }
        }else{

            if(_currentFocusedId != getAdapter().getItemId(position_)){
                scaleView(position_,  shouldEnable_, animation_);
            }else{
                View view = getChildAt(position_);
                View viewToShow =  _onItemFocusedLis.onItemFocused(view, position_, getAdapter().getItemId(position_));

                if(viewToShow != null){
                    viewToShow.setVisibility(GONE);
                }
            }

            _itemIDToProperty.remove(getAdapter().getItemId(position_));
        }
    }

    private void displayExpandingView(int position_, ListAdapter adapter_){
        View view = getChildAt(position_);
        if(view != null){
            View viewToShow =  _onItemFocusedLis.onItemFocused(view, position_, adapter_.getItemId(position_));
            viewToShow.setVisibility(VISIBLE);
            Animation flip = new CyclicFlipAnimation(60f);
            flip.setDuration(1000);
            viewToShow.startAnimation(flip);

            if(_expandingViewHeight <= 0){
                viewToShow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                _expandingViewHeight = viewToShow.getMeasuredHeight();
            }

        }
    }

    private Animation getZoomAnimation(float fromX_, float toX_, float fromY_, float toY_){
      Animation  scaleAnimation = new ScaleAnimation(
                fromX_, toX_,
                fromY_, toY_,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.ABSOLUTE, 0.7f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(500);
        return scaleAnimation;
    }

    private void scaleView(int position_,  boolean shouldEnable_, Animation animationScale_ ){
        View view = getChildAt(position_);
        ListAdapter adapter = getAdapter();
        long id = adapter.getItemId(position_);
        view.startAnimation(animationScale_);

        PropertyHolder holder = _itemIDToProperty.get(id);

        if (holder == null) {
            holder = new PropertyHolder((int) view.getTop(), (int) (view.getBottom()));
            _itemIDToProperty.put(id, holder);
        }

        int h = view.getHeight();
        if (_isZoomed) {
            view.animate().translationYBy((h * 0.5f)).setDuration(500).start();
        } else {
            view.animate().translationYBy(-(h * 0.5f )).setDuration(500).start();
        }

        if (_onItemFocusedLis != null) {
            _onItemFocusedLis.onItemOutOfFocus(position_, shouldEnable_);
        }

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        _isZoomed = true;

        int firstVisiblePosition = getFirstVisiblePosition();
        int pos = pointToPosition(_xPos, _yPos);
        int positionOrg = pos - firstVisiblePosition;

        _currentFocusedId = getAdapter().getItemId(positionOrg);
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

            case MotionEvent.ACTION_MOVE:
                if (!_isZoomed) {
                    animateRemaining();
                }
            break;
        }
        return super.onTouchEvent(event);
    }

    private void animateRemaining(){
        if(!_itemIDToProperty.isEmpty()){
        for(int i = 0; i < getChildCount(); i++){
           long id =  getAdapter().getItemId(i);
           PropertyHolder n = _itemIDToProperty.get(id);
            if(n != null){

                if(_currentFocusedId != getAdapter().getItemId(i)){
                    scaleView(i,  true, getZoomAnimation(0.6f, 1, 0.6f, 1f));
                }else{
                    Log.v(TAG, "not translating for " + getAdapter().getItemId(i)+ " for id " + _currentFocusedId);
                }
                _itemIDToProperty.remove(id);
            }
        }
        }
    }


    private Rect getChildViewRect(View parentView, View childView) {
        final Rect childRect = new Rect(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom() + _expandingViewHeight);
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

    private class PropertyHolder{
        int _top;
        int _bot;

        private PropertyHolder(int top_, int bot_){
            _top = top_;
            _bot = bot_;

        }

    }


}
