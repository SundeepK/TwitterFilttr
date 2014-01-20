package com.sun.tweetfiltrr.daoflyweigth.impl;

import android.content.ContentResolver;
import android.os.Parcelable;

import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordFriendToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.KeywordToParcelable;
import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.FriendKeywordDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.KeywordGroupDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.dao.UserFollowersDao;
import com.sun.tweetfiltrr.database.dao.UserFriendsDao;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.HashMap;
import java.util.Map;

public class DaoFlyWeightFactory {

	public enum DaoFactory{
		FRIEND_DAO,
		FRIEND_KEYWORD_DAO,
		KEYWORD_GROUP_DAO,
		TIMELINE_DAO,
        USDER_FOLLOWER_DAO,
		USERS_FRIEND_DAO;
	};
	
	private final Map<DaoFactory, IDBDao<? extends IParcelableTwitter>>  _daoCache;
	private static DaoFlyWeightFactory _factory; 
	private ContentResolver _resolver;
	private FriendToParcelable _cursorToFriend;
	private KeywordFriendToParcelable _cursorUserToKeyword;
	private KeywordToParcelable _cusorToKeyword;
	private KeywordToParcelable _keywordToParcelable;
	private TimelineToParcelable _timeLineToParcelable;

	private  DaoFlyWeightFactory(ContentResolver resolver_){
		_daoCache = new HashMap<DaoFactory, IDBDao<? extends IParcelableTwitter>>();
		_resolver = resolver_;
		_cursorToFriend= new FriendToParcelable();
	    _cusorToKeyword = new KeywordToParcelable();
	    _cursorUserToKeyword = new KeywordFriendToParcelable(_cursorToFriend, _cusorToKeyword);
	    _keywordToParcelable = new KeywordToParcelable();
	    _timeLineToParcelable =  new TimelineToParcelable();
	}
	
	public static DaoFlyWeightFactory getInstance(ContentResolver resolver_)
	{
		if(_factory == null)
			_factory = new DaoFlyWeightFactory(resolver_);
		return _factory;
	}


	public IDBDao<? extends IParcelableTwitter> getDao(DaoFactory daoVal_, ParcelableUser user_){
		IDBDao<? extends Parcelable> dao = null;
		switch (daoVal_) {
		case FRIEND_DAO:
			 dao = (FriendDao) _daoCache.get(daoVal_);
			if(dao == null){
                dao = new FriendDao(_resolver,_cursorToFriend);
                _daoCache.put(DaoFactory.FRIEND_DAO, dao);
			}
			return dao;
		case FRIEND_KEYWORD_DAO:
			 dao = _daoCache.get(daoVal_);

			if(dao == null){
				dao = new FriendKeywordDao(_resolver, _cursorUserToKeyword);
				_daoCache.put(DaoFactory.FRIEND_KEYWORD_DAO, dao);
			}
			return dao;
		case KEYWORD_GROUP_DAO:
			 dao = _daoCache.get(daoVal_);

			if(dao == null){
				dao =  new KeywordGroupDao(_resolver, _keywordToParcelable);

				_daoCache.put(DaoFactory.KEYWORD_GROUP_DAO, dao);
			}
			return dao;
		case TIMELINE_DAO:
			 dao = _daoCache.get(daoVal_);

			if(dao == null){
				dao = new TimelineDao(_resolver, _timeLineToParcelable);
				_daoCache.put(DaoFactory.TIMELINE_DAO, dao);
			}
			return dao;

		case USERS_FRIEND_DAO:
			 dao = _daoCache.get(daoVal_);

            if(user_ == null)
                throw new IllegalArgumentException("null ParcelableUser passed in through parameter, FriendDao requires a non-null value");

				dao = new UserFriendsDao(_resolver , _cursorToFriend,user_);

			return dao;

        case USDER_FOLLOWER_DAO:
                dao = _daoCache.get(daoVal_);

                if(user_ == null)
                    throw new IllegalArgumentException("null ParcelableUser passed in through parameter, FriendDao requires a non-null value");

                    dao = new UserFollowersDao(_resolver , _cursorToFriend,user_);

                return dao;

		default:
			throw new IllegalArgumentException("Unable to create DAO since argument supplied is not valid");
		}


	}




		
}
