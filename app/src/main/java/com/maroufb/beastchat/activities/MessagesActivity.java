package com.maroufb.beastchat.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.maroufb.beastchat.fragments.BaseFragment;
import com.maroufb.beastchat.fragments.MessageFragment;

import java.util.ArrayList;

public class MessagesActivity extends BaseFragmentActivity {

    public static final String EXTRA_FRIEND_DETAILS = "EXTRA_FRIEND_DETAILS";

    @Override
    Fragment createFragment() {
        ArrayList<String> friendDetails = getIntent().getStringArrayListExtra(EXTRA_FRIEND_DETAILS);
        return MessageFragment.newInstance(friendDetails);
    }



    public static Intent newInstance(ArrayList<String> friendDetails, Context context){
        Intent intent = new Intent(context,MessagesActivity.class);
        intent.putStringArrayListExtra(EXTRA_FRIEND_DETAILS,friendDetails);
        return intent;
    }
}
