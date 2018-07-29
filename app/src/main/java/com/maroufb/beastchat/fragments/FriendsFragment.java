package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.maroufb.beastchat.views.FriendsViewPagerAdapter;
import com.roughike.bottombar.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FriendsFragment extends BaseFragment {
    @BindView(R.id.friends_bottombar)   BottomBar mBottomBar;
    @BindView(R.id.fragment_friends_tabLayout) TabLayout mTabLayout;
    @BindView(R.id.fragment_friends_viewPager) ViewPager mViewPager;

    private Unbinder mUnbinder;

    private LiveFriendServices mLiveFriendServices;

    private DatabaseReference mAllFriendRequestReference;
    private ValueEventListener mAllFriendRequesListener;

    private String mUserEmailString;

    public static FriendsFragment newInstance(){return  new FriendsFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mBottomBar.selectTabWithId(R.id.tab_friends);
        setupBottomBar(mBottomBar, 2);

        FriendsViewPagerAdapter friendsViewPagerAdapter = new FriendsViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(friendsViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mAllFriendRequestReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_FRIEND_REQUEST_RECEIVED).child(Constants.encodeEmail(mUserEmailString));
        mAllFriendRequesListener = mLiveFriendServices.getFriendRequestBottom(mBottomBar,R.id.tab_friends);
        mAllFriendRequestReference.addValueEventListener(mAllFriendRequesListener);
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
    }
}
