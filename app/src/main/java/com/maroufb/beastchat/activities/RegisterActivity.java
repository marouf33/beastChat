package com.maroufb.beastchat.activities;

import android.support.v4.app.Fragment;

import com.maroufb.beastchat.fragments.RegisterFragment;

public class RegisterActivity extends BaseFragmentActivity {

    @Override
    Fragment createFragment() {
        return RegisterFragment.newInstance();
    }
}
