package com.sun.tweetfiltrr.fragment.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.TwitterFilttrUserHome;
import com.sun.tweetfiltrr.activity.adapter.UserTwitterDetails;
import com.sun.tweetfiltrr.animation.ExpandingAnimation;
import com.sun.tweetfiltrr.fragment.api.ATwitterFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends ATwitterFragment {

	private UrlImageLoader  _sicImageLoader;
	private TextView _userName;
	private TextView _twitterName;
	private TextView _description;
	private TextView _location;
	private ListView _tweetsListView;
    private static final String TAG = UserProfileFragment.class.getName();


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_sicImageLoader = TwitterUtil.getInstance().getGlobalImageLoader(getActivity());

	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView =  inflater.inflate(R.layout.user_home_overview_tab, container, false);
        ImageButton expandBut = (ImageButton) rootView.findViewById(R.id.expandable_user_profile_but);
        final ImageView profileImage = (ImageView) rootView.findViewById(R.id.user_profile_imageview);
        final ImageView backgroundImage = (ImageView) rootView.findViewById(R.id.user_profile_background);

        final RelativeLayout containeranium = (RelativeLayout) rootView.findViewById(R.id.main_container);


        rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int _targetHeight = rootView.getMeasuredHeight();

        final Animation anim = new ExpandingAnimation(containeranium, 300, 20);
     //   final Animation anim2 = new ExpandingAnimation(backgroundImage, 200, 20);

        expandBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim.setDuration(2000);
                containeranium.startAnimation(anim);
                containeranium.requestLayout();
//                anim2.setDuration(2000);
//                backgroundImage.startAnimation(anim2);
//                backgroundImage.requestLayout();
            }
        });

        _userName = (TextView) rootView.findViewById(R.id.user_name_textview);
//        _twitterName = (TextView) rootView.findViewById(R.id.user_twittername_textview);
        _description = (TextView) rootView.findViewById(R.id.user_desc_textview);
//        _location = (TextView) rootView.findViewById(R.id.user_location_textview);

        initUserDetailTextView(getCurrentUser());

//        List<UserTwitterDetails> userTwitterDetails = getUserTwitterDetails();
//        _tweetsListView = (ListView) rootView.findViewById(R.id.list);
//        UserHomeTweetArrayAdapter adapter = new UserHomeTweetArrayAdapter(getActivity(), R.layout.user_home_button_list_row, userTwitterDetails);
//        _tweetsListView.setAdapter(adapter);
//        _tweetsListView.setOnItemClickListener(getOnClickForTwitterHome());

        ImageLoaderUtils.attemptLoadImage(profileImage, _sicImageLoader, getCurrentUser().getProfileImageUrl(), 1, null );
        ImageLoaderUtils.attemptLoadImage(backgroundImage, _sicImageLoader, getCurrentUser().getProfileBackgroundImageUrl(), 2, null );



        return rootView;
    }

    private AdapterView.OnItemClickListener getOnClickForTwitterHome(){
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "on item is clicked on the user twitter home");
                Intent intent = new Intent(getActivity(), TwitterFilttrUserHome.class);
                startActivity(intent);
            }
        };
    }

    private List<UserTwitterDetails> getUserTwitterDetails(){
		List<UserTwitterDetails> details = new ArrayList<UserTwitterDetails>();
		details.add(new UserTwitterDetails("New Tweets", 100));
        details.add(new UserTwitterDetails("New Tweets", 100));

        details.add(new UserTwitterDetails("New Tweets", 100));

        details.add(new UserTwitterDetails("New Tweets", 100));


        details.add(new UserTwitterDetails("Tweets", 100));
		details.add(new UserTwitterDetails("Friends", getCurrentUser().getTotalFriendCount()));
		details.add(new UserTwitterDetails("Followers", 100));
		return details;
	}

	private void initUserDetailTextView(ParcelableUser currentUser_){
	 _userName.setText(currentUser_.getUserName()
             + " \n " + '@' + currentUser_.getScreenName()
             + " \n " + currentUser_.getLocation());
	 _description.setText(currentUser_.getDescription());
	}
	
	
}
