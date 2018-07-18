package com.maroufb.beastchat.activities;

import android.support.v4.app.Fragment;

import com.maroufb.beastchat.fragments.InboxFragment;

public class InboxActivity extends BaseFragmentActivity {
    @Override
    Fragment createFragment() {
        return InboxFragment.newInstancce();
    }
}
