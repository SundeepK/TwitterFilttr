package com.sun.tweetfiltrr.multipleselector.impl;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sun.tweetfiltrr.multipleselector.api.MultipleSelector;
import com.sun.tweetfiltrr.multipleselector.api.OnTextViewLoad;

import java.util.Collection;
import java.util.List;

public abstract class AItemSelector<T> extends ArrayAdapter<T> implements
        MultipleSelector<T> {


	public AItemSelector(Context context, int resource,
			List<T> objects) {
		super(context, resource,  objects);
	}


	public void addAllToAdapter(Collection<T> items_) {
		addAll(items_);
		notifyDataSetChanged();
	}

	public void clearAdapter() {
		clear();
		notifyDataSetChanged();
	}

	public void remove(T object_) {
		remove(object_);
		notifyDataSetChanged();
	}


	private void performPostTextViewLoad(OnTextViewLoad<T> callback_,
			TextView view_, T item_) {
		if (callback_ != null) {
			callback_.onPostTextViewLoad(view_, item_);
		} else {
			if (item_ instanceof CharSequence) {
				view_.setText((CharSequence) item_);
			} else {
				view_.setText(item_.toString());
			}
		}
	}

}

