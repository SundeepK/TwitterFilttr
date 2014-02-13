package com.sun.tweetfiltrr.fragment.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.sun.tweetfiltrr.imageprocessor.BlurredImageGenerator;
import com.sun.tweetfiltrr.imageprocessor.IImageProcessor;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sundeep on 11/01/14.
 */
public class SlidingMenuFragment extends ATwitterFragment implements
        ListView.OnItemClickListener, ImageTaskListener {

    private static final String TAG = SlidingMenuFragment.class.getName();
    private ListView _slidingMenuListView;
    private float _currentX = 0;
    private ImageView _blurredBackground;
    private static String BLURRED_IMAGE_PREFIX = "blurred_";
    private IImageProcessor _blurredImageProcessor;


    public void setOpactiy(float i){
        if(i > 6){
            _blurredBackground.setAlpha((i * -2f));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sliding_menu_layout, container, false);
        ParcelableUser _currentUser = UserRetrieverUtils.getCurrentFocusedUser(getActivity());

        ImageView profileImage = (ImageView) rootView.findViewById(R.id.profile_image);
        TextView userNameView = (TextView) rootView.findViewById(R.id.user_name);
        TextView userDesc = (TextView) rootView.findViewById(R.id.user_details);

        String[] names = new String[]{"Profile", "Filter", "Settings"};
        ListAdapter navAdapter = new ArrayAdapter<String>(getActivity(), R.layout.sliding_menu_list_row, names);
        _slidingMenuListView = (ListView) rootView.findViewById(R.id.sliding_menu_listview);
        _slidingMenuListView.setAdapter(navAdapter);
        _slidingMenuListView.setOnItemClickListener(this);
        ImageView background = (ImageView) rootView.findViewById(R.id.sliding_menu_background_image);
        _blurredBackground = (ImageView) rootView.findViewById(R.id.sliding_menu__blurred_background_image);
        _blurredImageProcessor = new BlurredImageGenerator(getActivity());


        UrlImageLoader imageLoader = TwitterUtil.getInstance().getGlobalImageLoader(getActivity());

        ImageLoaderUtils.attemptLoadImage(background,
                imageLoader,
                getCurrentUser().getProfileBackgroundImageUrl(), 1, this);

        ImageLoaderUtils.attemptLoadImage(_blurredBackground,
                imageLoader,
                getCurrentUser().getProfileBackgroundImageUrl(), 2, this);


        ImageLoaderUtils.attemptLoadImage(profileImage, imageLoader,
                getCurrentUser().getProfileImageUrl(), 1, this);
        userNameView.setText(
                _currentUser.getUserName()
                        + "\n"
                        + "@" + _currentUser.getScreenName()
                        + "\n"
                        + getText(_currentUser.getLocation())
        );

        userDesc.setText(
                getText(_currentUser.getDescription())
                        + "\n"
                        + "\n"
                        + "Tweets " + _currentUser.getTotalTweetCount()
                        + "\n"
                        + "Friends " + _currentUser.getTotalFriendCount()
                        + "\n"
                        + "Followers " + _currentUser.getTotalFollowerCount()
        );


//        rootView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_MOVE:
//
//                        if(event.getX() >= SlidingMenuFragment.this._currentX){
//                            _currentX = event.getX();
//                        }
//                        break;
//                }
//
//            return v.onTouchEvent(event);
//            }
//        });

        return rootView;
    }


    private String getText(String text_) {
        if (!TextUtils.isEmpty(text_) && !text_.equals("null")) {
            return "\n" + text_;
        } else {
            return "";
        }
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
        String imageFilePath = TwitterConstants.SIC_SAVE_DIRECTORY+"/"
                +BLURRED_IMAGE_PREFIX+imageSettings.getFinalFileName();
        Bitmap blurredBitmap = tryLoadImage(imageFilePath, bitmap);
        Log.v(TAG, "Image file path is " + imageFilePath);
        _blurredBackground.setImageBitmap(blurredBitmap);
    }

    @Override
    public void onImageLoadFail(FailedTaskReason failedTaskReason, ImageSettings imageSettings) {

    }

    private Bitmap tryLoadImage(String filePath_, Bitmap bitmap) {
        File blurredImage = new File(filePath_);
        Bitmap bmp = null;

        if(!blurredImage.exists()){
            FileOutputStream out = null;
            if (bmp == null) {
                try {
                    Log.v(TAG, "Bitmap null so attempting to generate a new one");
                    bmp = _blurredImageProcessor.processImage(bitmap);
                    out = new FileOutputStream(blurredImage);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    out.flush();

                    return bmp;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(out!=null){
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        bmp = BitmapFactory.decodeFile(filePath_);

//           Bitmap bmp = null;
//         bmp = _blurredImageProcessor.processImage(bitmap);

        return bmp;
    }
}
