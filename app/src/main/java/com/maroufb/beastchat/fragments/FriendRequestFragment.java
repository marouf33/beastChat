package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maroufb.beastchat.R;

public class FriendRequestFragment extends BaseFragment {
    public static FriendRequestFragment newInstance(){ return  new FriendRequestFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_request,container,false);
        return rootView;
    }
}
