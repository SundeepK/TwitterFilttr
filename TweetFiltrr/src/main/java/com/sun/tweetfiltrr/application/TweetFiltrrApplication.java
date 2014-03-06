package com.sun.tweetfiltrr.application;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
/**
 * Created by Sundeep on 11/02/14.
 */
public class TweetFiltrrApplication   extends Application {


    private ObjectGraph objectGraph;
    //TODO have a look at re-writing this once DI is working for many glue classes
    @Override
    public void onCreate() {
        super.onCreate();
        Object[] modules = getModules().toArray();
        objectGraph = ObjectGraph.create(modules);
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(
                new ApplicationProvider(this),
                new DaoProviderModule(getContentResolver())
        );
    }

    public ObjectGraph getObjectGraph() {
        return this.objectGraph;
    }
}