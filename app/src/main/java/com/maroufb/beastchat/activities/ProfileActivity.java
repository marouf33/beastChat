package com.maroufb.beastchat.activities;

import android.support.v4.app.Fragment;

import com.maroufb.beastchat.fragments.ProfileFragment;

public class ProfileActivity extends BaseFragmentActivity {
    @Override
    Fragment createFragment() {
        return ProfileFragment.newInstance();
    }
}
