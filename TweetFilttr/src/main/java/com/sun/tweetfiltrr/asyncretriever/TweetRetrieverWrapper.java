package com.sun.tweetfiltrr.asyncretriever;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.tweetprocessor.impl.DateBasedTweetProcessor;
import com.sun.tweetfiltrr.tweetprocessor.impl.KeywordTweetProcessor;
import com.sun.tweetfiltrr.tweetprocessor.impl.MentionsTweetProcessor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TweetRetrieverWrapper {

	private static final String TAG = TweetRetrieverWrapper.class.getName();
	private ExecutorService _executor;
    private ThreadLocal<SimpleDateFormat> _daterFormat;
	public TweetRetrieverWrapper(ExecutorService executor_,ThreadLocal<SimpleDateFormat> daterFormat_){
		_executor = executor_;
        _daterFormat = daterFormat_;
	}

	

	/**
	 * Fires an asynchronous thread to retrieve tweets in the background, and returns the last {@link ParcelableUser}
	 * that was processed. {@link ParcelableUser} can be null if {@link java.util.Collection} passed in is null;
	 * 
	 * @param friendsWithKeywords_
	 * 			{@link java.util.Collection} containing the {@link com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords} that will be used to search for tweets using
	 * 			keywords. Typically friendsWithKeywords_ will have a size of 1.
	 * @param shouldRunOnce_
	 * 			true if only the first entry in the collection should be processed.
     * @return  {@link java.util.Collection} of the {@link java.util.concurrent.Future}
     *         containing the processed {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} with their updated timeLines
	 */
	public  Collection<Future<ParcelableUser>> retrieveKeywordTweets(Collection<ParcelableUserToKeywords> friendsWithKeywords_,
                                                boolean shouldRunOnce_, boolean shouldLookForOldTweets_){
		ParcelableUser lastUser_ = null;
        ITweetProcessor tweetProcessor = getKeywordTweetProcessor();
        Collection<Future<ParcelableUser>> futures = new ArrayList<Future<ParcelableUser>>(friendsWithKeywords_.size());
        for (ParcelableUserToKeywords friend : friendsWithKeywords_) {
			lastUser_ = friend.getFriend();
			Log.v(TAG, " maxID fresh from query is: " + lastUser_.getMaxId());
			Log.v(TAG, " sinceID fresh from query is: " + lastUser_.getSinceId());

			Callable<ParcelableUser> callable =
					new FriendsKeywordTweetRetriever(friend,
                            tweetProcessor,  shouldLookForOldTweets_);
            futures.add(fireAsyncTask(callable));

		} // Skyrim+from:Logan_RTW+since:2013-10-12 OR	// console+from:Logan_RTW+since:2013-10-12
		return futures;
	}

    public Collection<Callable<ParcelableUser>> getCallableRetrieverList(Collection<ParcelableUserToKeywords> friendsWithKeywords_,
                                                                         boolean shouldRunOnce_, boolean shouldLookForOldTweets_){
        Collection<Callable<ParcelableUser>> callables = new ArrayList<Callable<ParcelableUser>>();
        ParcelableUser lastUser_ = null;
        ITweetProcessor tweetProcessor = getDateBasedProcessor();

        for (ParcelableUserToKeywords friend : friendsWithKeywords_) {
            lastUser_ = friend.getFriend();
            Log.v(TAG, " maxID fresh from query is: " + lastUser_.getMaxId());
            Log.v(TAG, " sinceID fresh from query is: " + lastUser_.getSinceId());

            Callable<ParcelableUser> r =
                    new FriendsKeywordTweetRetriever(friend, tweetProcessor,  shouldLookForOldTweets_);
            callables.add(r);

        }
        return callables;
    }



    public Callable<ParcelableUser> getTimeLineRetriever(ParcelableUser user_, boolean shouldRunOnce_,
                                                         boolean shouldLookForOldTweets){

            ITweetProcessor dateBasedProcessor = new DateBasedTweetProcessor(_daterFormat);
            return new TimeLineRetriever(user_, dateBasedProcessor,  shouldLookForOldTweets, shouldRunOnce_);
    }


    /**
     * Fires an asynchronous thread to retrieve tweets in a background thread, and returns the last {@link ParcelableUser}
     * that was processed. {@link ParcelableUser} can be null if {@link java.util.Collection} passed in is null;
     *
     * @param friends_
     * 			{@link java.util.Collection} containing the {@link ParcelableUser} that will be used to retrieve the tweets for.
     * @param shouldRunOnce_
     * 			true if only the first entry in the collection should be processed.
     *
     * @return  {@link java.util.Collection} of the {@link java.util.concurrent.Future}
     *         containing the processed {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} with their updated timeLines
     */
    public Collection<Future<ParcelableUser>> retrieveTweets(Collection<ParcelableUser> friends_,
                                                             boolean shouldRunOnce_, boolean shouldLookForOldTweets_){

        ITweetProcessor tweetProcessor = getDateBasedProcessor();
        Collection<Future<ParcelableUser>> futures = new ArrayList<Future<ParcelableUser>>(friends_.size());
        for (ParcelableUser friend : friends_) {
            Callable<ParcelableUser> r = new TimeLineRetriever(friend,  tweetProcessor,
                    shouldLookForOldTweets_, shouldRunOnce_);
            futures.add(fireAsyncTask(r));
        }
        return futures;
    }

    public Callable<ParcelableUser> getMentionsRetriever(ParcelableUser user_, boolean shouldRunOnce_,
                                                         boolean shouldLookForOldTweets){

        ITweetProcessor mentionsTweetProcessor = new MentionsTweetProcessor(_daterFormat);
        return new MentionsRetriever(user_, mentionsTweetProcessor,  shouldLookForOldTweets);
    }


    /**
     * Fires an asynchronous thread to retrieve tweets in a background thread, and returns the last {@link ParcelableUser}
     * that was processed. {@link ParcelableUser} can be null if {@link java.util.Collection} passed in is null;
     *
     * @param friends_
     * 			{@link java.util.Collection} containing the {@link ParcelableUser} that will be used to retrieve the tweets for.
     * @param shouldRunOnce_
     * 			true if only the first entry in the collection should be processed.
     *
     * @return  {@link java.util.Collection} of the {@link java.util.concurrent.Future}
     *         containing the processed {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} with their updated timeLines
     */
    public Collection<Future<ParcelableUser>> retrieveMentionTweets(Collection<ParcelableUser> friends_,
                                                             boolean shouldRunOnce_, boolean shouldLookForOldTweets_){

        ITweetProcessor tweetProcessor = getMentionsProcessor();
        Collection<Future<ParcelableUser>> futures = new ArrayList<Future<ParcelableUser>>(friends_.size());
        for (ParcelableUser friend : friends_) {
            Callable<ParcelableUser> r = new MentionsRetriever(friend,  tweetProcessor,
                    shouldLookForOldTweets_);
            futures.add(fireAsyncTask(r));
        }
        return futures;
    }

    private ITweetProcessor getMentionsProcessor(){
        return new DateBasedTweetProcessor(_daterFormat);
    }

    private ITweetProcessor getDateBasedProcessor(){
        return new DateBasedTweetProcessor(_daterFormat);
    }

    private ITweetProcessor getKeywordTweetProcessor(){
        return new KeywordTweetProcessor(_daterFormat);
    }

	private Future<ParcelableUser> fireAsyncTask(Callable<ParcelableUser> callableToExecute_){
        return _executor.submit(callableToExecute_);
	}

}
