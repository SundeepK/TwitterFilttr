package com.sun.tweetfiltrr.fragment.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.mergeadapters.ConversationAdapter;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.ConversationRetriever;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Created by Sundeep on 08/03/14.
 *
 */
public class ConversationFragment extends SherlockFragment {

    @Inject UrlImageLoader _urlImageLoader;
    @Inject ExecutorService _threadExecutor;
    @Inject TimelineDao _friendDao;
    private ParcelableUser _currentUser;
    private Handler _currentHandler = new Handler();
    private ConversationAdapter _convoHandler;

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
            ConversationRetriever convoRetriever = new ConversationRetriever(_currentUser, _friendDao,
                    _convoHandler, _currentHandler);
            _threadExecutor.execute(convoRetriever);
        }
    }
}
