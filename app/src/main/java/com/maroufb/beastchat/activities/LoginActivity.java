package com.maroufb.beastchat.activities;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.maroufb.beastchat.R;
import com.maroufb.beastchat.fragments.BaseFragment;
import com.maroufb.beastchat.fragments.LoginFragment;

public class LoginActivity extends BaseFragmentActivity {


    @Override
    Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
