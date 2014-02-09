package com.sun.tweetfiltrr.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.utils.FontUtils;

/**
 * Created by Sundeep on 07/02/14.
 */
public class TypeFaceEditText extends EditText {
    public TypeFaceEditText(Context context) {
        super(context);
    }

    public TypeFaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);

    }

    public TypeFaceEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);

    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.TypeFaceView);

        int typeface = values.getInt(R.styleable.TypeFaceView_typeface, 0);
        setTypeface(FontUtils.getTypeFace(typeface, context));
    }


}
