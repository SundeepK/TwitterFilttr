package com.sun.tweetfiltrr.customviews.webview.impl;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class AuthenticationDetails{
    private String _consumerKey;
    private String _consumerSecrect;
    private String _callback;

    public Twitter getTwitter() {
        return _twitter;
    }

    private Twitter _twitter;

    public AuthenticationDetails (String consumerKey_,String consumerSecrect, String callback_){
        _consumerKey = consumerKey_;
        _consumerSecrect = consumerSecrect;
        _callback = callback_;
        _twitter = new TwitterFactory().getInstance();
        _twitter.setOAuthConsumer(_consumerKey, _consumerSecrect);
    }

    public AuthenticationDetails (String consumerKey_,String consumerSecrect, String callback_, Twitter twitter_){
        _consumerKey = consumerKey_;
        _consumerSecrect = consumerSecrect;
        _callback = callback_;
        _twitter = twitter_;
    }

    public String getConsumerKey() {
        return _consumerKey;
    }

    public String getConsumerSecrect() {
        return _consumerSecrect;
    }

    public String getCallback() {
        return _callback;
    }

}