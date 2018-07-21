package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maroufb.beastchat.R;
import com.roughike.bottombar.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.profile_bottombar)  BottomBar mBottomBar;

    private Unbinder mUnbinder;

    public static ProfileFragment newInstance(){return new ProfileFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mBottomBar.selectTabWithId(R.id.tab_profile);
        setupBottomBar(mBottomBar, 3);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
