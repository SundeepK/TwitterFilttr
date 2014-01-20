package com.sun.tweetfiltrr.utils;

import android.text.TextUtils;

public class InputValidator {

	public InputValidator(){
		
	}
	
	public boolean checkNullInput(String input_){
		return TextUtils.isEmpty(input_);
	}
	
	public boolean compareWordCount(String input_, int maxWords_){
		String[] words = input_.split("\\s");
		
		return words.length > maxWords_;
		
	}
	
}
