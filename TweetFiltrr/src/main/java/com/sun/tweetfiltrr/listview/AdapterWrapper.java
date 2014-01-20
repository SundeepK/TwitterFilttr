package com.sun.tweetfiltrr.listview;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public class AdapterWrapper extends BaseAdapter implements WrapperListAdapter{

	protected ListAdapter _adapter;
	
	public AdapterWrapper(ListAdapter adapter_){
		_adapter = adapter_;
	}
	
	@Override
	public int getCount() {
		return _adapter.getCount();
	}

	@Override
	public Object getItem(int position_) {
		return _adapter.getItem(position_);
	}

	@Override
	public long getItemId(int position_) {
		return _adapter.getItemId(position_);
	}

	@Override
	public View getView(int position_, View viewParent_, ViewGroup viewGroup_) {
		return _adapter.getView(position_, viewParent_, viewGroup_);
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return _adapter;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return _adapter.areAllItemsEnabled();
	}

	@Override
	public int getItemViewType(int position) {
		return _adapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return _adapter.getViewTypeCount();
	}

	@Override
	public boolean hasStableIds() {
		return _adapter.hasStableIds();
	}

	@Override
	public boolean isEmpty() {
		return _adapter.isEmpty();
	}

	@Override
	public boolean isEnabled(int position) {
		return _adapter.isEnabled(position);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		_adapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

		_adapter.unregisterDataSetObserver(observer);
	}

}
