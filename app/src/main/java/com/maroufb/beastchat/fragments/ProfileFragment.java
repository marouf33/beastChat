package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.roughike.bottombar.BottomBar;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.profile_bottombar)  BottomBar mBottomBar;

    @BindView(R.id.fragment_profile_cameraPicture)
    ImageView mCameraImage;

    @BindView(R.id.fragment_profile_galleryPicture)
    ImageView mGalleryImage;

    @BindView(R.id.fragment_profile_user_picture)
    ImageView mUserPicture;

    @BindView(R.id.fragment_profile_userEmail)
    TextView mUserEmail;

    @BindView(R.id.fragment_profile_userName)
    TextView mUserName;

    private Unbinder mUnbinder;

    private LiveFriendServices mLiveFriendServices;

    private DatabaseReference mAllFriendRequestReference;
    private ValueEventListener mAllFriendRequesListener;

    private DatabaseReference mUsersNewMessagesReference;
    private ValueEventListener mUsersNewMessagesListener;


    private String mUserEmailString;

    public static ProfileFragment newInstance(){return new ProfileFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mBottomBar.selectTabWithId(R.id.tab_profile);
        setupBottomBar(mBottomBar, 3);

        Picasso.get()
                .load(mSharedPreferences.getString(Constants.USER_PICTURE,""))
                .into(mUserPicture);
        mUserEmail.setText(mUserEmailString);
        mUserName.setText(mSharedPreferences.getString(Constants.USER_NAME,""));

        mAllFriendRequestReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_FRIEND_REQUEST_RECEIVED).child(Constants.encodeEmail(mUserEmailString));
        mAllFriendRequesListener = mLiveFriendServices.getFriendRequestBottom(mBottomBar,R.id.tab_friends);
        mAllFriendRequestReference.addValueEventListener(mAllFriendRequesListener);

        mUsersNewMessagesReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_USER_NEW_MESSAGES).child(Constants.encodeEmail(mUserEmailString));
        mUsersNewMessagesListener = mLiveFriendServices.getAllNewMessages(mBottomBar,R.id.tab_messages);
        mUsersNewMessagesReference.addValueEventListener(mUsersNewMessagesListener);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLiveFriendServices = LiveFriendServices.getInstance();
        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if(mAllFriendRequesListener != null){
            mAllFriendRequestReference.removeEventListener(mAllFriendRequesListener);
        }

        if(mUsersNewMessagesListener != null){
            mUsersNewMessagesReference.removeEventListener(mUsersNewMessagesListener);
        }
    }
}
