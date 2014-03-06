package com.sun.tweetfiltrr.fragment.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.customviews.webview.api.ITwitterAuthCallback;
import com.sun.tweetfiltrr.customviews.webview.impl.AuthenticationDetails;
import com.sun.tweetfiltrr.customviews.webview.impl.TwitterAuthView;
import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.fragment.api.ASignInFragment;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AsyncAccessTokenRetriever;
import com.sun.tweetfiltrr.validator.InputValidator;
import com.sun.tweetfiltrr.utils.TwitterConstants;

import javax.inject.Inject;

import twitter4j.auth.AccessToken;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public class OAuthSignInFragment extends ASignInFragment {
    private static final String TAG = OAuthSignInFragment.class.getName();
    private TwitterAuthView _authWebView;
    @Inject
    FriendDao _friendDao;
    private InputValidator _inputValidator;
    private ImageView _appIcon;
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
        final Button backToWebAuthBut = (Button) rootView_.findViewById(R.id.go_back_web_but);

        final Button authenticateBut = (Button) rootView_.findViewById(R.id.authenticate_app_but);
        authenticateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser(OAuthSignInFragment.this);
            }
        });


        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.twitter_logo_blue);
        setCircleDrawable(bm, _appIcon);

        _tokenEditText = (EditText) rootView_.findViewById(R.id.auth_token_edit);
        _tokenEditText.addTextChangedListener(_inputValidator.getEditTextWatcher(_tokenEditText, 1, 200));
        _inputValidator.prepareEditTextView("Token", _tokenEditText);

        _secrectEditText = (EditText) rootView_.findViewById(R.id.auth_token_secret_edit);
        _secrectEditText.addTextChangedListener(_inputValidator.getEditTextWatcher(_secrectEditText, 1, 200));
        _inputValidator.prepareEditTextView("Secrect", _secrectEditText);

        manualAuthBut.setOnClickListener(getManualAuthOnClick(_tokenEditText, _secrectEditText));
        showManualAuthBut.setOnClickListener(showManualAuthView(authenticateBut, manualAuthView));
        backToWebAuthBut.setOnClickListener(hideManualAuthView(authenticateBut, showManualAuthBut, manualAuthView));

    }

    private View.OnClickListener hideManualAuthView(final Button authButton_, final Button manualAuth_, final View manualAuthView_){
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        manualAuthView_.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                };
                authButton_.setVisibility(View.VISIBLE);
                manualAuth_.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(authButton_,"translationX",-800, 0).start();
                ObjectAnimator.ofFloat(manualAuth_,"translationX",-800,0).start();
                ObjectAnimator offAnimation =   ObjectAnimator.ofFloat(manualAuthView_,"translationX",800);
                offAnimation.addListener(animationListener);
                offAnimation.start();
            }
        };
    }

    private View.OnClickListener showManualAuthView(final Button authenticateButton_, final View authView_){
       return new View.OnClickListener() {
            @Override
            public void onClick(final View clickedView_) {
               final Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
                   @Override
                   public void onAnimationStart(Animator animation) {
                   }

                   @Override
                   public void onAnimationEnd(Animator animation) {
                       authenticateButton_.setVisibility(View.GONE);
                       clickedView_.setVisibility(View.GONE);
                   }

                   @Override
                   public void onAnimationCancel(Animator animation) {
                   }

                   @Override
                   public void onAnimationRepeat(Animator animation) {
                   }
               };
                //animations for views coming/off the screen
                authView_.setVisibility(View.VISIBLE);
                ObjectAnimator offAnimation =  ObjectAnimator.ofFloat(clickedView_,"translationX",-800);
                offAnimation.addListener(animationListener);
                offAnimation.start();
                ObjectAnimator.ofFloat(authenticateButton_,"translationX",-800).start();
                ObjectAnimator.ofFloat(authView_,"translationX",800,0).start();
                authView_.clearFocus();
            }
        };

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, _tokenEditText.getEditableText().toString());
        outState.putString(TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, _secrectEditText.getEditableText().toString());
    }


    private View.OnClickListener getManualAuthOnClick(
                                                  final EditText tokenEditText_, final EditText secrectEditText_) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String token = tokenEditText_.getEditableText().toString().trim();
                String secrect = secrectEditText_.getEditableText().toString().trim();

                if (_inputValidator.isEditTextValid(tokenEditText_, 1, 200) &&
                        _inputValidator.isEditTextValid(secrectEditText_, 1, 200)) {
                    final SharedPreferences sharedPreferences =  PreferenceManager
                            .getDefaultSharedPreferences(OAuthSignInFragment.this.getActivity());
                    final AccessToken accessToken = new AccessToken(token, secrect);
                    setAccessTokenInPref(accessToken);
                    Log.v(TAG, "accessToken +" + accessToken.getToken() + " secrect" + accessToken.getTokenSecret());
                    AsyncAccessTokenRetriever task =
                            new AsyncAccessTokenRetriever(_friendDao, OAuthSignInFragment.this,sharedPreferences );
                    task.execute();
                    startLoadAnimation(OAuthSignInFragment.this._appIcon);
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
