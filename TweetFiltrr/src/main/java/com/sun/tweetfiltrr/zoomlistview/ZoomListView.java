package com.sun.tweetfiltrr.zoomlistview;

import android.content.Context;
import android.graphics.Rect;
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
public class ZoomListView extends ListView implements AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener {


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
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch (scrollState){

            case SCROLL_STATE_FLING :
                _shouldPerformScrollAnimation = false;
                break;

            case SCROLL_STATE_IDLE :
                _shouldPerformScrollAnimation = true;
                break;

            case SCROLL_STATE_TOUCH_SCROLL:
                _shouldPerformScrollAnimation = true;
                break;

        }

        _onScrollListener.onScrollStateChanged(view,scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if(_shouldPerformScrollAnimation){
//            View lastView = getChildAt((visibleItemCount-1));
//            lastView.startAnimation(getZoomAnimation(0.8f, 1f, 0.8f, 1f));
        }
        _onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

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
    public void setOnScrollListener(OnScrollListener l) {
        _onScrollListener = l;
        super.setOnScrollListener(this);
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



    private void scaleAllVisibleViews(final int clickedItemPosition_, final float scale_, final boolean shouldEnable_) {
        Animation scaleAnimation;
        if(_isZoomed){
            scaleAnimation = getZoomAnimation(1f, 0.6f, 1f, 0.6f);
        }else{
            scaleAnimation = getZoomAnimation(0.6f, 1f, 0.6f, 1f);
        }
        int firstVisiblePosition = getFirstVisiblePosition();
        int count = getChildCount();



        if (_isZoomed) {

        for (int i = 0; i < count; i++) {
            int pos = i ;
            if (_isZoomed) {

                if (getAdapter().getItemId(clickedItemPosition_) != getAdapter().getItemId(pos)) {
                    scaleView(pos, scale_, shouldEnable_, scaleAnimation);
                }else{
                    displayExpandingView(pos, clickedItemPosition_);
                }

            } else {
//                    View view = getChildAt(pos);
//
//                    View viewToShow =  _onItemFocusedLis.onItemFocused(view, pos, getAdapter().getItemId(clickedItemPosition_));
//
//                    if(viewToShow != null){
//                        viewToShow.setVisibility(GONE);
//                    }
//                    scaleView(pos, scale_, shouldEnable_, scaleAnimation);
//                    invalidateViews();
            }

        }
            translateViews();

        }else{
            final Animation scaleAnim = scaleAnimation;
            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    int firstVisiblePosition = getFirstVisiblePosition();

                    for (int i = 0; i < getChildCount(); ++i) {
                    int pos = i ;
                    View view = getChildAt(pos);
                    View viewToShow =  _onItemFocusedLis.onItemFocused(view, pos, getAdapter().getItemId(clickedItemPosition_));
                    _itemIDToProperty.remove(getAdapter().getItemId(pos));
                    if(viewToShow != null){
                        viewToShow.setVisibility(GONE);
                    }
                    scaleView(pos, scale_, shouldEnable_, scaleAnim);
                    }

                    return true;
                }
            });
        }




    }

    private void displayExpandingView(int position_, int clickedItemPosition_){
        View view = getChildAt(position_);
        if(view != null){
          //  view.animate().translationYBy(30).setDuration(500).start();
            Log.v(TAG, "view is valid");
            View viewToShow =  _onItemFocusedLis.onItemFocused(view, position_, getAdapter().getItemId(clickedItemPosition_));

            viewToShow.setVisibility(VISIBLE);
            Animation flip = new CyclicFlipAnimation(50f);
            flip.setDuration(500);
            viewToShow.startAnimation(flip);

            if(_expandingViewHeight <= 0){
                viewToShow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                _expandingViewHeight = viewToShow.getMeasuredHeight();
                Log.v(TAG, "expanding view hieght is + " + _expandingViewHeight);
            }
        }
    }


    private Animation getZoomAnimation(float fromX_, float toX_, float fromY_, float toY_){
      Animation  scaleAnimation = new ScaleAnimation(
                fromX_, toX_,
                fromY_, toY_,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.ABSOLUTE, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(500);
        return scaleAnimation;
    }

    private void scaleView(int position_, float scale_, boolean shouldEnable_, Animation animation_){
        View view = getChildAt(position_);
        ListAdapter adapter = getAdapter();
        long id = adapter.getItemId(position_);
        view.startAnimation(animation_);

        if (view != null) {

            PropertyHolder holder = _itemIDToProperty.get(id);

            if(holder == null){
                holder = new PropertyHolder(view.getTop(), view.getBottom());
                _itemIDToProperty.put(id, holder);
            }

            //view.setTop(top);
            if (_onItemFocusedLis != null) {
                _onItemFocusedLis.onItemOutOfFocus(position_, shouldEnable_);
            }
        }
    }

    private void translateViews(){

        for (int i = 0; i < getChildCount(); ++i) {
            View view = getChildAt(i);
            ListAdapter adapter = getAdapter();
            long id = adapter.getItemId(i);
            PropertyHolder holder = _itemIDToProperty.get(id);

            if (holder != null) {


                int prevPos = i - 1 < 0 ? -1 : i - 1;

                if (i - 1 >= 0) {
                    long idPrev = adapter.getItemId(prevPos);
                    PropertyHolder prevProp = _itemIDToProperty.get(idPrev);

                    if (prevProp != null) {
                        int p = (prevProp._bot - holder._top);
                        if (p >= 200) {
                            view.animate().translationYBy( p / 2).setDuration(500).start();

                        } else {
                            if ((holder._bot - holder._top) >= 300) {
                                view.animate().translationYBy((holder._bot - holder._top) / 2).setDuration(500).start();
                            }
                        }
                    }

                } else {
                    if (holder != null) {
                        long idPrev = adapter.getItemId((i+1));
                        PropertyHolder prevProp = _itemIDToProperty.get(idPrev);
                        int p = (prevProp._top - holder._bot);
                        Log.v(TAG, "first view bot " + holder._bot + "with nexttop: " + prevProp._top );
                        if ((p) >= 200) {
                            view.animate().translationYBy((p) / 2).setDuration(500).start();
                        }
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
                scaleView(i, 0, true, getZoomAnimation(0.8f, 1, 0.8f, 1f));
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
