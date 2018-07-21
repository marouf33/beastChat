package com.maroufb.beastchat.activities;

import android.support.v4.app.Fragment;

import com.maroufb.beastchat.fragments.FriendsFragment;

public class FriendsActivity extends BaseFragmentActivity {
    @Override
    Fragment createFragment() {
        return FriendsFragment.newInstance();
    }
}
