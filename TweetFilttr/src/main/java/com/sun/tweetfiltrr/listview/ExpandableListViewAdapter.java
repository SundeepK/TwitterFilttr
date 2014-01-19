package com.sun.tweetfiltrr.listview;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ListAdapter;

import java.util.BitSet;

public class ExpandableListViewAdapter extends AdapterWrapper {
	View _viewToExpand;

	/**
	 * Reference to the last expanded list item. Since lists are recycled this
	 * might be null if though there is an expanded list item
	 */
	private View lastOpen = null;
	/**
	 * The position of the last expanded list item. If -1 there is no list item
	 * expanded. Otherwise it points to the position of the last expanded list
	 * item
	 */
	private int lastOpenPosition = -1;

	/**
	 * Default Animation duration Set animation duration with @see
	 * setAnimationDuration
	 */
	private int animationDuration = 1000;

	/**
	 * A list of positions of all list items that are expanded. Normally only
	 * one is expanded. But a mode to expand multiple will be added soon.
	 * 
	 * If an item onj position x is open, its bit is set
	 */
	private BitSet openItems = new BitSet();
	/**
	 * We remember, for each collapsable view its height. So we dont need to
	 * recalculate. The height is calculated just before the view is drawn.
	 */
	private final SparseIntArray viewHeights = new SparseIntArray(10);
	int togleButton;
	private static final String TAG = ExpandableListViewAdapter.class.getName();
	int viewTarget;
	private OnExpandingViewLoad _onExpandingViewListener;
	
	public ExpandableListViewAdapter(ListAdapter adapter_, int viewTarget_,
			int togleButton_) {
		super(adapter_);
		Log.v(TAG, "in ExpandableListViewAdapter");
		viewTarget = viewTarget_;
		togleButton = togleButton_;
	}
	
	public ExpandableListViewAdapter(ListAdapter adapter_, int viewTarget_,
			int togleButton_, OnExpandingViewLoad onExpandingViewListener_) {
		super(adapter_);
		Log.v(TAG, "in ExpandableListViewAdapter");
		viewTarget = viewTarget_;
		togleButton = togleButton_;
		_onExpandingViewListener = onExpandingViewListener_;
	}

	
	public interface OnExpandingViewLoad {
		public void onViewLoad(View expandingView_, int position_, long rowId_);
	}
	
	public View getExpandToggleButton(View parent) {
		return parent.findViewById(togleButton);
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = _adapter.getView(position, view, viewGroup);
		enableFor(view, position);
		return view;
	}

	/**
	 * Gets the duration of the collapse animation in ms. Default is 330ms.
	 * Override this method to change the default.
	 * 
	 * @return the duration of the anim in ms
	 */
	public int getAnimationDuration() {
		return animationDuration;
	}

	/**
	 * Set's the Animation duration for the Expandable animation
	 * 
	 * @param duration
	 *            The duration as an integer in MS (duration > 0)
	 * @exception IllegalArgumentException
	 *                if parameter is less than zero
	 */
	public void setAnimationDuration(int duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration is less than zero");
		}

