package com.sun.tweetfiltrr.validator;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

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

    public void prepareEditTextView(final String default_, final EditText editText_){
        editText_.setTextColor(Color.GRAY);
        editText_.setText(default_);

        editText_.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                EditText editText = (EditText) v;

                if(!hasFocus && TextUtils.isEmpty(editText.getText().toString())){
                    editText.setTextColor(Color.GRAY);
                    editText.setText(default_);
                    return;
                } else if (hasFocus && editText.getText().toString().equals(default_)){
                    editText.setText("");
                    return;
                }
            }
        });
    }


    public boolean isEditTextValid(EditText editTextToValidate_,  int wordCount, int maxLenght_) {
        final Editable editableText = editTextToValidate_.getEditableText();
        return isEditTextValid(editTextToValidate_, editableText, wordCount, maxLenght_);
    }

    public boolean isEditTextValid(EditText editTextToValidate_, Editable editableText_,  int wordCount, int maxLenght_) {
        boolean isValid = false;
        String inputString = editableText_.toString().toLowerCase(Locale.US);
        if(compareWordCount(inputString, wordCount)){
            editTextToValidate_.setError("Must use less than " + wordCount + " keywords");
            isValid = false;
        }else if (checkNullInput(inputString)) {
            editTextToValidate_.setError("Input cannot be empty");
            isValid = false;
        }else if ((inputString.length() > maxLenght_)) {
            editTextToValidate_.setError("Input is too big");
            isValid = false;
        }else {
            editTextToValidate_.setError(null);
            isValid = true;
        }
        return isValid;
    }


    public TextWatcher getEditTextWatcher(final EditText groupName_, final int wordCount, final int maxLenght_){
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                isEditTextValid(groupName_, s , wordCount, maxLenght_);
            }
        };
    }


}
