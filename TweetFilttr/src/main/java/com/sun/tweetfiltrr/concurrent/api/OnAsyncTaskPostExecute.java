package com.sun.tweetfiltrr.concurrent.api;

import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

/**
 * Created by Sundeep on 12/01/14.
 */
public interface OnAsyncTaskPostExecute<T > {
		
		public void onPreExecute();
        public void onPostExecute(T twitterParcelable);
}
