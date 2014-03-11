package com.sun.tweetfiltrr.fragment.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.ConversationAdapter;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.concurrent.AsyncUserDBUpdateTask;
import com.sun.tweetfiltrr.concurrent.api.OnAsyncTaskExecute;
import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.TimelineDatabaseUpdater;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.TweetRetrieverFactory;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.ConversationRetrieverFromDB;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.ObjectGraph;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 08/03/14.
 *
 */
public class ConversationFragment extends SherlockFragment implements ITwitterAPICallStatus,
        OnAsyncTaskExecute<Collection<ParcelableUser>> {

    private static final String TAG = ConversationFragment.class.getName();
    private ParcelableUser _currentUser;
    private ConversationAdapter _convoHandler;
    private Collection<IDatabaseUpdater> _dbUpdaters;
    @Inject UrlImageLoader _urlImageLoader;
    @Inject ExecutorService _threadExecutor;
    @Inject TweetRetrieverFactory _tweetRetriver;
    @Inject ConversationRetrieverFromDB _convoRetrieverFromDB;
    @Inject FriendDao _friendDao;
    @Inject TimelineDao _timelineDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ObjectGraph appObjectGraph = ((TweetFiltrrApplication) getActivity().getApplication()).getObjectGraph();
        appObjectGraph.inject(this);
        _currentUser = UserRetrieverUtils.getUserFromBundle(getActivity());
        _dbUpdaters = new ArrayList<IDatabaseUpdater>();
        String[] cols = new String[]{FriendTable.FriendColumn.FRIEND_ID.s(),
                FriendTable.FriendColumn.TWEET_COUNT.s(), FriendTable.FriendColumn.COLUMN_MAXID.s(), FriendTable.FriendColumn.COLUMN_SINCEID.s(),
                FriendTable.FriendColumn.FOLLOWER_COUNT.s() , FriendTable.FriendColumn.FRIEND_NAME.s(), FriendTable.FriendColumn.FRIEND_SCREENNAME.s(), FriendTable.FriendColumn.PROFILE_IMAGE_URL.s(),
                FriendTable.FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(), FriendTable.FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendTable.FriendColumn.DESCRIPTION.s()};
        _dbUpdaters.add(new DatabaseUpdater(_friendDao,cols ));
        _dbUpdaters.add(new TimelineDatabaseUpdater(_timelineDao));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.conversation_layout, container, false);
        final ListView listview = (ListView) rootView.findViewById(android.R.id.list);
        _convoHandler = new ConversationAdapter(getActivity(),new ArrayList<ParcelableUser>(),
                _urlImageLoader,_currentUser);
        listview.setAdapter(_convoHandler);
        loadConversation();
        return rootView;
    }

    private void loadConversation(){
        ParcelableTweet tweet = _currentUser.getUserTimeLine().iterator().next();
        if(tweet.getInReplyToUserId() > 0){
            if(_convoRetrieverFromDB.hasConversationInDB(tweet)){
                Log.v(TAG, "convo found in DB");
                Collection<ParcelableUser> users = _convoRetrieverFromDB.getConversationFromDB(tweet);
                addConvoToAdapter(users);
            }else{
                Log.v(TAG, "network call for convo");
                Callable<Collection<ParcelableUser>> callable = _tweetRetriver.getTweetConvoRetriever(_currentUser,this);
                Future<Collection<ParcelableUser>> convoFuture = _threadExecutor.submit(callable);
                AsyncUserDBUpdateTask<Collection<ParcelableUser>> task =
                        new AsyncUserDBUpdateTask<Collection<ParcelableUser>>(1, TimeUnit.MINUTES,_dbUpdaters,this);
                task.execute(convoFuture);
            }
        }
    }

    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_, ITwitterAPICall apiCallType_) {
    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall apiCallType_) {
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onPostExecute(Collection<ParcelableUser> result_) {
        //add the convo to adapter and refresh
        addConvoToAdapter(result_);
    }

    private void addConvoToAdapter(Collection<ParcelableUser> users_){
        _convoHandler.clear();
        _convoHandler.addAll(users_);
    }
}
