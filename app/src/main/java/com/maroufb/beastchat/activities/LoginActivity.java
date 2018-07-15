package com.maroufb.beastchat.activities;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.maroufb.beastchat.fragments.LoginFragment;

public class LoginActivity extends BaseFragmentActivity {


    @Override
    Fragment createFragment() {
        return LoginFragment.newInstance();
    }


}
