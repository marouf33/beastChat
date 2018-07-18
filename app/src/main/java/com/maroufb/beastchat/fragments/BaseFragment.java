package com.maroufb.beastchat.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.maroufb.beastchat.utils.Constants;

import io.reactivex.disposables.CompositeDisposable;

public class BaseFragment extends Fragment{

    protected CompositeDisposable mCompositeDisposable;

    protected SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        mSharedPreferences = getActivity().getSharedPreferences(Constants.USER_INFO_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeDisposable.clear();
    }
}
