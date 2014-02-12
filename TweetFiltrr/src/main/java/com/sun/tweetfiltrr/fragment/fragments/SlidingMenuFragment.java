package com.sun.tweetfiltrr.fragment.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.KeywordGroupScreen;
import com.sun.tweetfiltrr.activity.activities.TwitterUserProfileHome;
import com.sun.tweetfiltrr.fragment.api.ATwitterFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

/**
 * Created by Sundeep on 11/01/14.
 */
public class SlidingMenuFragment extends ATwitterFragment implements
        ListView.OnItemClickListener, ImageTaskListener{

    private ListView _slidingMenuListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sliding_menu_layout, container, false);
        ParcelableUser _currentUser = UserRetrieverUtils.getCurrentFocusedUser(getActivity());

        ImageView profileImage = (ImageView) rootView.findViewById(R.id.profile_image);
        TextView userNameView = (TextView) rootView.findViewById(R.id.user_name);

        String[] names = new String[]{"Profile", "Filter", "Settings"};
        ListAdapter navAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
        _slidingMenuListView = (ListView) rootView.findViewById(R.id.sliding_menu_listview);
        _slidingMenuListView.setAdapter(navAdapter);
        _slidingMenuListView.setOnItemClickListener(this);
        ImageView background = (ImageView) rootView.findViewById(R.id.sliding_menu_background_image);

        UrlImageLoader imageLoader = TwitterUtil.getInstance().getGlobalImageLoader(getActivity());

        ImageLoaderUtils.attemptLoadImage(background,
                imageLoader,
                getCurrentUser().getProfileBackgroundImageUrl(), 1, this);
        ImageLoaderUtils.attemptLoadImage(profileImage,imageLoader,
                getCurrentUser().getProfileImageUrl(), 1, this);
        userNameView.setText(_currentUser.getUserName());
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = null;
        switch (position) {
            case 0:
                i = new Intent(getActivity(), TwitterUserProfileHome.class);
                i.putExtra(TwitterConstants.FRIENDS_BUNDLE, getCurrentUser());
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                break;
            case 1:
                i = new Intent(getActivity(), KeywordGroupScreen.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(getActivity(), SettingsScreen.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    public void preImageLoad(ImageSettings imageSettings) {

    }

    @Override
    public void onImageLoadComplete(Bitmap bitmap, ImageSettings imageSettings) {

    }

    @Override
    public void onImageLoadFail(FailedTaskReason failedTaskReason, ImageSettings imageSettings) {

    }
}
