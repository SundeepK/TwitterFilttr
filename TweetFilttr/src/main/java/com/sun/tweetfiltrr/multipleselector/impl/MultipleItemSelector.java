package com.sun.tweetfiltrr.multipleselector.impl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ListView;

import com.sun.tweetfiltrr.multipleselector.api.OnTextViewLoad;

import java.util.Collection;
import java.util.List;


public class MultipleItemSelector<T> {
	protected Dialog _dialog;
	protected ListView _list;
	private OnClickListener<T> _onDialogClickListener;
	protected AItemSelector<T> _listAdapter;
	private int _tag;
	public MultipleItemSelector(String title_,Context context_, int resource_, int checkBox_,
			int textView_, List<T> itemList_, OnClickListener<T> onDialogClickListener_) {
		_listAdapter = new MultipleSelectorArrayAdapter<T>(context_, resource_, checkBox_, textView_, itemList_);
		_list = new ListView(context_);
		_list.setAdapter(_listAdapter);
		_dialog = createNewDialog(context_, title_);
		_onDialogClickListener = onDialogClickListener_;
	}
	
	
	public MultipleItemSelector(String title_, Context context_, int resource_, int checkBox_,
			int textView_, List<T> itemList_,  OnClickListener<T> onDialogClickListener_,
			OnTextViewLoad<T> adapterCallback_) {
		_listAdapter = new MultipleSelectorArrayAdapter<T>(context_, resource_, checkBox_, textView_, itemList_, adapterCallback_);
		_onDialogClickListener = onDialogClickListener_;
		_list = new ListView(context_);
		_list.setAdapter(_listAdapter);
		_dialog = createNewDialog(context_, title_);
	}
	
	public MultipleItemSelector(String title_, Context context_, int resource_, int checkBox_,
			int textView_, List<T> itemList_, AItemSelector<T> listAdapter_) {
		_listAdapter = listAdapter_;
		_list = new ListView(context_);
		_list.setAdapter(_listAdapter);
		_dialog = createNewDialog(context_, title_);
	}
	
	protected MultipleItemSelector(String title_, Context context_, ListView list_, List<T> itemList_, AItemSelector<T> listAdapter_) {
		_listAdapter = listAdapter_;
		_list =list_;
		_list.setAdapter(_listAdapter);
		_dialog = createNewDialog(context_, title_);
	}

    protected MultipleItemSelector(AItemSelector<T> listAdapter_){
        _listAdapter = listAdapter_;
    }
	


	public interface OnClickListener<T>{
		
		public void onNegativeClick(DialogInterface dialog, int which);
		
		public void onPositiveClick(Collection<T> selectedItems_, DialogInterface dialog_, int which_);
		
	}
	
	public void showDialog(){
		_dialog.show();
	}
	
	public void hideDialog(){
		_dialog.hide();
	}
	
	public void addToAdapter(Collection<T> items_){
		_listAdapter.addAllToAdapter(items_);
	}
	
	public void clearAdapter(){
		_listAdapter.clearAdapter();
	}
	
	public void remove(T object_){
		_listAdapter.remove(object_);
	}
	
	public void setOnDialogButtonClickListener(OnClickListener<T> onDialogClickListener_){
		_onDialogClickListener = onDialogClickListener_;
	}

	protected Dialog createNewDialog(Context context_, String title_) {
		  AlertDialog.Builder builder = new AlertDialog.Builder(context_)
			.setView(_list).setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {	
					if(_onDialogClickListener != null){
					 _onDialogClickListener.onPositiveClick(_listAdapter.getAllSelectedItems(), dialog, which);
					}
					 
				}
			}).setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog_, int which_) {
					if(_onDialogClickListener != null){
					_onDialogClickListener.onNegativeClick(dialog_, which_);	
					}
				}
			})
			.setTitle(title_);
	
		Dialog	dialog = builder.create();
		return dialog;
	}
	
	public void setTag(int tag_){
		_tag = tag_;
	}
	
	public int getTag(){
		return _tag;
	}
	
}
