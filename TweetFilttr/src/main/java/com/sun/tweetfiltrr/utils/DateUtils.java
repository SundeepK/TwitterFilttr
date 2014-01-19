package com.sun.tweetfiltrr.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sundeep on 17/12/13.
 */
public class DateUtils {

    public static Date getCurrentDate(){
        Calendar calender = Calendar.getInstance();
        return calender.getTime();
    }

    public static Date getPreviousDate(){
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.DATE, -1);
        return calender.getTime();
    }

}
