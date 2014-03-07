package com.sun.tweetfiltrr.concurrent.api;


/**
 * Created by Sundeep on 12/01/14.
 *
 */
public interface OnAsyncTaskPostExecute<T > {
		
		public void onPreExecute();
        public void onPostExecute(T twitterParcelable);
}