		animationDuration = duration;
	}

	/**
	 * Check's if any position is currently Expanded To collapse the open item @see
	 * collapseLastOpen
	 * 
	 * @return boolean True if there is currently an item expanded, otherwise
	 *         false
	 */
	public boolean isAnyItemExpanded() {
		return (lastOpenPosition != -1) ? true : false;
	}

	public View getChildView(View parent, int id) {
		return parent.findViewById(id);
	}

	public void enableFor(View parent, int position) {
		View more = getChildView(parent, togleButton);
		View itemToolbar = getChildView(parent, viewTarget);
		
		if(_onExpandingViewListener != null){
			_onExpandingViewListener.onViewLoad(itemToolbar, position,_adapter.getItemId(position));
		}
		
		enableFor(more, itemToolbar, position);
	}

	private void enableFor(final View button, final View target,
			final int position) {

		if (target == lastOpen && position != lastOpenPosition) {
			// lastOpen is recycled, so its reference is false
			lastOpen = null;
		}
		if (position == lastOpenPosition) {
			// re reference to the last view
			// so when can animate it when collapsed
			lastOpen = target;
		}

		int height = viewHeights.get(position, -1);
		if (height == -1) {
			target.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);
			viewHeights.put(position, target.getMeasuredHeight());
			updateExpandable(target, position);
		} else {
			updateExpandable(target, position);
		}

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {

				Animation a = target.getAnimation();

				if (a != null && a.hasStarted() && !a.hasEnded()) {

					a.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							view.performClick();
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});

				} else {
					target.setAnimation(null);

					int type = target.getVisibility() == View.VISIBLE ? ExpandingAnimation.COLLAPSE
							: ExpandingAnimation.EXPAND;

					Log.v(TAG, "target visibility is: " + type
							+ "where visible is: " + View.VISIBLE);

					// remember the state
					if (type == ExpandingAnimation.EXPAND) {
						openItems.set(position, true);
					} else {
						openItems.set(position, false);
					}
					// check if we need to collapse a different view
					if (type == ExpandingAnimation.EXPAND) {
						if (lastOpenPosition != -1
								&& lastOpenPosition != position) {
							if (lastOpen != null) {
								animateView(lastOpen,
										ExpandingAnimation.COLLAPSE);
								Log.v(TAG, "I am where I think i should be ");

							}
							openItems.set(lastOpenPosition, false);
						}
						lastOpen = target;
						lastOpenPosition = position;
					} else if (lastOpenPosition == position) {

						lastOpenPosition = -1;
					}
					Log.v(TAG, "i should be animating  ");

					animateView(target, type);
				}
			}
		});
	}

	private void updateExpandable(View target, int position) {

		if (openItems.get(position)) {
			target.setVisibility(View.VISIBLE);
		} else {
			target.setVisibility(View.GONE);
		}
	}

	/**
	 * Performs either COLLAPSE or EXPAND animation on the target view
	 * 
	 * @param target
	 *            the view to animate
	 * @param type
	 *            the animation type, either ExpandCollapseAnimation.COLLAPSE or
	 *            ExpandCollapseAnimation.EXPAND
	 */
	private void animateView(final View target, final int type) {
		Animation anim = new ExpandingAnimation(target, type);

		anim.setDuration(1000);
		target.startAnimation(anim);
	}


	public Parcelable onSaveInstanceState(Parcelable parcelable) {
		Log.v(TAG, "SavedState called");

		SavedState ss = new SavedState(parcelable);
		ss.lastOpenPosition = this.lastOpenPosition;
		ss.openItems = this.openItems;
		return ss;
	}

	public void onRestoreInstanceState(SavedState state) {
		Log.v(TAG, "SavedState called");

		this.lastOpenPosition = state.lastOpenPosition;
		this.openItems = state.openItems;
	}

	/**
	 * Utility methods to read and write a bitset from and to a Parcel
	 */
	private static BitSet readBitSet(Parcel src) {
		int cardinality = src.readInt();
		Log.v(TAG, "SavedState called");

		BitSet set = new BitSet();
		for (int i = 0; i < cardinality; i++) {
			set.set(src.readInt());
		}

		return set;
	}

	private static void writeBitSet(Parcel dest, BitSet set) {
		int nextSetBit = -1;
		Log.v(TAG, "SavedState called");

		dest.writeInt(set.cardinality());

		while ((nextSetBit = set.nextSetBit(nextSetBit + 1)) != -1) {
			dest.writeInt(nextSetBit);
		}
	}

	/**
	 * The actual state class
	 */
	static class SavedState extends View.BaseSavedState {
		public BitSet openItems = null;
		public int lastOpenPosition = -1;

		SavedState(Parcelable superState) {
			super(superState);
			Log.v(TAG, "SavedState called");

		}

		private SavedState(Parcel in) {
			super(in);
			in.writeInt(lastOpenPosition);
			writeBitSet(in, openItems);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			lastOpenPosition = out.readInt();
			openItems = readBitSet(out);
		}

		// required field that makes Parcelables from a Parcel
		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
