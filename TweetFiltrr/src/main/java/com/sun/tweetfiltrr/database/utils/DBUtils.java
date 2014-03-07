package com.sun.tweetfiltrr.database.utils;

import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.database.tables.DBColumnName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {

	private static final String TAG = DBUtils.class.getName();

	public static String[] getFullyQualifiedProjections(DBColumnName[] dbColumn_) {
		List<String> projections = new ArrayList<String>();
        for (DBColumnName aDbColumn_ : dbColumn_) {
            if (!aDbColumn_.s().equals(aDbColumn_.tableName())) {
                projections.add(aDbColumn_.p());
                Log.v(TAG, "Col name " + aDbColumn_.p());
            }
        }
		return projections.toArray(new String[projections.size()]);
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
		boolean isEqual;
        for (DBColumnName aDbColumn_ : dbColumn_) {
            isEqual = TextUtils.equals(aDbColumn_.s(), aDbColumn_.tableName());
            if (!isEqual) {
                String prefix = aDbColumn_.p();
                projections.put(prefix, prefix + " AS " + aDbColumn_.a());
            }
        }

		return projections;
	}
	
	public static String[] concatColumns(String[] ...columnNames) {
		int size = 0;
        for (String[] columnName1 : columnNames) {
            size += columnName1.length;
        }
		Log.v(TAG, "Size : " + size);
		String[] concatedCols= new String[size];

		size = 0;
        for (String[] columnName : columnNames) {
            System.arraycopy(columnName, 0, concatedCols, size, columnName.length);
            size += columnName.length;
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
		return excludeCols.toArray(new DBColumnName[excludeCols.size()]);
	}
	
	private static List<DBColumnName> copyAsList(DBColumnName[] arr_){
		return Arrays.asList(arr_);
	}
			
}
