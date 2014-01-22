package com.sun.tweetfiltrr.asyncretriever.retrievers;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.asyncretriever.api.ATweetRetiever;
import com.sun.tweetfiltrr.utils.DateUtils;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.Date;
import java.util.Iterator;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class FriendsKeywordTweetRetriever extends ATweetRetiever<ParcelableUser> {
	
	private static final String TAG = FriendsKeywordTweetRetriever.class.getName();
	protected ParcelableUserToKeywords _friendWithKeyword;
	protected final int _tweetCountToRetrieve = 50;
	protected static final String FROM = "from:@";
    private boolean _shouldLookForOldTweetsOnly;

    public FriendsKeywordTweetRetriever(ParcelableUserToKeywords friendWithKeyword_,
                                        ITweetProcessor tweetProcessor_,
                                        boolean shouldLookForOldTweetsOnly_) {
        super(tweetProcessor_);
        _friendWithKeyword = friendWithKeyword_;
        _shouldLookForOldTweetsOnly = shouldLookForOldTweetsOnly_;
    }
	
	
	private void addQueryToken(StringBuilder queryBuilder_, String keyword_, String username_){
		queryBuilder_.append(keyword_);
		queryBuilder_.append("+");
		queryBuilder_.append(FROM);
		queryBuilder_.append(username_);
	}
	
	private String searchQuery(String[] keywords){
		StringBuilder queryBuilder = new StringBuilder();
        ParcelableUser friend = _friendWithKeyword.getFriend();
		String username = friend.getScreenName();
		for(int position = 0; position <  keywords.length; position++){
			
			if(position == keywords.length - 1){
				addQueryToken(queryBuilder, keywords[position], username);
			}else{
				addQueryToken(queryBuilder,keywords[position], username);
				queryBuilder.append(" OR ");
			}
		}
		
		return queryBuilder.toString();		
	}

	@Override
	public ParcelableUser call() {
        ParcelableUser friend = _friendWithKeyword.getFriend();
        long maxID = friend.getMaxId() <= 0  ? 1 :  friend.getMaxId() ;
		long sinceID = friend.getSinceId();

		String[] keywords = _friendWithKeyword.getKeywordGroup().getGroupKeywords().split("\\s");
		Log.v(TAG, "Maxid that may be used: " +  friend.getMaxId());
		Log.v(TAG, "since that may be used: " + friend.getSinceId());	

		String queryS = searchQuery(keywords);
		Log.v(TAG, "Query string passed :" + queryS);
		Query keywordSearchQuery = new Query(queryS);
		keywordSearchQuery.setCount(_tweetCountToRetrieve);
		
		if(_shouldLookForOldTweetsOnly){
			if(maxID > 1){
				Log.v(TAG, "Setting max ID to: " + maxID);
				keywordSearchQuery.setMaxId(maxID);
			}
		}else if(sinceID > 1){
			Log.v(TAG, "Setting since ID to: " + sinceID);
			keywordSearchQuery.setSinceId(sinceID);
		}

		Log.v(TAG, " query object "  +keywordSearchQuery.toString());

		Twitter twitter = TwitterUtil.getInstance().getTwitter();
        retrieveTweets(twitter, keywordSearchQuery);

        int newCount = friend.getNewTweetCount() +friend.getUserTimeLine().size();
        Log.v(TAG, " new tweets count should be "  + newCount);

        friend.setNewTweetCount(newCount);

        return friend;
	}


    private void retrieveTweets(Twitter twitter_, Query keywordSearchQuery_){
        QueryResult timeLine = null;
        Date previousDate = DateUtils.getPreviousDate();
        ParcelableUser friend = _friendWithKeyword.getFriend();
        Log.v(TAG, "Current date minus 1 day :" + previousDate.toString());
        boolean firstRun = false;
        int pageCount = 1;
        do {
            try {

                if(!firstRun){
                    timeLine = twitter_.search(keywordSearchQuery_);
                }else{
                    timeLine = twitter_.search(timeLine.nextQuery());
                }

            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e(TAG, "Error occured while attempting to retrieve user timeline, maybe becuase query limit has been execeeded.");
                Log.i(TAG, "Setting page count to: " + pageCount  + " and inserting into DB");
                setFriendSinceMaxId(friend);
                return ;
            }

            Iterator<twitter4j.Status> it = timeLine.getTweets().iterator();
            //TODO check if we need to do this, i.e. always break after one loop
//            if(!processTimeLine(it, friend, previousDate)){
//                break;
//            }

              pageCount++;
        } while (timeLine.hasNext());
        Log.v(TAG, "reached end of timeline task, with pagenumber: " + pageCount);

    }

}
