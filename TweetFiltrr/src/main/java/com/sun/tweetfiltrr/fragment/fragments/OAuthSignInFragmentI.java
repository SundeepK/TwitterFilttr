package com.sun.tweetfiltrr.fragment.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.ITwitterAuthCallback;
import com.sun.tweetfiltrr.customviews.webview.AuthenticationDetails;
import com.sun.tweetfiltrr.customviews.webview.TwitterAuthView;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.fragment.api.ASignInFragment;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AsyncAccessTokenRetriever;
import com.sun.tweetfiltrr.utils.InputValidator;
import com.sun.tweetfiltrr.utils.TwitterConstants;

import java.util.Locale;

import javax.inject.Inject;

import twitter4j.auth.AccessToken;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public class OAuthSignInFragmentI extends ASignInFragment {
    private static final String TAG = OAuthSignInFragmentI.class.getName();
    private TwitterAuthView _authWebView;
    @Inject
    FriendDao _friendDao;
    private InputValidator _inputValidator;
    private ImageView _appIcon;
    private String _token;
    private String secrect;
    private EditText _tokenEditText;
    private EditText _secrectEditText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            _tokenEditText.setText(savedInstanceState.getString(TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN));
            _secrectEditText.setText(savedInstanceState.getString(TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET));
        }else{
            _inputValidator = new InputValidator();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_screen_web_auth, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    protected void initView(View rootView_){
        _authWebView = (TwitterAuthView) rootView_.findViewById(R.id.twitter_auth_web_view);

        final View manualAuthView = rootView_.findViewById(R.id.manual_auth_view);
        _appIcon = (ImageView) rootView_.findViewById(R.id.app_loading_image_view);
        Button showManualAuthBut = (Button) rootView_.findViewById(R.id.show_manual_authenticate_but);
        Button manualAuthBut = (Button) rootView_.findViewById(R.id.manual_authenticate);

        final Button authenticateBut = (Button) rootView_.findViewById(R.id.authenticate_app_but);
        authenticateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser(OAuthSignInFragmentI.this);
            }
        });


        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.twitter_logo_blue);
        setCircleDrawable(bm, _appIcon);

        _tokenEditText = (EditText) rootView_.findViewById(R.id.auth_token_edit);
        _tokenEditText.addTextChangedListener(getGroupNameTextWatcher(_tokenEditText));
        prepareEditTextView("Token", _tokenEditText);

        _secrectEditText = (EditText) rootView_.findViewById(R.id.auth_token_secret_edit);
        prepareEditTextView("Secrect", _secrectEditText);

        manualAuthBut.setOnClickListener(getManualAuthOnClick(_tokenEditText, _secrectEditText));

        showManualAuthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        Animation translate = new TranslateAnimation(0, -800, 0, 0);
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                authenticateBut.setVisibility(View.GONE);
                                v.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        translate.setDuration(500);
                        translate.setFillAfter(true);
                        v.startAnimation(translate);

                        authenticateBut.startAnimation(translate);
                    }
                });

                Animation translate = new TranslateAnimation(500, 0, 0, 0);
                translate.setDuration(500);
                translate.setFillAfter(true);
                manualAuthView.startAnimation(translate);
                manualAuthView.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, _tokenEditText.getEditableText().toString());
        outState.putString(TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, _secrectEditText.getEditableText().toString());

    }

    private void prepareEditTextView(final String default_, final EditText editText_){
        editText_.setTextColor(Color.GRAY);
        editText_.setText(default_);

        editText_.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                EditText editText = (EditText) v;

                if(!hasFocus && TextUtils.isEmpty(editText.getText().toString())){
                    editText.setTextColor(Color.GRAY);
                    editText.setText(default_);
                    return;
                } else if (hasFocus && editText.getText().toString().equals(default_)){
                    editText.setText("");
                    return;
                }
            }
        });
    }

    private TextWatcher getGroupNameTextWatcher(final EditText groupName_){
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                isEditTextValid(groupName_, s , 1, 200);
            }
        };
    }

    private boolean isEditTextValid(EditText editTextToValidate_,  int wordCount, int maxLenght_) {
        final Editable editableText = editTextToValidate_.getEditableText();
        return isEditTextValid(editTextToValidate_, editableText, wordCount, maxLenght_);
    }

    private boolean isEditTextValid(EditText editTextToValidate_, Editable editableText_,  int wordCount, int maxLenght_) {
        boolean isValid = false;
        String inputString = editableText_.toString().toLowerCase(Locale.US);
        if(_inputValidator.compareWordCount(inputString, wordCount)){
            editTextToValidate_.setError("Must use less than " + wordCount + " keywords");
            isValid = false;
        }else if (_inputValidator.checkNullInput(inputString)) {
            editTextToValidate_.setError("Input cannot be empty");
            isValid = false;
        }else if ((inputString.length() > maxLenght_)) {
            editTextToValidate_.setError("Input is too big");
            isValid = false;
        }else {
            editTextToValidate_.setError(null);
            isValid = true;
        }
        return isValid;
    }


    private View.OnClickListener getManualAuthOnClick(
                                                  final EditText tokenEditText_, final EditText secrectEditText_) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String token = tokenEditText_.getEditableText().toString().trim();
                String secrect = secrectEditText_.getEditableText().toString().trim();

                if (isEditTextValid(tokenEditText_, 1, 200) && isEditTextValid(secrectEditText_, 1, 200)) {
                    final SharedPreferences sharedPreferences =  PreferenceManager
                            .getDefaultSharedPreferences(OAuthSignInFragmentI.this.getActivity());
                    final AccessToken accessToken = new AccessToken(token, secrect);
                    setAccessTokenInPref(accessToken);
                    Log.v(TAG, "accessToken +" + accessToken.getToken() + " secrect" + accessToken.getTokenSecret());
                    AsyncAccessTokenRetriever task =
                            new AsyncAccessTokenRetriever(_friendDao, OAuthSignInFragmentI.this,sharedPreferences );
                    task.execute();
                    startLoadAnimation(OAuthSignInFragmentI.this._appIcon);
                }

            }
        };
    }

    private void setAccessTokenInPref(AccessToken token_){
         final SharedPreferences sharedPreferences =  PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN,
                token_.getToken());
        Log.v(TAG, " token: " + token_.getToken());
        editor.putString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
                token_.getTokenSecret());
        editor.commit();
    }

    @Override
    protected void authenticateUser(ITwitterAuthCallback callback_) {
        _authWebView.setSuccessLis(callback_);
        AuthenticationDetails details = new AuthenticationDetails(TwitterConstants.TWITTER_CONSUMER_KEY,
                TwitterConstants.TWITTER_CONSUMER_SECRET, "https://twitterfiltrr.com");
        _authWebView.setVisibility(View.VISIBLE);
        _authWebView.startTwitterAuthentication(details);
    }


    @Override
    public void onFailTwitterOAuth(Exception e) {
        super.onFailTwitterOAuth(e);
        _authWebView.setVisibility(View.GONE);
        _appIcon.setAnimation(null);
    }

}
