package com.sun.tweetfiltrr.fragment.fragments;

import android.os.Bundle;
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
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.TweetRetrieverFactory;
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

    private ParcelableUser _currentUser;
    private ConversationAdapter _convoHandler;

    @Inject UrlImageLoader _urlImageLoader;
    @Inject ExecutorService _threadExecutor;
    @Inject TimelineDao _friendDao;
    @Inject TweetRetrieverFactory _tweetRetriver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ObjectGraph appObjectGraph = ((TweetFiltrrApplication) getActivity().getApplication()).getObjectGraph();
        appObjectGraph.inject(this);
        _currentUser = UserRetrieverUtils.getUserFromBundle(getActivity());
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
            Callable<Collection<ParcelableUser>> callable = _tweetRetriver.getTweetConvoRetriever(_currentUser,this);
            Future<Collection<ParcelableUser>> convoFuture = _threadExecutor.submit(callable);
            AsyncUserDBUpdateTask<Collection<ParcelableUser>> task = new AsyncUserDBUpdateTask<Collection<ParcelableUser>>(1, TimeUnit.MINUTES,null,this);
            task.execute(convoFuture);
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
        _convoHandler.clear();
        _convoHandler.addAll(result_);
    }
}
