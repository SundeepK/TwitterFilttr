package com.sun.tweetfiltrr.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

public class InputValidator {

	public InputValidator(){
		
	}
	
	public boolean checkNullInput(String input_){
        boolean isValid = false;
        if(input_.trim().length() == 0 ||  TextUtils.isEmpty(input_)){
            isValid = true;
        }
		return isValid;
	}
	
	public boolean compareWordCount(String input_, int maxWords_){
		String[] words = input_.split("\\s");
		return words.length > maxWords_;
	}


	
}
