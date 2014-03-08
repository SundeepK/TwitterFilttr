package com.sun.tweetfiltrr.concurrent.api;


/**
 * Created by Sundeep on 12/01/14.
 *
 */
public interface OnAsyncTaskExecute<T > {
		
		public void onPreExecute();
        public void onPostExecute(T twitterParcelable);
}
