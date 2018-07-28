package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.maroufb.beastchat.views.userFriendViews.UserFriendAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserFriendsFragment extends BaseFragment implements UserFriendAdapter.UserClickedListener {

    public static  UserFriendsFragment newInstance(){return new UserFriendsFragment();}

    @BindView(R.id.fragment_user_friends_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_user_friends_message)
    TextView mTextView;

    LiveFriendServices mLiveFriendServices;
    String mUserEmailString;

    private ValueEventListener mGetAllCurrentUserFriendsListener;
    private DatabaseReference mGetAllCurrentUserFriendsReference;

    private Unbinder mUnbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_friends,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);

        UserFriendAdapter userFriendAdapter = new UserFriendAdapter((BaseFragmentActivity) getActivity(),this);
        mGetAllCurrentUserFriendsReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_USER_FRIENDS).child(Constants.encodeEmail(mUserEmailString));
        mGetAllCurrentUserFriendsListener = mLiveFriendServices.getAllFriends(userFriendAdapter,mTextView,mRecyclerView);
        mGetAllCurrentUserFriendsReference.addValueEventListener(mGetAllCurrentUserFriendsListener);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(userFriendAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if(mGetAllCurrentUserFriendsListener != null){
            mGetAllCurrentUserFriendsReference.removeEventListener(mGetAllCurrentUserFriendsListener);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLiveFriendServices = LiveFriendServices.getInstance();
        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
    }

    @Override
    public void OnUserClicked(User user) {

    }
}
