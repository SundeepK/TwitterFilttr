package com.sun.tweetfiltrr.activity.tasks;

import android.content.Context;

import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.AsyncSmoothProgressBarTask;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AsyncAccessTokenRetriever;

import java.util.concurrent.ExecutionException;

public class TwitterLoginTask extends AsyncSmoothProgressBarTask<String, String, ParcelableUser> {
    private Context _context;

    public TwitterLoginTask(IProgress progressBar_, Context context_){
        super(progressBar_);
        _context = context_;

    }

    @Override
    protected void onPostExecute(ParcelableUser status) {
        super.onPostExecute(status);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ParcelableUser doInBackground(String... params) {
        ParcelableUser user = null;
        try {
            user = new AsyncAccessTokenRetriever(_context)
                    .execute(params[0]).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return user;
    }


}