package com.sun.tweetfiltrr.twitter.twitterretrievers.api;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.callables.MentionsRetrieverCallable;
import com.sun.tweetfiltrr.twitter.callables.TwitterAPICallable;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.ConversationRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.KeywordTweetRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.MentionsRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.TimeLineRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.twitterparameter.TwitterPageParameter;
import com.sun.tweetfiltrr.twitter.twitterretrievers.twitterparameter.TwitterQueryParameter;
import com.sun.tweetfiltrr.twitter.tweetprocessor.impl.KeywordTweetProcessor;
import com.sun.tweetfiltrr.twitter.tweetprocessor.impl.MentionsTweetProcessor;
import com.sun.tweetfiltrr.twitter.tweetprocessor.impl.PlainTweetProcessor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TweetRetrieverFactory {

	private static final String TAG = TweetRetrieverFactory.class.getName();
	private ExecutorService _executor;
    @Inject MentionsTweetProcessor _mentionsTweetProcessor;
    @Inject KeywordTweetProcessor _keywordTweetProcessor;
    @Inject PlainTweetProcessor _plainTweetProcessor;
    @Inject TwitterPageParameter _pageBasedTwitterParam;
    @Inject TwitterQueryParameter _queryBasedTwitterParam;
    @Inject ConversationRetriever _conversationRetriever;

    @Inject
	public TweetRetrieverFactory(ExecutorService executor_){
        Log.v(TAG,"im creating wrapper");
		_executor = executor_;
	}

    public TweetRetrieverFactory(ExecutorService executor_, ThreadLocal<SimpleDateFormat> daterFormat_){
        _executor = executor_;
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
//	public  Collection<Future<ParcelableUser>> retrieveKeywordTweets(Collection<ParcelableUserToKeywords> friendsWithKeywords_,
//                                                boolean shouldRunOnce_, boolean shouldLookForOldTweets_){
//		ParcelableUser lastUser_ = null;
//        ITweetProcessor tweetProcessor = getKeywordTweetProcessor();
//        Collection<Future<ParcelableUser>> futures = new ArrayList<Future<ParcelableUser>>(friendsWithKeywords_.size());
//        for (ParcelableUserToKeywords friend : friendsWithKeywords_) {
//			lastUser_ = friend.getFriend();
//			Log.v(TAG, " maxID fresh from query is: " + lastUser_.getMaxId());
//			Log.v(TAG, " sinceID fresh from query is: " + lastUser_.getSinceId());
//
//			Callable<ParcelableUser> callable =
//					new KeywordTweetRetriever(friend,
//                            tweetProcessor,  shouldLookForOldTweets_);
//            futures.add(fireAsyncTask(callable));
//
//		} // Skyrim+from:Logan_RTW+since:2013-10-12 OR	// console+from:Logan_RTW+since:2013-10-12
//		return futures;
//	}

    public Collection<Callable<Collection<ParcelableUser>>> getKeywordRetriever(Collection<ParcelableUser> friendsWithKeywords_, boolean shouldRunOnce_,
                        boolean shouldLookForOldTweets_, ITwitterAPICallStatus onFailLis_){
        Collection<Callable<Collection<ParcelableUser>>> callables
                = new ArrayList<Callable<Collection<ParcelableUser>>>();
        ITwitterAPICall<Collection<ParcelableUser>> retriever = new KeywordTweetRetriever(_keywordTweetProcessor, _queryBasedTwitterParam,
                shouldRunOnce_, shouldLookForOldTweets_);

        for (ParcelableUser user : friendsWithKeywords_) {
            Callable<Collection<ParcelableUser>> r =
                    new TwitterAPICallable(user, retriever, onFailLis_);
            Log.v(TAG, "creating callable for tweet keyword search for user " + user.toString());
            callables.add(r);

        }
        return callables;
    }



    public Callable<Collection<ParcelableUser>> getTimeLineRetriever(ParcelableUser user_, boolean shouldRunOnce_,
                                                         boolean shouldLookForOldTweets, ITwitterAPICallStatus onFailLis_){

        ITwitterAPICall<Collection<ParcelableUser>> retriever = new TimeLineRetriever(_plainTweetProcessor, _pageBasedTwitterParam,
                    shouldRunOnce_, shouldLookForOldTweets);
            return new TwitterAPICallable(user_,retriever, onFailLis_);
    }

    public Callable<Collection<ParcelableUser>> getTweetConvoRetriever(ParcelableUser user_, ITwitterAPICallStatus onFailLis_){
        return new TwitterAPICallable(user_,_conversationRetriever, onFailLis_);
    }

    /**
     * Fires an asynchronous thread to retrieve tweets in a background thread, and returns the last {@link ParcelableUser}
     * that was processed. {@link ParcelableUser} can be null if {@link java.util.Collection} passed in is null;
     *
     * @param user_
     * 			{@link java.util.Collection} containing the {@link ParcelableUser} that will be used to retrieve the tweets for.
     * @param shouldRunOnce_
     * 			true if only the first entry in the collection should be processed.
     *
     * @return  {@link java.util.Collection} of the {@link java.util.concurrent.Future}
     *         containing the processed {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} with their updated timeLines
     */
//    public Collection<Future<ParcelableUser>> retrieveTweets(Collection<ParcelableUser> friends_,
//                                                             boolean shouldRunOnce_, boolean shouldLookForOldTweets_){
//
//        Collection<Future<ParcelableUser>> futures = new ArrayList<Future<ParcelableUser>>(friends_.size());
//        for (ParcelableUser friend : friends_) {
//            Callable<ParcelableUser> r = getTimeLineRetriever(friend,shouldRunOnce_, shouldLookForOldTweets_ );
//            futures.add(fireAsyncTask(r));
//        }
//        return futures;
//    }

    public Callable<Collection<ParcelableUser>> getMentionsRetriever(ParcelableUser user_, boolean shouldRunOnce_,
                                                         boolean shouldLookForOldTweets_, ITwitterAPICallStatus onFailLis_){
        ITwitterAPICall<Collection<ParcelableUser>> retriever = new MentionsRetriever(_mentionsTweetProcessor, _pageBasedTwitterParam,
                shouldRunOnce_,  shouldLookForOldTweets_);
        return new MentionsRetrieverCallable(user_, retriever,onFailLis_);
    }

//
//    /**
//     * Fires an asynchronous thread to retrieve tweets in a background thread, and returns the last {@link ParcelableUser}
//     * that was processed. {@link ParcelableUser} can be null if {@link java.util.Collection} passed in is null;
//     *
//     * @param friends_
//     * 			{@link java.util.Collection} containing the {@link ParcelableUser} that will be used to retrieve the tweets for.
//     * @param shouldRunOnce_
//     * 			true if only the first entry in the collection should be processed.
//     *
//     * @return  {@link java.util.Collection} of the {@link java.util.concurrent.Future}
//     *         containing the processed {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} with their updated timeLines
//     */
//    public Collection<Future<Collection<ParcelableUser>>> retrieveMentionTweets(Collection<ParcelableUser> friends_,
//                                                             boolean shouldRunOnce_, boolean shouldLookForOldTweets_){
//
//        ITweetProcessor tweetProcessor = getMentionsProcessor();
//        ITwitterRetriever retriever = new TimeLineRetriever(tweetProcessor, shouldLookForOldTweets_);
//        Collection<Future<ParcelableUser>> futures = new ArrayList<Future<ParcelableUser>>(friends_.size());
//        for (ParcelableUser friend : friends_) {
//            Callable<Collection<ParcelableUser>> r = getMentionsRetriever(friend,  shouldRunOnce_, shouldLookForOldTweets_);
//            futures.add(fireAsyncTask(r));
//        }
//        return futures;
//    }


}
