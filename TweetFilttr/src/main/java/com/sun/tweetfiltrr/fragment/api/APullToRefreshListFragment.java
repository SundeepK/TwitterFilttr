package com.sun.tweetfiltrr.fragment.api;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sun.tweetfiltrr.R;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Sundeep on 12/01/14.
 */
public abstract class APullToRefreshListFragment extends ATwitterFragment implements OnRefreshListener,
        AdapterView.OnItemClickListener {

    protected PullToRefreshLayout _pullToRefreshView;
    protected ListView _pullToRefreshListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        View rootView = inflater.inflate(R.layout.pull_to_refresh_list_view, container, false);
        _pullToRefreshView = (PullToRefreshLayout) rootView.findViewById(R.id.pulls_refresh_layout);

        ActionBarPullToRefresh.from(activity)
                // Mark All Children as pullable
                .allChildrenArePullable()
                .listener(this)
                .options(Options.create()
                        .scrollDistance(.50f)
                        .minimize()
                        .build())
                .setup(_pullToRefreshView);

        _pullToRefreshListView = (ListView) rootView.findViewById(R.id.refreshable_listview);
        _pullToRefreshListView.setOnItemClickListener(this);
        return rootView;
    }


}
