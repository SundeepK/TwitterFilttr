package com.sun.tweetfiltrr.multipleselector.api;

import android.widget.ListAdapter;

import java.util.Collection;

public interface MultipleSelector<T> extends ListAdapter{

	public  Collection<T> getAllSelectedItems();
	
	public void addAllToAdapter(Collection<T> items_);
	
	public void clearAdapter();
	
	public void remove(T object_);
	
}
