package com.sun.tweetfiltrr.database.utils;

import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.database.tables.DBColumnName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {

	private static final String TAG = DBUtils.class.getName();

	public static String[] getFullyQualifiedProjections(DBColumnName[] dbColumn_) {

		List<String> projections = new ArrayList<String>();
		for (int colNum = 0; colNum < dbColumn_.length  ; colNum++) {
			if (!dbColumn_[colNum].s().equals(dbColumn_[colNum].tableName())) {
				projections.add(dbColumn_[colNum].p());
				Log.v(TAG, "Col name " + dbColumn_[colNum].p());
			}
	}

		String[] stringColumns = projections.toArray(new String[]{});

		
		return stringColumns;
	}


    public static String[] getprojections(DBColumnName[] dbColumn_) {

        String[] stringColumns = new String[dbColumn_.length - 1];
        int col = 0;
        for(DBColumnName dbColumnName : dbColumn_){
            if(!TextUtils.equals(dbColumnName.s(), dbColumnName.tableName())){
                Log.v(TAG, "Col name " + dbColumnName.s());

                stringColumns[col] = dbColumnName.s();
                col++;
            }
        }
        return stringColumns;
    }

	public static Map<String, String> getAliasProjectionMap(DBColumnName[] dbColumn_) {

		Map<String, String> projections = new HashMap<String, String>();
		boolean isEqual = false;
		for (int colNum = 0; colNum < dbColumn_.length ; colNum++) {
				isEqual  = TextUtils.equals(dbColumn_[colNum].s(), dbColumn_[colNum].tableName());
			if (!isEqual) {		
				String prefix = dbColumn_[colNum].p();
				projections.put(prefix,  prefix + " AS " + dbColumn_[colNum].a());
			}
		}

		return projections;
	}
	
	public static String[] concatColumns(String[] ...columnNames) {
		int size = 0;
		for(int i = 0; i < columnNames.length ; i++){
			size+=columnNames[i].length;
		}
		Log.v(TAG, "Size : " + size);
		String[] concatedCols= new String[size];

		size = 0;
		for(int i = 0; i < columnNames.length; i++){
			Log.v(TAG, "contents : " + columnNames[i].toString());
			   System.arraycopy(columnNames[i], 0, concatedCols, size, columnNames[i].length);
			   size+=columnNames[i].length;
		}
		return concatedCols;
	}
	
	/*
	 * Returns an array of DBColumNames excluding columns passed in as parameters,
	 * this can be used to make code more readable. 
	 * 
	 */
	public static DBColumnName[] getDBColExcluding(DBColumnName[] colsToRemoveFrom_, DBColumnName[] colsToExclude_){
		List<DBColumnName> excludeCols = copyAsList(colsToRemoveFrom_);
		List<DBColumnName> cols = copyAsList(colsToExclude_);
		excludeCols.removeAll(cols);
		return excludeCols.toArray(new DBColumnName[]{});
	}
	
	private static List<DBColumnName> copyAsList(DBColumnName[] arr_){
		List<DBColumnName> cols = new ArrayList<DBColumnName>();
		for(DBColumnName col : arr_){
			cols.add(col);
		}
		return cols;
	}
			
}
